package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository repository;

    public List<Cliente> listarTodos() {
        return repository.listAllClientes();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        repository.persistCliente(cliente);
        return cliente;
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new NotFoundException("Cliente não encontrado: " + id);
        }
        existente.nome = clienteAtualizado.nome;
        existente.email = clienteAtualizado.email;
        existente.perfilRisco = clienteAtualizado.perfilRisco;
        repository.persist(existente);
        return existente;
    }

    @Transactional
    public void remover(Long id) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new NotFoundException("Cliente não encontrado: " + id);
        }
        repository.removeById(id);
    }
}

