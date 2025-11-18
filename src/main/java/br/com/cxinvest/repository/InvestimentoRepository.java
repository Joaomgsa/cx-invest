package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InvestimentoRepository implements PanacheRepository<Investimento> {

    // Retorna investimentos do cliente ordenados por data e paginados no banco usando PanacheQuery.page
    public List<Investimento> listarPorCliente(Long clienteId, int page, int size, boolean asc) {
        return Optional.ofNullable(clienteId)
                .map(id -> {
                    int pageSize = Math.max(1, size);
                    String order = asc ? "ASC" : "DESC";
                    // usa paginação no banco para evitar carregar tudo em memória
                    return find("cliente.id = ?1 ORDER BY dataInvestimento " + order, id)
                            .page(Page.of(Math.max(0, page), pageSize))
                            .list();
                })
                .orElseGet(List::of);
    }
}
