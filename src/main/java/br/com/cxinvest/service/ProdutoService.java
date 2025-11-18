package br.com.cxinvest.service;

import br.com.cxinvest.dto.ProdutoResponse;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;


//TODO: adicionar soft delete para nao perder histórico de perfis vinculados a produtos e clientes

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository repository;

    @Inject
    PerfilRepository perfilRepository;


    public List<Produto> listarTodos() {
        return repository.listAllProdutos();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    @Transactional
    public Produto criar(Produto produto) {
        if (produto.perfilInvestimento == null || produto.perfilInvestimento.id == null) {
            throw new IllegalArgumentException("perfilId é obrigatório");
        }
        Optional<Perfil> perfilOpt = perfilRepository.findByIdOptional(produto.perfilInvestimento.id);
        if (perfilOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + produto.perfilInvestimento.id);
        }
        produto.perfilInvestimento = perfilOpt.get();
        repository.persistProduto(produto);
        return produto;
    }

    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Optional<Produto> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Produto não encontrado: " + id);
        }
        Produto existente = existenteOpt.get();
        existente.nome = produtoAtualizado.nome;
        existente.tipo = produtoAtualizado.tipo;
        existente.rentabilidadeMensal = produtoAtualizado.rentabilidadeMensal;
        if (produtoAtualizado.perfilInvestimento == null || produtoAtualizado.perfilInvestimento.id == null) {
            throw new IllegalArgumentException("perfilId é obrigatório");
        }
        Optional<Perfil> perfilOpt = perfilRepository.findByIdOptional(produtoAtualizado.perfilInvestimento.id);
        if (perfilOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + produtoAtualizado.perfilInvestimento.id);
        }
        existente.perfilInvestimento = perfilOpt.get();
        repository.persist(existente);
        return existente;
    }

    @Transactional
    public void remover(Long id) {
        Optional<Produto> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Produto não encontrado: " + id);
        }
        repository.removeById(id);
    }

    public List<ProdutoResponse> produtosRecomendadosPerfil(Long perfilId) {
        var opt = repository.listarProdutosPorPerfil(perfilId);
        return opt
                .map(list -> list.stream().map(p -> new ProdutoResponse(
                        p.id,
                        p.nome,
                        p.tipo,
                        p.rentabilidadeMensal,
                        p.perfilInvestimento != null ? p.perfilInvestimento.id : null,
                        p.perfilInvestimento != null ? p.perfilInvestimento.nome : null
                )).toList())
                .orElseGet(List::of);
    }
}
