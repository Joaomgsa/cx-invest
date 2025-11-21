package br.com.cxinvest.service;

import br.com.cxinvest.dto.perfil.PerfilRiscoResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.repository.ClienteRepository;
import jakarta.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ClienteServiceIntegrationTest {

    @Inject
    ClienteService clienteService;

    @Inject
    ClienteRepository clienteRepository;

    @Test
    void listarTodos_deveRetornarClientes() {
        List<Cliente> todos = clienteService.listarTodos();
        assertNotNull(todos);
        assertFalse(todos.isEmpty(), "Esperava ao menos um cliente no banco de teste");
    }

    @Test
    void buscarPorId_deveRetornarClienteExistente() {
        var opt = clienteService.buscarPorId(1L);
        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().id);
    }

    @Test
    void perfilRiscoCliente_deveRetornarPerfil() {
        PerfilRiscoResponse resp = clienteService.perfilRiscoCliente(1L);
        assertNotNull(resp);
        assertEquals(1L, resp.clienteId());
        assertNotNull(resp.perfil());
    }
}

