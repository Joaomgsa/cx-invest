package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    public Optional<Produto> findByTipo(String tipo, Double valor, Integer prazoMeses) {
        return find("tipo = ?1 and (valorMinimo is null or valorMinimo <= ?2) " +
                "and (prazoMinMeses is null or prazoMinMeses <= ?3) " +
                "and (prazoMaxMeses is null or prazoMaxMeses >= ?3)", 
                tipo, valor, prazoMeses)
                .firstResultOptional();
    }

    public List<Produto> findByPerfil(String perfil) {
        String risco;
        switch (perfil.toUpperCase()) {
            case "CONSERVADOR":
                risco = "Baixo";
                break;
            case "MODERADO":
                risco = "Médio";
                break;
            case "AGRESSIVO":
                risco = "Alto";
                break;
            default:
                risco = "Baixo";
        }
        return list("risco", risco);
    }
}
