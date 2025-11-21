package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.repository.PerfilRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para ClienteService usando persistência real (BD em memória/configuração do Quarkus test).
 * Valida criação de cliente, atribuição de perfil e persistência de histórico.
 */
@QuarkusTest
public class ClienteServiceIT {

    @Inject
    ClienteService clienteService;

    @Inject
    PerfilRepository perfilRepository;

    @Inject
    EntityManager em;

    @Test
    @Transactional
    public void criar_devePersistirClienteEAtribuirPerfilEHistorico() {
        // criar perfil base
        Perfil perfil = new Perfil();
        perfil.nome = "CONSERVADOR";
        perfil.pontuacao = 10;
        perfil.descricao = "teste conservador";
        perfilRepository.persistPerfil(perfil);

        Cliente c = new Cliente();
        c.nome = "Integração Teste";
        c.email = "integ.test@example.com";
        c.totalInvestido = new BigDecimal("500");
        // deixar frequencia e preferencia nulos para usar defaults

        Cliente criado = clienteService.criar(c);
        assertNotNull(criado.id);
        assertNotNull(criado.perfilInvestimento);
        assertEquals("CONSERVADOR", criado.perfilInvestimento.nome);

        // verificar histórico
        List<ClientePerfilHistorico> resultados = em.createQuery(
                "SELECT h FROM ClientePerfilHistorico h WHERE h.cliente.id = :cid", ClientePerfilHistorico.class)
                .setParameter("cid", criado.id)
                .getResultList();

        assertFalse(resultados.isEmpty(), "Histórico de perfil não foi criado");
        ClientePerfilHistorico h = resultados.get(0);
        assertEquals(criado.id, h.cliente.id);
        assertEquals(criado.perfilInvestimento.id, h.perfilNovo.id);
    }
}
