package br.com.cxinvest.service;

import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.repository.ProdutoRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    @Mock
    ProdutoRepository repository;

    ProdutoService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ProdutoService(repository);
    }

    @Test
    void criar_devePersistir() {
        Produto p = new Produto();
        p.nome = "CDB Test";
        p.tipo = "CDB";
        p.rentabilidadeMensal = new BigDecimal("0.12");
        p.classeRisco = Produto.ClasseRisco.CONSERVADOR;

        when(repository.persistProduto(any())).thenAnswer(inv -> inv.getArgument(0));

        Produto criado = service.criar(p);

        verify(repository, times(1)).persistProduto(p);
        assertEquals("CDB Test", criado.nome);
        assertEquals(Produto.ClasseRisco.CONSERVADOR, criado.classeRisco);
    }

    @Test
    void atualizar_deveAlterar_quandoExiste() {
        Produto existente = new Produto();
        existente.id = 1L;
        existente.nome = "Old";
        existente.tipo = "CDB";
        existente.rentabilidadeMensal = new BigDecimal("0.05");
        existente.classeRisco = Produto.ClasseRisco.MODERADO;

        Produto atualizado = new Produto();
        atualizado.nome = "New";
        atualizado.tipo = "CDB";
        atualizado.rentabilidadeMensal = new BigDecimal("0.20");
        atualizado.classeRisco = Produto.ClasseRisco.AGRESSIVO;

        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(existente));

        Produto result = service.atualizar(1L, atualizado);

        assertEquals("New", result.nome);
        assertEquals(new BigDecimal("0.20"), result.rentabilidadeMensal);
        assertEquals(Produto.ClasseRisco.AGRESSIVO, result.classeRisco);
    }

    @Test
    void atualizar_deveLancarNotFound_quandoNaoExiste() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.atualizar(99L, new Produto()));
    }

    @Test
    void remover_deveLancarNotFound_quandoNaoExiste() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.remover(99L));
    }
}

