package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PerfilServiceIntegrationTest {

    @Inject
    PerfilService perfilService;

    @Test
    void listarTodos_deveRetornarPerfis() {
        List<Perfil> perfis = perfilService.listarTodos();
        assertNotNull(perfis);
        assertFalse(perfis.isEmpty());
    }

    @Test
    void definirPerfilCliente_deveRetornarPerfilParaValores() {
        Perfil p = perfilService.definirPerfilCliente(new BigDecimal("1000"), null, null);
        assertNotNull(p);
        assertNotNull(p.nome);
    }
}

