package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.repository.PerfilRepository;
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

    @Inject
    PerfilRepository perfilRepository;

    public ClienteService() {}

    public ClienteService(ClienteRepository repository, PerfilRepository perfilRepository) {
        this.repository = repository;
        this.perfilRepository = perfilRepository;
    }

    public List<Cliente> listarTodos() {
        return repository.listAllClientes();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        if (cliente.perfilInvestimento == null || cliente.perfilInvestimento.id == null) {
            throw new IllegalArgumentException("perfilId é obrigatório");
        }
        Optional<Perfil> perfilOpt = perfilRepository.findByIdOptional(cliente.perfilInvestimento.id);
        if (perfilOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + cliente.perfilInvestimento.id);
        }
        cliente.perfilInvestimento = perfilOpt.get();
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
        if (clienteAtualizado.perfilInvestimento == null || clienteAtualizado.perfilInvestimento.id == null) {
            throw new IllegalArgumentException("perfilId é obrigatório");
        }
        Optional<Perfil> perfilOpt = perfilRepository.findByIdOptional(clienteAtualizado.perfilInvestimento.id);
        if (perfilOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + clienteAtualizado.perfilInvestimento.id);
        }
        existente.perfilInvestimento = perfilOpt.get();
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
