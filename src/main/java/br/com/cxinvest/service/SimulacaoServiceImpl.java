package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;
import br.com.cxinvest.repository.SimulacaoRepository;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.entity.Enum.TipoProduto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {


    //TODO : Implementar a lógica real do SimulacaoService (preencher DTOs e regras de negócio).
    @Inject
    SimulacaoRepository repository;

    @Inject
    ClienteRepository clienteRepository;

    @Override
    public SimulacaoResponse simular(SimulacaoRequest simulacaoRequest) {
        // chama repositório apenas para utilizar consultas; lógica de simulação não implementada
        if (simulacaoRequest != null && simulacaoRequest.tipoProduto() != null) {
            repository.findProdutosByTipo(simulacaoRequest.tipoProduto());
        }
        return null;
    }

    @Override
    public SimulacaoHistoricoResponse historico(SimulacaoRequest simulacaoRequest) {
        if (simulacaoRequest != null) {
            repository.buscarHistoricoPerfilDoCliente(simulacaoRequest.clienteId());
        }
        return null;
    }

    @Override
    public List<SimulacaoProdutoDiaResponse> produtoDia() {
        List<SimulacaoProdutoDiaResponse> produtoDiaInfo = repository.produtoDiaInfo();
        return produtoDiaInfo;
    }

    /**
     * Valida e mapeia o tipo de produto informado para o enum TipoProduto.
     * Retorna um Optional contendo o TipoProduto quando for válido, ou Optional.empty() quando inválido.
     * Este método normaliza e delega o parsing para TipoProduto.fromLabel.
     *
     * @param tipoProduto string com o tipo do produto (ex.: "CDB", "Tesouro Direto")
     * @return Optional com o TipoProduto correspondente
     */
    public Optional<TipoProduto> validarProduto(String tipoProduto) {
        return TipoProduto.fromLabel(tipoProduto);
    }

    /**
     * Verifica se o produto identificado por produtoId pertence ao mesmo perfil do cliente (clienteId).
     * Se o produto não for adequado ao perfil do cliente lança BadRequestException com a mensagem
     * especificada: "este produto nao é adequado ao perfil do cliente".
     *
     * @param produtoId id do produto a validar
     * @param clienteId id do cliente
     */
    public void validarPerfilProduto(Long produtoId, Long clienteId) {
        var produtoOpt = repository.findProdutoById(produtoId);
        var clienteOpt = clienteRepository.findByIdOptional(clienteId);

        if (produtoOpt.isEmpty()) {
            throw new NotFoundException("Produto não encontrado: " + produtoId);
        }
        if (clienteOpt.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado: " + clienteId);
        }

        var produto = produtoOpt.get();
        var cliente = clienteOpt.get();

        Long perfilProdutoId = produto.perfilInvestimento != null ? produto.perfilInvestimento.id : null;
        Long perfilClienteId = cliente.perfilInvestimento != null ? cliente.perfilInvestimento.id : null;

        if (!Objects.equals(perfilProdutoId, perfilClienteId)) {
            throw new BadRequestException("este produto nao é adequado ao perfil do cliente");
        }
    }

    /**
     * Calcula o valor final de uma simulação aplicando juros compostos mensais.
     * Fórmula: valorFinal = valorSimulacao * (1 + taxaMensal)^meses
     *
     * @param valorSimulacao valor inicial investido
     * @param taxaMensal taxa de juros mensal em decimal (ex.: 0.01 para 1%)
     * @param meses quantidade de meses (inteiro >= 0)
     * @return valor final arredondado para 2 casas decimais
     */
    public BigDecimal calcularRentabilidadeFinal(BigDecimal valorSimulacao, BigDecimal taxaMensal, int meses) {
        if (valorSimulacao == null) return null;
        if (taxaMensal == null || meses <= 0) {
            // sem aplicação de juros retorna o valor original (normalizado)
            return valorSimulacao.setScale(2, RoundingMode.HALF_EVEN);
        }

        MathContext mc = MathContext.DECIMAL128;
        BigDecimal base = BigDecimal.ONE.add(taxaMensal, mc);
        BigDecimal fator = base.pow(meses, mc);
        BigDecimal resultado = valorSimulacao.multiply(fator, mc);
        return resultado.setScale(2, RoundingMode.HALF_EVEN);
    }
}
