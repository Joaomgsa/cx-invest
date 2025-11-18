package br.com.cxinvest.repository;

import br.com.cxinvest.entity.TelemetriaEvento;
import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<TelemetriaEvento> {

    // Retorna eventos de telemetria no período (inclusive). Se inicio ou fim for null, ajusta a condição.
    public List<TelemetriaEvento> listarPorPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        if (inicio == null && fim == null) {
            return listAll();
        }
        if (inicio == null) {
            return list("dataEvento <= ?1", fim);
        }
        if (fim == null) {
            return list("dataEvento >= ?1", inicio);
        }
        return list("dataEvento between ?1 and ?2", inicio, fim);
    }

    // Agregação no banco: retorna nome, quantidadeChamadas, mediaTempoRespostaMs com paginação
    public List<ServicoTelemetriaResponse> listarAgregadoPorPeriodo(OffsetDateTime inicio, OffsetDateTime fim, int page, int size) {
        String base = "SELECT servico, COUNT(*) AS cnt, AVG(tempo_resposta_ms) AS avg FROM tb_telemetria_events";

        List<Object> params = new ArrayList<>();

        // montar cláusulas usando Optional e Stream para reduzir condicionais explícitas
        var conds = java.util.stream.Stream.of(
                java.util.Optional.ofNullable(inicio).map(i -> {
                    params.add(i.toString());
                    return "data_evento >= ?";
                }).orElse(null),
                java.util.Optional.ofNullable(fim).map(f -> {
                    params.add(f.toString());
                    return "data_evento <= ?";
                }).orElse(null)
        ).filter(java.util.Objects::nonNull).toList();

        StringBuilder sql = new StringBuilder(base);
        if (!conds.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conds));
        }

        sql.append(" GROUP BY servico ORDER BY servico");
        sql.append(" LIMIT ? OFFSET ?");

        int p = Math.max(0, page);
        int s = size <= 0 ? 10 : size;
        params.add(s);
        params.add(p * s);

        var query = getEntityManager().createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<ServicoTelemetriaResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            String nome = (String) row[0];
            Number cntN = (Number) row[1];
            Number avgN = (Number) row[2];
            int cnt = cntN == null ? 0 : cntN.intValue();
            int avg = avgN == null ? 0 : (int) Math.round(avgN.doubleValue());
            result.add(new ServicoTelemetriaResponse(nome, cnt, avg));
        }
        return result;
    }
}
