package br.com.cxinvest.service;

import br.com.cxinvest.dto.ProdutoResponse;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    TelemetriaService telemetriaService;

    public List<ProdutoResponse> listarProdutosPorPerfil(String perfil) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<Produto> produtos = produtoRepository.findByPerfil(perfil);
            
            return produtos.stream()
                    .map(this::toProdutoResponse)
                    .collect(Collectors.toList());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("produtos-recomendados", duration);
        }
    }

    private ProdutoResponse toProdutoResponse(Produto produto) {
        ProdutoResponse response = new ProdutoResponse();
        response.setId(produto.getId());
        response.setNome(produto.getNome());
        response.setTipo(produto.getTipo());
        response.setRentabilidade(produto.getRentabilidade());
        response.setRisco(produto.getRisco());
        return response;
    }
}
