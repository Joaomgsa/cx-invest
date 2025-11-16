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

    public Perfil persistPerfil(Perfil perfil) {
        persist(perfil);
        return perfil;
    }

    public void removeById(Long id) {
        delete("id", id);
    }
}

