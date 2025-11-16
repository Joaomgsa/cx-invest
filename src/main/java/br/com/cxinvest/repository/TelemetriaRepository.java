package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Telemetria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<Telemetria> {

    public List<Object[]> findEstatisticasPorServico(LocalDateTime inicio, LocalDateTime fim) {
        return getEntityManager()
                .createQuery("SELECT t.nomeServico, COUNT(t), AVG(t.tempoRespostaMs) " +
                        "FROM Telemetria t " +
                        "WHERE t.timestamp BETWEEN ?1 AND ?2 " +
                        "GROUP BY t.nomeServico", Object[].class)
                .setParameter(1, inicio)
                .setParameter(2, fim)
                .getResultList();
    }
}
