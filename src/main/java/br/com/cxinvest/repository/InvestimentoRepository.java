package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.math.BigDecimal;

@ApplicationScoped
public class InvestimentoRepository implements PanacheRepository<Investimento> {

    // Retorna investimentos do cliente ordenados por data e paginados no banco usando PanacheQuery.page
    public List<Investimento> listarPorCliente(Long clienteId, int page, int size, boolean asc) {
        return clienteId == null ? List.of() :
                find("cliente.id = ?1 ORDER BY dataInvestimento " + (asc ? "ASC" : "DESC"), clienteId)
                        .page(Page.of(Math.max(0, page), Math.max(1, size)))
                        .list();
    }

    // Retorna o total investido por um cliente
    public BigDecimal totalInvestidoPorCliente(Long clienteId) {
        if (clienteId == null) return BigDecimal.ZERO;

        try {
            BigDecimal soma = getEntityManager()
                    .createQuery("SELECT SUM(i.valorInvestido) FROM Investimento i WHERE i.cliente.id = :id", BigDecimal.class)
                    .setParameter("id", clienteId)
                    .getSingleResult();
            return soma == null ? BigDecimal.ZERO : soma;
        } catch (Exception e) {
            // em caso de erro, log poderia ser adicionado; retornamos zero para segurança
            return BigDecimal.ZERO;
        }
    }
}
