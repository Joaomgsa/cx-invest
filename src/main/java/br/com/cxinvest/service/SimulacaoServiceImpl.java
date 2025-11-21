package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoResultadoResponse;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.entity.Simulacao;
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
import java.time.Instant;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {


    //TODO : Implementar a lógica real do SimulacaoService (preencher DTOs e regras de negócio).
    @Inject
    SimulacaoRepository repository;

    @Inject
    ClienteRepository clienteRepository;

    /*
        * Método de simulação de investimento.
        *    Etapas da simulação (não implementadas):
        *    1. Validar o tipo de produto solicitado.
        *    2. Verificar se o produto é adequado ao perfil do cliente.
        *    3. Calcular a rentabilidade final com base no valor investido, taxa e período.
        *    4. Persistir e retornar o SimulacaoResponse com os dados da simulação.
        *
     */
    @Override
    public SimulacaoResponse simular(SimulacaoRequest simulacaoRequest) {
        if (simulacaoRequest == null) {
            throw new BadRequestException("Requisição de simulação inválida");
        }

        // 1) validar tipo de produto informado
        var tipoOpt = validarProduto(simulacaoRequest.tipoProduto());
        if (tipoOpt.isEmpty()) {
            throw new BadRequestException("Tipo de produto inválido: " + simulacaoRequest.tipoProduto());
        }

        // 2) recuperar produtos pelo tipo e escolher um (modelo simples: primeiro da lista)
        List<Produto> produtos = repository.findProdutosByTipo(simulacaoRequest.tipoProduto());
        if (produtos == null || produtos.isEmpty()) {
            throw new NotFoundException("Nenhum produto encontrado para o tipo: " + simulacaoRequest.tipoProduto());
        }
        Produto produto = produtos.stream().findFirst().orElseThrow(() -> new NotFoundException("Nenhum produto encontrado para o tipo: " + simulacaoRequest.tipoProduto()));

        // 3) verificar se o produto é adequado ao perfil do cliente
        validarPerfilProduto(produto.id, simulacaoRequest.clienteId());

        // 4) calcular rentabilidade final
        BigDecimal valorSim = simulacaoRequest.valor();
        int meses = simulacaoRequest.prazoMeses();
        BigDecimal taxaMensal = produto.rentabilidadeMensal;
        BigDecimal valorFinal = calcularRentabilidadeFinal(valorSim, taxaMensal, meses);

        // determinar rentabilidade efetiva (retorno percentual total)
        BigDecimal rentabilidadeEfetiva = null;
        if (valorFinal != null && valorSim != null && valorSim.compareTo(BigDecimal.ZERO) > 0) {
            rentabilidadeEfetiva = valorFinal.subtract(valorSim).divide(valorSim, 6, RoundingMode.HALF_EVEN);
        }

        // persistir simulação
        var clienteOpt = clienteRepository.findByIdOptional(simulacaoRequest.clienteId());
        var cliente = clienteOpt.orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + simulacaoRequest.clienteId()));

        var simulacao = new Simulacao();
        simulacao.produto = produto;
        simulacao.cliente = cliente;
        simulacao.rentabilidadeEfetiva = rentabilidadeEfetiva != null ? rentabilidadeEfetiva : BigDecimal.ZERO;
        simulacao.valorSimulacao = valorSim;
        simulacao.valorFinal = valorFinal != null ? valorFinal : valorSim;
        simulacao.prazoMeses = meses;

        repository.salvarSimulacao(simulacao);

        // montar DTOs de resposta
        var produtoDto = new SimulacaoProdutoResponse(
                produto.id,
                produto.nome,
                produto.tipo,
                produto.rentabilidadeMensal,
                determinarRisco(produto.perfilInvestimento)
        );

        var resultadoDto = new SimulacaoResultadoResponse(
                simulacao.valorFinal,
                simulacao.rentabilidadeEfetiva,
                simulacao.prazoMeses
        );

        String data = Instant.now().toString();

        return new SimulacaoResponse(produtoDto, resultadoDto, data);
    }

    /*
        Metodo recebe parametros para paginar o historico de simulacoes realizadas.
     */
    @Override
    public List<SimulacaoHistoricoResponse> historico(int page, int size) {
        List<br.com.cxinvest.entity.Simulacao> sims = repository.buscarTodos(page, size);
        return sims.stream().map(s -> new SimulacaoHistoricoResponse(
                s.id,
                s.produto != null ? s.produto.id : null,
                s.produto != null ? s.produto.nome : null,
                s.cliente != null ? s.cliente.id : null,
                s.valorSimulacao,
                s.valorFinal,
                s.prazoMeses,
                s.rentabilidadeEfetiva,
                s.dataSimulacao != null ? s.dataSimulacao.toString() : null
        )).toList();
    }

    @Override
    public List<SimulacaoProdutoDiaResponse> produtoDia(int page, int size) {
        int p = Math.max(0, page);
        int s = size <= 0 ? 10 : size;
        return repository.produtoDiaInfo(p, s);
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

    /**
     * Mapeia o perfil para um rótulo de risco (Baixo/Médio/Alto) ou retorna o nome do perfil quando desconhecido.
     * TODO: Verificar pq produto tem perfil de risco e ver se conflita com perfil de investimento
     */
    public String determinarRisco(br.com.cxinvest.entity.Perfil perfil) {
        String risco = "Desconhecido";
        if (perfil != null && perfil.nome != null) {
            switch (perfil.nome.toUpperCase()) {
                case "CONSERVADOR" -> risco = "Baixo";
                case "MODERADO" -> risco = "Médio";
                case "AGRESSIVO" -> risco = "Alto";
                default -> risco = perfil.nome;
            }
        }
        return risco;
    }
}
