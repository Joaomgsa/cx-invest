package br.com.cxinvest.repository;

import br.com.cxinvest.entity.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    public List<Produto> listAllProdutos() {
        return listAll();
    }

    public Optional<Produto> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    public Produto persistProduto(Produto produto) {
        persist(produto);
        return produto;
    }

    public void removeById(Long id) {
        delete("id", id);
    }


    /*
    * Consulta que retorna todos os produtos de determinado perfil de investidor (por perfilId)
    *      */
    public Optional<List<Produto>> listarProdutosPorPerfil(Long perfilId) {
        // busca por id do perfil associado (perfilInvestimento.id)
        List<Produto> produtos = list("perfilInvestimento.id = ?1", perfilId);
        return produtos == null || produtos.isEmpty() ? Optional.empty() : Optional.of(produtos);
    }
}
