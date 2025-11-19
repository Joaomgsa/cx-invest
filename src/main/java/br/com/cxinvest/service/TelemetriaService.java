package br.com.cxinvest.service;

import br.com.cxinvest.dto.telemetria.PeriodoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository telemetriaRepository;

    public TelemetriaResponse obterTelemetria(String inicio, String fim, int page, int size) {
        // período default se não informado
        LocalDate inicioEfetivo = (inicio != null && !inicio.isBlank())
                ? LocalDate.parse(inicio)
                : LocalDate.parse("2025-10-01");

        LocalDate fimEfetivo = (fim != null && !fim.isBlank())
                ? LocalDate.parse(fim)
                : LocalDate.parse("2025-10-31");

        OffsetDateTime ini = inicioEfetivo.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = fimEfetivo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<ServicoTelemetriaResponse> servicos = telemetriaRepository
                .listarAgregadoPorPeriodo(ini, end, page, size);

        PeriodoTelemetriaResponse periodo = new PeriodoTelemetriaResponse(
                inicioEfetivo.toString(),
                fimEfetivo.toString()
        );

        return new TelemetriaResponse(servicos, periodo);
    }
}
