package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.exception.ApiException;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PerfilServiceTest {

    @Mock
    PerfilRepository repository;

    @Mock
    PanacheQuery<Perfil> query;

    PerfilService service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        service = new PerfilService();
        // injetar repository via reflection
        Field f = PerfilService.class.getDeclaredField("repository");
        f.setAccessible(true);
        f.set(service, repository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) closeable.close();
    }

    @Test
    void definirPerfilCliente_deveRetornarConservador_quandoInputsNulos() {
        Perfil expected = new Perfil();
        expected.id = 1L;
        expected.nome = "CONSERVADOR";
        expected.pontuacao = 10;

        when(repository.find(eq("nome"), (Object[]) any())).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.of(expected));

        Perfil result = service.definirPerfilCliente(null, null, null);
        assertNotNull(result);
        assertEquals("CONSERVADOR", result.nome);
        assertEquals(1L, result.id);
    }

    @Test
    void definirPerfilCliente_deveTratarLimites_corretamente() {
        Perfil p1000 = new Perfil();
        p1000.id = 2L;
        p1000.nome = "MODERADO";
        p1000.pontuacao = 50;

        Perfil p3000 = new Perfil();
        p3000.id = 3L;
        p3000.nome = "AGRESSIVO";
        p3000.pontuacao = 90;

        // para total = 1000 deve mapear para MODERADO
        when(repository.find(eq("nome"), (Object[]) any())).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.of(p1000))
                .thenReturn(Optional.of(p3000));
        Perfil r1 = service.definirPerfilCliente(new BigDecimal("1000"), FrequenciaInvestimento.MEDIA, PreferenciaInvestimento.LIQUIDEZ);
        assertEquals("MODERADO", r1.nome);

        // ajustar stubs para 3000 -> AGRESSIVO
        Perfil r2 = service.definirPerfilCliente(new BigDecimal("3000"), FrequenciaInvestimento.ALTA, PreferenciaInvestimento.RENTABILIDADE);
        assertEquals("AGRESSIVO", r2.nome);
    }

    @Test
    void definirPerfilCliente_deveLancarNotFound_quandoPerfilNaoExistir() {
        when(repository.find(anyString(), (Object[]) any())).thenReturn(query);
        when(query.firstResultOptional()).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.definirPerfilCliente(new BigDecimal("5000"), FrequenciaInvestimento.ALTA, PreferenciaInvestimento.RENTABILIDADE));
    }
}
