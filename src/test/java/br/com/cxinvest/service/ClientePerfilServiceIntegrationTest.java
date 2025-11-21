package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.Perfil;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ClientePerfilServiceIntegrationTest {

    @Inject
    ClientePerfilService clientePerfilService;

    @Inject
    ClienteService clienteService;

    @Inject
    PerfilService perfilService;

    @Test
    void aplicarDecisaoDePerfil_devePersistirHistoricoSemErros() {
        Cliente cliente = clienteService.buscarPorId(1L).orElseThrow();
        Perfil novo = perfilService.buscarPorId(2L).orElseThrow();
        // Should not throw
        clientePerfilService.aplicarDecisaoDePerfil(cliente, novo, "Teste", null);
        assertEquals(novo.id, cliente.perfilInvestimento.id);
    }
}

