package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    public Optional<Cliente> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    public List<Cliente> listAllClientes() {
        return listAll();
    }

    public Optional<Cliente> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Cliente persistCliente(Cliente cliente) {
        persist(cliente);
        return cliente;
    }

    public void removeById(Long id) {
        delete("id", id);
    }
}

