package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class InvestimentoRepository implements PanacheRepository<Investimento> {

    public List<Investimento> findByClienteId(Long clienteId) {
        return list("clienteId = ?1 ORDER BY data DESC", clienteId);
    }
}
