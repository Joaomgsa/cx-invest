package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.exception.ApiException;
import br.com.cxinvest.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    ClienteRepository repository;

    @InjectMocks
    ClienteService service;

    @BeforeEach
    void setup() {
    }

    @Test
    void remover_deveMarcarComoInativo_quandoClienteExistir() {
        Cliente c = new Cliente();
        c.id = 42L;
        c.nome = "Teste";
        c.email = "t@ex.com";
        c.status = 'A';

        when(repository.findById(42L)).thenReturn(c);

        service.remover(42L);

        assertEquals('I', c.status);
        verify(repository, times(1)).persist(c);
    }

    @Test
    void remover_deveLancar404_quandoClienteNaoExistir() {
        when(repository.findById(99L)).thenReturn(null);

        ApiException ex = assertThrows(ApiException.class, () -> service.remover(99L));
        assertTrue(ex.getMessage().contains("Cliente não encontrado"));
        verify(repository, never()).persist(any(Cliente.class));
    }
}
