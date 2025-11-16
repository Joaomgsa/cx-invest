package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {

    public List<Simulacao> findByClienteId(Long clienteId) {
        return list("clienteId", clienteId);
    }

    public List<Object[]> findAgregadoPorProdutoEDia() {
        return getEntityManager()
                .createQuery("SELECT p.nome, FUNCTION('DATE', s.dataSimulacao), COUNT(s), AVG(s.valorFinal) " +
                        "FROM Simulacao s JOIN s.produto p " +
                        "GROUP BY p.nome, FUNCTION('DATE', s.dataSimulacao)", Object[].class)
                .getResultList();
    }
}
