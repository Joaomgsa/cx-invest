package br.com.cxinvest.service;


import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import br.com.cxinvest.entity.Perfil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


//TODO: Validar etapas de negócio antes de implementar o serviço
/*

Serviço para aplicar decisão de perfil ao cliente e registrar histórico.
Gravar histórico antes de sobrescrever o perfil para garantir rastreabilidade.
Armazenar metadata em JSON (string) facilita auditoria; pode usar coluna JSONB se DB suportar.
Usar transação para garantir consistência (histórico + atualização do cliente).
Expor endpoint ou listener do motor que chama aplicarDecisaoDePerfil sempre que houver mudança (mesmo para tentativas não aplicadas, se quiser registrar).
 */

@ApplicationScoped
public class ClientePerfilService {

    @Inject
    EntityManager em;


    @Transactional
    public void aplicarDecisaoDePerfil(Cliente cliente, Perfil novoPerfil, String motivo, String metadata) {
        Perfil anterior = cliente.perfilInvestimento;
        // criar registro de histórico antes de alterar
        ClientePerfilHistorico historico = new ClientePerfilHistorico(
                cliente,
                anterior,
                novoPerfil,
                motivo,
                metadata,
                novoPerfil != null ? novoPerfil.pontuacao : null
        );

        // persistir histórico e atualizar cliente dentro da mesma transação
        em.persist(historico);

        // atualizar cliente (assume que cliente já está gerenciado)
        cliente.setPerfil(novoPerfil);
        em.merge(cliente); // opcional se cliente já for gerenciado
    }
}
