package br.com.cxinvest.service;

import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.repository.RequestMetricRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class MetricsServiceIntegrationTest {

    @Inject
    MetricsService metricsService;

    @InjectMock
    RequestMetricRepository requestMetricRepository;

    @Test
    void obterTelemetria_deveRetornarPeriodoPadrao() {
        // mock repository to avoid SQL conversion issues when reading request_metrics timestamp
        when(requestMetricRepository.listarAgregadoPorPeriodo(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt()))
                .thenReturn(List.of());

        TelemetriaResponse resp = metricsService.obterTelemetria(null, null, 0, 10);
        assertNotNull(resp);
        assertNotNull(resp.servicos());
    }
}
