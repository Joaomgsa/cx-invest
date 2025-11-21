package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.exception.ApiException;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.repository.ProdutoRepository;
import br.com.cxinvest.dto.produto.Risco;
import br.com.cxinvest.dto.produto.ProdutoRecomendadoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para as operações de CRUD e validação de regras de negócio
 * do serviço de produtos (ProdutoService).
 */
public class ProdutoServiceTest {

    @Mock
    ProdutoRepository repository;

    @Mock
    PerfilRepository perfilRepository;

    @InjectMocks
    ProdutoService service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void criar_devePersistir() {
        Produto p = new Produto();
        p.nome = "CDB Test";
        p.tipo = "CDB";
        p.rentabilidadeMensal = new BigDecimal("0.12");
        Perfil perfil = new Perfil();
        perfil.id = 1L;
        perfil.nome = "CONSERVADOR";
        perfil.pontuacao = 10;
        p.perfilInvestimento = perfil;

        when(perfilRepository.findByIdOptional(1L)).thenReturn(Optional.of(perfil));
        when(repository.persistProduto(any())).thenAnswer(inv -> inv.getArgument(0));

        Produto criado = service.criar(p);

        verify(repository, times(1)).persistProduto(p);
        assertEquals("CDB Test", criado.nome);
        assertNotNull(criado.perfilInvestimento);
        assertEquals(1L, criado.perfilInvestimento.id);
    }

    @Test
    void atualizar_deveAlterar_quandoExiste() {
        Produto existente = new Produto();
        existente.id = 1L;
        existente.nome = "Old";
        existente.tipo = "CDB";
        existente.rentabilidadeMensal = new BigDecimal("0.05");
        Perfil perfilExistente = new Perfil();
        perfilExistente.id = 2L;
        perfilExistente.nome = "MODERADO";
        perfilExistente.pontuacao = 50;
        existente.perfilInvestimento = perfilExistente;

        Produto atualizado = new Produto();
        atualizado.nome = "New";
        atualizado.tipo = "CDB";
        atualizado.rentabilidadeMensal = new BigDecimal("0.20");
        Perfil perfilNovo = new Perfil();
        perfilNovo.id = 3L;
        perfilNovo.nome = "AGRESSIVO";
        perfilNovo.pontuacao = 90;
        atualizado.perfilInvestimento = perfilNovo;

        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(existente));
        when(perfilRepository.findByIdOptional(3L)).thenReturn(Optional.of(perfilNovo));

        Produto result = service.atualizar(1L, atualizado);

        assertEquals("New", result.nome);
        assertEquals(new BigDecimal("0.20"), result.rentabilidadeMensal);
        assertNotNull(result.perfilInvestimento);
        assertEquals(3L, result.perfilInvestimento.id);
    }

    @Test
    void atualizar_deveLancarNotFound_quandoNaoExiste() {
        Long idInexistente = 123L;
        when(repository.findByIdOptional(idInexistente)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.atualizar(idInexistente, new Produto()));
    }


    @Test
    void remover_deveLancarNotFound_quandoNaoExiste() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.remover(99L));
    }

    // Novo teste: perfil inexistente -> ApiException 404
    @Test
    void produtosRecomendadosPerfilNome_deveLancar404_quandoPerfilNaoExiste() {
        String perfilNome = "INEXISTENTE";
        when(perfilRepository.findByNomeOptional(perfilNome)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.produtosRecomendadosPerfilNome(perfilNome));
        assertEquals(404, ex.getHttpCode());
        assertTrue(ex.getMessage().contains(perfilNome));
    }

    // Novo teste: perfil existente -> retorna lista de produtos
    @Test
    void produtosRecomendadosPerfilNome_deveRetornarProdutos_quandoPerfilExiste() {
        String perfilNome = "CONSERVADOR";
        Perfil perfil = new Perfil();
        perfil.id = 1L;
        perfil.nome = perfilNome;

        Produto p1 = new Produto();
        p1.id = 10L;
        p1.nome = "Produto A";
        p1.tipo = "Fundo";

        p1.perfilInvestimento = perfil;

        when(perfilRepository.findByNomeOptional(perfilNome)).thenReturn(Optional.of(perfil));
        when(repository.listarProdutosPorNomePerfil(perfilNome)).thenReturn(Optional.of(List.of(p1)));

        var resp = service.produtosRecomendadosPerfilNome(perfilNome);

        assertNotNull(resp);
        assertEquals(1, resp.size());
        var first = resp.stream().findFirst().orElseThrow();
        assertEquals(p1.nome, first.nome());
        assertEquals(p1.id, first.id());
        assertEquals(perfil.id, first.perfilId());
        assertEquals(Risco.BAIXO, first.risco());
    }
}
