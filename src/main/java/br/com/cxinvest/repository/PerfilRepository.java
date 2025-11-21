package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Perfil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PerfilRepository implements PanacheRepository<Perfil> {

    public List<Perfil> listAllPerfis() {
        return listAll();
    }

    public Optional<Perfil> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    public void persistPerfil(Perfil perfil) {
        persist(perfil);
    }

    public void removeById(Long id) {
        delete("id", id);
    }

    /**
     * Busca um perfil pelo nome (case-insensitive).
     *
     * @param nome nome do perfil (ex.: "Conservador")
     * @return Optional contendo o perfil quando encontrado, caso contrário Optional.empty()
     */
    public Optional<Perfil> findByNomeOptional(String nome) {
        if (nome == null || nome.isBlank()) {
            return Optional.empty();
        }
        String n = nome.trim().toLowerCase();
        return find("LOWER(nome) = ?1", n).firstResultOptional();
    }
}
