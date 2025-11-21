package br.com.cxinvest.service;

import br.com.cxinvest.dto.telemetria.ServicoTelemetriaResponse;
import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.repository.RequestMetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest {

    @Mock
    RequestMetricRepository repository;

    @InjectMocks
    MetricsService service;

    @Test
    public void obterTelemetria_deveRetornarDadosAgregados_ePeriodoPadrao() {
        OffsetDateTime ini = OffsetDateTime.of(2025,10,1,0,0,0,0, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.of(2025,11,1,0,0,0,0, ZoneOffset.UTC);

        var s1 = new ServicoTelemetriaResponse("/simulacao", 120, 250);
        var s2 = new ServicoTelemetriaResponse("/perfil-risco", 80, 180);

        when(repository.listarAgregadoPorPeriodo(ini.toInstant(), end.toInstant(), 0, 10)).thenReturn(List.of(s1,s2));

        TelemetriaResponse resp = service.obterTelemetria(null, null, 0, 10);

        assertNotNull(resp);
        assertEquals(2, resp.servicos().size());
        assertEquals("/simulacao", resp.servicos().get(0).nome());
        assertEquals(120, resp.servicos().get(0).quantidadeChamadas());
        assertEquals("2025-10-01", resp.periodo().inicio());
        assertEquals("2025-10-31", resp.periodo().fim());
    }
}

