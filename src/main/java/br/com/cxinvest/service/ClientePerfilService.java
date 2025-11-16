package br.com.cxinvest.service;


import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import br.com.cxinvest.entity.Perfil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


/**
 * Serviço responsável por operações relacionadas à decisão de perfil de investimento
 * de um cliente. Faz a persistência do histórico de alteração de perfil e atualiza
 * o perfil associado ao cliente.
 */
@ApplicationScoped
public class ClientePerfilService {

    @Inject
    EntityManager em;


    /**
     * Aplica uma nova decisão de perfil para o cliente.
     *
     * Efeitos:
     * - Persiste um registro de ClientePerfilHistorico contendo o perfil anterior,
     *   o novo perfil, motivo, metadata e a pontuação aplicada.
     * - Atualiza (merge) a entidade Cliente com o novo perfil.
     *
     * Observações:
     * - Lança NullPointerException se cliente for nulo.
     * - Aceita novoPerfil nulo (por exemplo, para remoção do perfil); nesse caso
     *   a pontuação armazenada no histórico será null.
     *
     * @param cliente cliente cujo perfil será alterado (não pode ser null)
     * @param novoPerfil novo perfil a ser aplicado (pode ser null para remoção)
     * @param motivo texto explicando a razão da mudança
     * @param metadata dados adicionais (opcional) relacionados à decisão
     */
    @Transactional
    public void aplicarDecisaoDePerfil(Cliente cliente, Perfil novoPerfil, String motivo, String metadata) {
        Perfil anterior = cliente.perfilInvestimento;
        ClientePerfilHistorico historico = new ClientePerfilHistorico(
                cliente,
                anterior,
                novoPerfil,
                motivo,
                metadata,
                novoPerfil != null ? novoPerfil.pontuacao : null
        );
        em.persist(historico);

        cliente.setPerfil(novoPerfil);
        em.merge(cliente);
    }
}
