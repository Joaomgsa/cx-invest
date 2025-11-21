package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.repository.SimulacaoRepository;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.repository.ProdutoRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;


import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class SimulacaoServiceIntegrationTest {

    @Inject
    SimulacaoService simulacaoService;

    @InjectMock
    SimulacaoRepository simulacaoRepository;

    @InjectMock
    ClienteRepository clienteRepository;

    @InjectMock
    ProdutoRepository produtoRepository;

    @Test
    void simular_deveRetornarRespostaParaRequisicaoValida() {
        // arrange: mock repositories so product and client profiles match
        Perfil perfil = new Perfil();
        perfil.id = 1L;
        perfil.nome = "CONSERVADOR";

        Produto produto = new Produto();
        produto.id = 1L;
        produto.nome = "CDB Test";
        produto.tipo = "CDB";
        produto.rentabilidadeMensal = new BigDecimal("0.01");
        produto.perfilInvestimento = perfil;

        Cliente cliente = new Cliente();
        cliente.id = 1L;
        cliente.nome = "Teste Cliente";
        cliente.perfilInvestimento = perfil;

        when(simulacaoRepository.findProdutosByTipo("CDB")).thenReturn(List.of(produto));
        // service also calls simulacaoRepository.findProdutoById in validarPerfilProduto -> stub it
        when(simulacaoRepository.findProdutoById(1L)).thenReturn(Optional.of(produto));
        when(clienteRepository.findByIdOptional(1L)).thenReturn(Optional.of(cliente));
        // produtoRepository is used by service/validation paths that load by id -> mock it
        when(produtoRepository.findByIdOptional(1L)).thenReturn(Optional.of(produto));

        SimulacaoRequest req = new SimulacaoRequest(1L, new BigDecimal("1000.00"), 6, "CDB");

        // act
        SimulacaoResponse resp = simulacaoService.simular(req);

        // assert
        assertNotNull(resp);
        assertNotNull(resp.resultadoSimulacao());
        assertNotNull(resp.produtoValidado());
    }
}
