package br.com.cxinvest.service;

import br.com.cxinvest.dto.TelemetriaResponse;
import br.com.cxinvest.entity.Telemetria;
import br.com.cxinvest.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository telemetriaRepository;

    @Transactional
    public void registrar(String nomeServico, long tempoRespostaMs) {
        try {
            Telemetria telemetria = new Telemetria();
            telemetria.setNomeServico(nomeServico);
            telemetria.setTimestamp(LocalDateTime.now());
            telemetria.setTempoRespostaMs(tempoRespostaMs);
            telemetriaRepository.persist(telemetria);
        } catch (Exception e) {
            // Log but don't fail the request if telemetry fails
            System.err.println("Erro ao registrar telemetria: " + e.getMessage());
        }
    }

    public TelemetriaResponse obterEstatisticas() {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusMonths(1);

        List<Object[]> estatisticas = telemetriaRepository.findEstatisticasPorServico(inicio, fim);

        List<TelemetriaResponse.ServicoTelemetria> servicos = estatisticas.stream()
                .map(e -> {
                    TelemetriaResponse.ServicoTelemetria servico = new TelemetriaResponse.ServicoTelemetria();
                    servico.setNome((String) e[0]);
                    servico.setQuantidadeChamadas(((Number) e[1]).intValue());
                    servico.setMediaTempoRespostaMs(((Number) e[2]).longValue());
                    return servico;
                })
                .collect(Collectors.toList());

        TelemetriaResponse.Periodo periodo = new TelemetriaResponse.Periodo();
        periodo.setInicio(inicio.toLocalDate());
        periodo.setFim(fim.toLocalDate());

        TelemetriaResponse response = new TelemetriaResponse();
        response.setServicos(servicos);
        response.setPeriodo(periodo);

        return response;
    }
}
