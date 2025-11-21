package br.com.cxinvest.repository;

import br.com.cxinvest.entity.RequestMetric;
import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório para operações de persistência e consulta da entidade {@link RequestMetric}.
 * <p>
 * Fornece métodos para salvar métricas em lote, listar métricas por intervalo de tempo e
 * contar o número de métricas em um período.
 */
@ApplicationScoped
public class RequestMetricRepository implements PanacheRepository<RequestMetric> {

    /**
     * Persiste uma métrica única delegando para o método de batch.
     *
     * @param metric métrica a ser persistida; chamada é ignorada se for null
     */
    public void salvar(RequestMetric metric) {
        if (metric == null) return;
        salvar(List.of(metric));
    }

    /**
     * Persiste uma lista de métricas em lote usando o {@link EntityManager}.
     *
     * O método aplica "flush" e "clear" periodicamente para controlar o tamanho do
     * contexto de persistência e evitar uso excessivo de memória em operações de grande volume.
     *
     * @param metrics lista de métricas; se for nula ou vazia a chamada é ignorada
     */
    public void salvar(List<RequestMetric> metrics) {
        if (metrics == null || metrics.isEmpty()) return;
        EntityManager em = getEntityManager();
        final int batchSize = 50; // tune as needed
        int i = 0;
        for (RequestMetric m : metrics) {
            em.persist(m);
            i++;
            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        // final flush para garantir escrita das entidades restantes
        em.flush();
    }

    /**
     * Lista métricas cujo {@code timestamp} está no intervalo [inicio, fim).
     *
     * @param inicio instante inicial (inclusivo)
     * @param fim instante final (exclusivo)
     * @param page número da página (0-based)
     * @param size tamanho da página
     * @return lista paginada de métricas ordenadas por timestamp
     */
    public List<RequestMetric> listarPorPeriodo(Instant inicio, Instant fim, int page, int size) {
        long iniMillis = inicio.toEpochMilli();
        long fimMillis = fim.toEpochMilli();
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        // compara valores numéricos (milissegundos) contra a coluna que armazena epoch millis
        return find("timestamp >= ?1 and timestamp < ?2 ORDER BY timestamp", iniMillis, fimMillis)
                .page(Page.of(p, s))
                .list();
    }

    /**
     * Conta quantas métricas existem no período [inicio, fim).
     *
     * @param inicio instante inicial (inclusivo)
     * @param fim instante final (exclusivo)
     * @return número de métricas no intervalo
     */
    public long contarPorPeriodo(Instant inicio, Instant fim) {
        long iniMillis = inicio.toEpochMilli();
        long fimMillis = fim.toEpochMilli();
        return count("timestamp >= ?1 and timestamp < ?2", iniMillis, fimMillis);
    }


    //TODO : Ajustar a query para o banco utilizado no projeto, falta o parse do Instant para o formato aceito pelo banco
    /**
     * Agrega métricas por caminho (path) no período fornecido e retorna DTOs
     * com nome (path), quantidade de chamadas e média de tempo de resposta (ms).
     *
     * @param inicio instante inicial (inclusivo)
     * @param fim instante final (exclusivo)
     * @param page página 0-based
     * @param size tamanho da página
     * @return lista de DTOs agregados
     */
    public List<ServicoTelemetriaResponse> listarAgregadoPorPeriodo(Instant inicio, Instant fim, int page, int size) {
        long iniMillis = inicio.toEpochMilli();
        long fimMillis = fim.toEpochMilli();
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        int offset = p * s;

        String sql = "SELECT path, COUNT(*) AS quantidade, AVG(response_time_ms) AS media " +
                "FROM request_metrics " +
                "WHERE timestamp >= :ini AND timestamp < :fim " +
                "GROUP BY path " +
                "ORDER BY quantidade DESC, path";

        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("ini", iniMillis);
        q.setParameter("fim", fimMillis);
        q.setMaxResults(s);
        q.setFirstResult(offset);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        List<ServicoTelemetriaResponse> result = new ArrayList<>();
        for (Object[] r : rows) {
            String nome = r[0] != null ? r[0].toString() : null;
            int quantidade = r[1] instanceof Number ? ((Number) r[1]).intValue() : Integer.parseInt(r[1].toString());
            int media = 0;
            if (r.length > 2 && r[2] != null) {
                media = r[2] instanceof Number ? ((Number) r[2]).intValue()
                        : (int) Math.round(Double.parseDouble(r[2].toString()));
            }
            result.add(new ServicoTelemetriaResponse(nome, quantidade, media));
        }
        return result;
    }
}
