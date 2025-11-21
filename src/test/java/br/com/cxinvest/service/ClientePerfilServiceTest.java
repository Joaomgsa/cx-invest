package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.exception.ApiException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ClientePerfilService.
 * Verifica comportamento de persistência do histórico e atualização do cliente
 * em cenário de sucesso e condições de falha (entrada nula e falha do EntityManager).
 */
public class ClientePerfilServiceTest {

    @Mock
    EntityManager em;

    ClientePerfilService service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        service = new ClientePerfilService();
        // injetar EntityManager via reflection
        Field emField = ClientePerfilService.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(service, em);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    /**
     * Cenário de sucesso: garante que é persistido um ClientePerfilHistorico
     * com os dados corretos e que o cliente é atualizado com o novo perfil.
     */
    @Test
    void aplicarDecisaoDePerfil_devePersistirHistoricoEAtualizarCliente() {
        Cliente cliente = new Cliente();
        cliente.id = 1L;
        cliente.nome = "João";

        Perfil anterior = new Perfil();
        anterior.id = 1L;
        anterior.nome = "CONSERVADOR";
        anterior.pontuacao = 10;
        cliente.perfilInvestimento = anterior;

        Perfil novo = new Perfil();
        novo.id = 2L;
        novo.nome = "MODERADO";
        novo.pontuacao = 50;

        String motivo = "Reavaliação anual";
        String metadados = "auto-calculo";

        // executar
        service.aplicarDecisaoDePerfil(cliente, novo, motivo, metadados);

        // verificar que persistiu historico com os valores corretos
        verify(em, times(1)).persist(argThat(obj -> {
            if (!(obj instanceof ClientePerfilHistorico h)) return false;
            return h.cliente == cliente
                    && h.perfilAnterior == anterior
                    && h.perfilNovo == novo
                    && motivo.equals(h.motivo)
                    && metadados.equals(h.metadados)
                    && Objects.equals(novo.pontuacao, h.pontuacaoNoMomento);
        }));

        // verificar que atualizou o cliente
        verify(em, times(1)).merge(cliente);
        assertSame(novo, cliente.perfilInvestimento);
    }

    /**
     * Cenário de erro: cliente nulo deve lançar ApiException(400) e
     * não interagir com o EntityManager.
     */
    @Test
    void aplicarDecisaoDePerfil_clienteNulo_deveLancarNPE() {
        Perfil novo = new Perfil();
        novo.id = 1L;
        novo.nome = "CONSERVADOR";
        novo.pontuacao = 10;

        ApiException ex = assertThrows(ApiException.class, () -> service.aplicarDecisaoDePerfil(null, novo, "m", null));
        assertTrue(ex.getMessage().contains("Cliente é obrigatório"));
        verifyNoInteractions(em);
    }

    /**
     * Cenário de erro: quando o EntityManager.persist lançar, a exceção
     * é propagada e merge não é chamado.
     */
    @Test
    void aplicarDecisaoDePerfil_persistFalha_devePropagarExcecao() {
        Cliente cliente = new Cliente();
        cliente.id = 1L;
        cliente.nome = "Maria";
        cliente.perfilInvestimento = null;

        Perfil novo = new Perfil();
        novo.id = 3L;
        novo.nome = "AGRESSIVO";
        novo.pontuacao = 90;

        doThrow(new RuntimeException("DB error")).when(em).persist(any());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.aplicarDecisaoDePerfil(cliente, novo, "motivo", "meta"));

        assertTrue(ex.getMessage().contains("Falha ao aplicar decisão de perfil"));
        assertTrue(ex.getMessage().contains("DB error"));
        // merge não deve ser chamado quando persist lançar
        verify(em, never()).merge(any());
    }
}
