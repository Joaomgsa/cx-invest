package br.com.cxinvest.service;

import br.com.cxinvest.dto.telemetria.PeriodoResponse;
import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.entity.TelemetriaEvento;
import br.com.cxinvest.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository repository;

    // método que busca eventos no período (opcional) e agrega por serviço, com paginação
    public TelemetriaResponse obterTelemetria(String inicioStr, String fimStr, int page, int size) {
        OffsetDateTime inicio = null;
        OffsetDateTime fim = null;
        try {
            if (inicioStr != null && !inicioStr.isBlank()) {
                LocalDate ld = LocalDate.parse(inicioStr);
                inicio = ld.atStartOfDay().atOffset(ZoneOffset.UTC);
            }
            if (fimStr != null && !fimStr.isBlank()) {
                LocalDate ld2 = LocalDate.parse(fimStr);
                fim = ld2.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
            }
        } catch (Exception e) {
            throw new BadRequestException("Formato de data inválido. Use YYYY-MM-DD");
        }

        // usar agregação no banco para obter lista paginada de serviços
        List<ServicoTelemetriaResponse> pageList = repository.listarAgregadoPorPeriodo(inicio, fim, page, size);

        // determinar periodo para resposta: preferir valores fornecidos, senão inferir a partir dos eventos
        String periodoInicio = inicioStr != null && !inicioStr.isBlank() ? inicioStr :
                repository.listarPorPeriodo(inicio, fim).stream().map(e -> e.dataEvento.toLocalDate()).min(LocalDate::compareTo).map(LocalDate::toString).orElse(null);
        String periodoFim = fimStr != null && !fimStr.isBlank() ? fimStr :
                repository.listarPorPeriodo(inicio, fim).stream().map(e -> e.dataEvento.toLocalDate()).max(LocalDate::compareTo).map(LocalDate::toString).orElse(null);

        PeriodoResponse periodo = new PeriodoResponse(periodoInicio, periodoFim);
        return new TelemetriaResponse(pageList, periodo);
    }

    // compatibilidade com método antigo
    public TelemetriaResponse obterTelemetria() {
        return obterTelemetria(null, null, 0, Integer.MAX_VALUE);
    }
}
