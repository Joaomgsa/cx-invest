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

    /**
     * Lista produtos filtrando pelo nome do perfil de investimento.
     * <p>
     * A busca é feita de forma case-insensitive (comparação em lower case) e
     * valida o parâmetro: se o nome for nulo ou vazio, retorna Optional.empty().
     *
     * @param perfilNome nome do perfil de investimento (ex.: "Conservador")
     * @return Optional com a lista de produtos quando houver resultado, ou Optional.empty()
     */
    public Optional<List<Produto>> listarProdutosPorNomePerfil(String perfilNome) {
        if (perfilNome == null || perfilNome.isBlank()) {
            return Optional.empty();
        }
        String nome = perfilNome.trim().toLowerCase();
        List<Produto> produtos = list("LOWER(perfilInvestimento.nome) = ?1", nome);
        return produtos == null || produtos.isEmpty() ? Optional.empty() : Optional.of(produtos);
    }


}
