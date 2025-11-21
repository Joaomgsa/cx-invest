package br.com.cxinvest.service;

import br.com.cxinvest.dto.produto.ProdutoRecomendadoResponse;
import br.com.cxinvest.entity.Produto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProdutoServiceIntegrationTest {

    @Inject
    ProdutoService produtoService;

    @Test
    void listarTodos_deveRetornarProdutos() {
        List<Produto> todos = produtoService.listarTodos();
        assertNotNull(todos);
        assertFalse(todos.isEmpty(), "Esperava ao menos um produto no banco de teste");
    }

    @Test
    void produtosRecomendadosPerfilNome_deveRetornarListaParaPerfilExistente() {
        List<ProdutoRecomendadoResponse> lista = produtoService.produtosRecomendadosPerfilNome("CONSERVADOR");
        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Esperava produtos recomendados para perfil CONSERVADOR");
    }

    @Test
    void buscarPorId_deveRetornarQuandoExistir() {
        var opt = produtoService.buscarPorId(1L);
        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().id);
    }
}

