package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.repository.PerfilRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PerfilService {

    @Inject
    PerfilRepository repository;

    public List<Perfil> listarTodos() {
        return repository.listAllPerfis();
    }

    public Optional<Perfil> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    @Transactional
    public Perfil criar(Perfil perfil) {
        repository.persistPerfil(perfil);
        return perfil;
    }

    @Transactional
    public Perfil atualizar(Long id, Perfil perfilAtualizado) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + id);
        }
        Perfil existente = existenteOpt.get();
        existente.nome = perfilAtualizado.nome;
        existente.pontuacao = perfilAtualizado.pontuacao;
        existente.descricao = perfilAtualizado.descricao;
        repository.persist(existente);
        return existente;
    }

    @Transactional
    public void remover(Long id) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + id);
        }
        repository.removeById(id);
    }
}

