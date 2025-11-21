package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.repository.SimulacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o serviço que agrega simulações por produto e dia.
 * Verifica delegação ao repositório e normalização de parâmetros de paginação.
 */
@ExtendWith(MockitoExtension.class)
public class SimulacaoProdutoDiaServiceTest {

    @Mock
    SimulacaoRepository repository;

    @InjectMocks
    SimulacaoServiceImpl service;

    @Test
    public void produtoDia_deveDelegarARepository_eRetornarLista() {
        var dto = new SimulacaoProdutoDiaResponse("Produto X", LocalDate.now(), 5L, new BigDecimal("123.45"));
        when(repository.produtoDiaInfo(0, 5)).thenReturn(List.of(dto));

        var result = service.produtoDia(0, 5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Produto X", result.get(0).produto());
        verify(repository, times(1)).produtoDiaInfo(0, 5);
    }

    @Test
    public void produtoDia_deveNormalizarParametrosNegativos() {
        var dto = new SimulacaoProdutoDiaResponse("Produto Y", LocalDate.now(), 2L, new BigDecimal("10.00"));
        // repository deve ser chamado com paginação normalizada pelo repository (testa apenas delegação)
        when(repository.produtoDiaInfo(0, 10)).thenReturn(List.of(dto));

        var result = service.produtoDia(-1, -5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).produtoDiaInfo(0, 10);
    }
}
