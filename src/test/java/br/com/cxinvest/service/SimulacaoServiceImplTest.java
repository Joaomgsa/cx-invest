package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.exception.ApiException;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.repository.SimulacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a implementação de simulação (SimulacaoServiceImpl).
 * Cobre cenários de fluxo feliz, validações de tipo de produto e erros de negócio.
 */
@ExtendWith(MockitoExtension.class)
public class SimulacaoServiceImplTest {

    @Mock
    SimulacaoRepository repository;

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    SimulacaoServiceImpl service;

    Perfil perfil;
    Cliente cliente;
    Produto produto;

    @BeforeEach
    public void setup() {
        perfil = new Perfil();
        perfil.id = 1L;
        perfil.nome = "Conservador";

        cliente = new Cliente();
        cliente.id = 1L;
        cliente.nome = "Joao";
        cliente.email = "joao@example.com";
        cliente.perfilInvestimento = perfil;

        produto = new Produto();
        produto.id = 10L;
        produto.nome = "Prod Test";
        produto.tipo = "CDB";
        produto.rentabilidadeMensal = new BigDecimal("0.01");
        produto.perfilInvestimento = perfil;
    }

    @Test
    public void simular_deveRetornarResultado_quandoRequisicaoValida() {

            when(repository.findProdutosByTipo("CDB")).thenReturn(List.of(produto));
            when(repository.findProdutoById(10L)).thenReturn(Optional.of(produto));
            when(clienteRepository.findByIdOptional(1L)).thenReturn(Optional.of(cliente));
            when(repository.salvarSimulacao(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var req = new SimulacaoRequest(1L, new BigDecimal("1000"), 12, "CDB");
            SimulacaoResponse resp = service.simular(req);

            assertNotNull(resp);
            assertEquals(10L, resp.produtoValidado().id());
            assertEquals(12, resp.resultadoSimulacao().prazoMeses());

            // calcula valor final esperado usando mesma lógica
            BigDecimal valorSim = new BigDecimal("1000");
            BigDecimal taxa = produto.rentabilidadeMensal;
            MathContext mc = MathContext.DECIMAL128;
            BigDecimal esperado = valorSim
                    .multiply(BigDecimal.ONE.add(taxa, mc).pow(12, mc), mc)
                    .setScale(2, RoundingMode.HALF_EVEN);

            assertEquals(esperado, resp.resultadoSimulacao().valorFinal());

            verify(repository, times(1)).salvarSimulacao(any());

    }

    @Test
    public void simular_deveLancarBadRequest_quandoTipoInvalido() {
        var req = new SimulacaoRequest(1L, new BigDecimal("1000"), 12, "INVALID");
        ApiException ex = assertThrows(ApiException.class, () -> service.simular(req));
        assertTrue(ex.getMessage().contains("Tipo de produto inválido"));
    }

    @Test
    public void simular_deveLancarNotFound_quandoNenhumProdutoEncontradoParaTipo() {
        when(repository.findProdutosByTipo("CDB")).thenReturn(List.of());
        var req = new SimulacaoRequest(1L, new BigDecimal("1000"), 12, "CDB");
        ApiException ex = assertThrows(ApiException.class, () -> service.simular(req));
        assertTrue(ex.getMessage().contains("Nenhum produto encontrado"));
    }

    @Test
    public void simular_deveLancarBadRequest_quandoPerfilProdutoDiferenteDoCliente() {
        // produto com perfil diferente
        Perfil outro = new Perfil();
        outro.id = 2L;
        produto.perfilInvestimento = outro;

        when(repository.findProdutosByTipo("CDB")).thenReturn(List.of(produto));
        when(repository.findProdutoById(10L)).thenReturn(Optional.of(produto));
        when(clienteRepository.findByIdOptional(1L)).thenReturn(Optional.of(cliente));

        var req = new SimulacaoRequest(1L, new BigDecimal("1000"), 12, "CDB");
        ApiException ex = assertThrows(ApiException.class, () -> service.simular(req));
        assertEquals("este produto nao é adequado ao perfil do cliente", ex.getMessage());
    }
}
