package br.com.cxinvest.service;

import br.com.cxinvest.entity.RequestMetric;
import br.com.cxinvest.repository.RequestMetricRepository;
import br.com.cxinvest.dto.telemetria.PeriodoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Serviço responsável por coletar e persistir métricas de requisição (RequestMetric).
 * <p>
 * - Métricas são enfileiradas via {@link #enqueue(RequestMetric)} durante o processamento
 *   das requisições (muito rápido, não bloqueante).
 * - Periodicamente o método {@link #flush()} é executado (agendado) para persistir
 *   em lote as métricas acumuladas, delegando a escrita para o
 *   {@link RequestMetricRepository}.
 * <p>
 * A persistência é executada dentro de uma transação (@Transactional) no método
 * {@link #flush()} para garantir atomicidade do lote.
 */
@ApplicationScoped
public class MetricsService {

    // fila thread-safe com métricas a persistir
    private final Queue<RequestMetric> queue = new ConcurrentLinkedQueue<>();

    @Inject
    RequestMetricRepository requestMetricRepository;

    /**
     * Enfileira uma métrica para persistência posterior.
     * <p>
     * Este método deve ser chamado no contexto da requisição (por exemplo, em um filtro),
     * e deve executar rapidamente: a métrica é apenas adicionada a uma fila concorrente.
     *
     * @param metric métrica de requisição a ser registrada; se nula, a chamada é ignorada
     */
    public void enqueue(RequestMetric metric) {
        // operação rápida; não bloqueia o fluxo da requisição
        if (metric == null) return;
        queue.add(metric);
    }

    /**
     * Persiste em lote todas as métricas que estavam na fila no momento da chamada.
     * <p>
     * - Agendado para executar periodicamente (veja a anotação @Scheduled).
     * - Executado dentro de uma transação (@Transactional) para garantir consistência
     *   do lote.
     * - Coleta todas as métricas atualmente na fila e faz uma chamada única ao
     *   repositório para gravar o lote (eficiente para alto volume).
     *
     * Nota: o método consome a fila ("drain") e, portanto, as métricas removidas
     *       não serão reprocessadas em caso de falha posterior ao drain — a falha
     *       durante a persistência lançará exceção e abortará a transação.
     */
    @Scheduled(every = "5s")
    @Transactional
    public void flush() {
        List<RequestMetric> batch = new ArrayList<>();
        RequestMetric m;
        while ((m = queue.poll()) != null) {
            batch.add(m);
        }

        if (!batch.isEmpty()) {
            requestMetricRepository.salvar(batch);
        }
    }

    /**
     * Obtém telemetria agregada por serviço/caminho no período informado.
     *
     * @param inicio data de início (YYYY-MM-DD). Se nulo/blank, usa 2025-10-01 como default.
     * @param fim data de fim (YYYY-MM-DD). Se nulo/blank, usa 2025-10-31 como default.
     * @param page página (0-based)
     * @param size tamanho da página
     * @return TelemetriaResponse com lista de serviços agregados e período
     */
    public TelemetriaResponse obterTelemetria(String inicio, String fim, int page, int size) {

        LocalDate inicioEfetivo = (inicio != null && !inicio.isBlank())
                ? LocalDate.parse(inicio)
                : LocalDate.parse("2025-10-01");

        LocalDate fimEfetivo = (fim != null && !fim.isBlank())
                ? LocalDate.parse(fim)
                : LocalDate.now(ZoneOffset.UTC);

        OffsetDateTime ini = inicioEfetivo.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = fimEfetivo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<ServicoTelemetriaResponse> servicos = requestMetricRepository
                .listarAgregadoPorPeriodo(ini.toInstant(), end.toInstant(), page, size);

        PeriodoTelemetriaResponse periodo = new PeriodoTelemetriaResponse(
                inicioEfetivo.toString(),
                fimEfetivo.toString()
        );

        return new TelemetriaResponse(servicos, periodo);
    }

}
