package br.com.cxinvest.repository;

import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.ClientePerfilHistorico;
import br.com.cxinvest.entity.Simulacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

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
        return produtoDiaInfo(0, 10);
    }

    /**
     * Retorna resumido por todos os produtos a informação de:
     * nome do produto,
     * data,
     * total de simulacoes por dia,
     * media de ValorFinal por dia.
     *
     * Versão paginada: aceita page e size e aplica LIMIT/OFFSET na query nativa.
     *
     * @param page página 0-based
     * @param size tamanho da página
     * @return lista de SimulacaoProdutoDiaResponse com agregações diárias por produto
     */
    public List<SimulacaoProdutoDiaResponse> produtoDiaInfo(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        int offset = page * size;

        // Agrupa por dia (data sem hora) usando FUNCTION('DATE', ...) para portabilidade do agrupamento por dia
        String jpql = "SELECT p.nome, FUNCTION('DATE', s.dataSimulacao), COUNT(s), AVG(s.valorFinal) "
                + "FROM Simulacao s JOIN s.produto p "
                + "GROUP BY p.nome, FUNCTION('DATE', s.dataSimulacao) "
                + "ORDER BY p.nome, FUNCTION('DATE', s.dataSimulacao) DESC";

        var query = em.createQuery(jpql, Object[].class);
        query.setFirstResult(offset);
        query.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<SimulacaoProdutoDiaResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            String produtoNome = row[0] != null ? row[0].toString() : null;
            java.time.LocalDate dia = null;
            if (row[1] instanceof java.sql.Date d) {
                dia = d.toLocalDate();
            } else if (row[1] instanceof java.sql.Timestamp ts) {
                dia = ts.toLocalDateTime().toLocalDate();
            } else if (row[1] instanceof java.time.LocalDate ld) {
                dia = ld;
            } else if (row[1] instanceof java.time.Instant inst) {
                dia = inst.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            } else if (row[1] != null) {
                dia = java.time.LocalDate.parse(row[1].toString());
            }
            Long totalSim = null;
            if (row[2] instanceof Number n) totalSim = n.longValue();
            java.math.BigDecimal media = null;
            if (row[3] instanceof Number m) media = new java.math.BigDecimal(m.toString());

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

    /**
     * Persiste uma simulação no banco.
     * @param s entidade Simulacao
     * @return a entidade persistida
     */
    @Transactional
    public Simulacao salvarSimulacao(Simulacao s) {
        em.persist(s);
        return s;
    }

    /**
     * Busca simulações de um cliente com paginação.
     * @param clienteId id do cliente
     * @param page número da página (0-based)
     * @param size tamanho da página
     * @return lista de Simulacao
     */
    public List<Simulacao> buscarPorCliente(Long clienteId, int page, int size) {
        if (clienteId == null) return List.of();
        TypedQuery<Simulacao> q = em.createQuery("SELECT s FROM Simulacao s WHERE s.cliente.id = :cid ORDER BY s.dataSimulacao DESC", Simulacao.class);
        q.setParameter("cid", clienteId);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    /**
     * Busca todas as simulações com paginação.
     * @param page número da página (0-based)
     * @param size tamanho da página
     * @return lista de Simulacao
     */
    public List<Simulacao> buscarTodos(int page, int size) {
        TypedQuery<Simulacao> q = em.createQuery("SELECT s FROM Simulacao s ORDER BY s.dataSimulacao DESC", Simulacao.class);
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }
}
