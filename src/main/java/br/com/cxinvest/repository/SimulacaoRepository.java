package br.com.cxinvest.repository;

import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Repositório responsável por consultas necessárias ao fluxo de simulação.
 *
 * Notas:
 * - métodos focados apenas em recuperar dados; lógica de negócio permanece no serviço.
 */
@ApplicationScoped
public class SimulacaoRepository {

    @Inject
    EntityManager em;

    /**
     * Recupera produtos pelo tipo informado.
     * @param tipo tipo do produto (ex.: "CDB", "LCI")
     * @return lista de produtos que casam com o tipo (pode ser vazia)
     */
    public List<Produto> findProdutosByTipo(String tipo) {
        if (tipo == null) return List.of();
        TypedQuery<Produto> q = em.createQuery("SELECT p FROM Produto p WHERE p.tipo = :tipo", Produto.class);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    /**
     * Recupera produtos que estejam associados a um perfil específico e, opcionalmente, por tipo.
     * @param perfilId id do perfil
     * @param tipo tipo do produto (se nulo, ignora essa condição)
     * @return lista de produtos
     */
    public List<Produto> findProdutosByPerfilAndTipo(Long perfilId, String tipo) {
        String jpql = "SELECT p FROM Produto p WHERE p.perfilInvestimento.id = :pid" + (tipo != null ? " AND p.tipo = :tipo" : "");
        TypedQuery<Produto> q = em.createQuery(jpql, Produto.class);
        q.setParameter("pid", perfilId);
        if (tipo != null) q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    /**
     * Recupera um produto por id.
     * @param id identificador do produto
     * @return Optional contendo o produto quando encontrado
     */
    public Optional<Produto> findProdutoById(Long id) {
        return Optional.ofNullable(em.find(Produto.class, id));
    }

    /**
     * Retorna resumido por todos os produtos a informação de:
     * nome do produto,
     * data,
     * total de simulacoes por dia,
     * media de ValorFinal por dia.
     *
     * Observação: utiliza uma query nativa contra uma tabela esperada de simulações
     * chamada "tb_simulacoes" com colunas (produto_id, data, valor_final). Se a tabela
     * não existir, o método retornará lista vazia em tempo de execução.
     * @return lista de SimulacaoProdutoDiaResponse com agregações diárias por produto
     */
    public List<SimulacaoProdutoDiaResponse> produtoDiaInfo() {
        String sql = "SELECT p.nome as produtoNome, DATE(s.data_simulacao) as dia, COUNT(*) as totalSim, AVG(s.valor_final) as mediaValorFinal "
                + "FROM tb_simulacoes s JOIN tb_produtos p ON p.id = s.produto_id "
                + "GROUP BY p.nome, DATE(s.data_simulacao) "
                + "ORDER BY p.nome, DATE(s.data_simulacao) DESC";

        var query = em.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<SimulacaoProdutoDiaResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            String produtoNome = row[0] != null ? row[0].toString() : null;
            LocalDate dia = null;
            if (row[1] instanceof Date d) {
                dia = d.toLocalDate();
            } else if (row[1] instanceof java.sql.Timestamp ts) {
                dia = ts.toLocalDateTime().toLocalDate();
            } else if (row[1] != null) {
                dia = LocalDate.parse(row[1].toString());
            }
            Long totalSim = null;
            if (row[2] instanceof Number n) totalSim = n.longValue();
            BigDecimal media = null;
            if (row[3] instanceof Number m) media = new BigDecimal(m.toString());

            result.add(new SimulacaoProdutoDiaResponse(produtoNome, dia, totalSim, media));
        }
        return result;
    }

    /**
     * Busca histórico de perfil do cliente (utilizado como proxy para histórico de ações relacionadas ao cliente).
     * Retorna registros com data de criação, perfil anterior e novo perfil e motivo.
     * @param clienteId id do cliente
     * @return lista de arrays: [criadoEm, perfilAnteriorNome, perfilNovoNome, motivo]
     */
    public List<Object[]> buscarHistoricoPerfilDoCliente(Long clienteId) {
        TypedQuery<Object[]> q = em.createQuery(
                "SELECT h.criadoEm, h.perfilAnterior.nome, h.perfilNovo.nome, h.motivo FROM ClientePerfilHistorico h WHERE h.cliente.id = :cid ORDER BY h.criadoEm DESC",
                Object[].class);
        q.setParameter("cid", clienteId);
        return q.getResultList();
    }
}
