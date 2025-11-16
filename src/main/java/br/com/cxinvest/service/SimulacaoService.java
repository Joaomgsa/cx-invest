package br.com.cxinvest.service;

import br.com.cxinvest.dto.*;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.entity.Simulacao;
import br.com.cxinvest.repository.ProdutoRepository;
import br.com.cxinvest.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    TelemetriaService telemetriaService;

    @Transactional
    public SimulacaoResponse simularInvestimento(SimulacaoRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validar entrada
            validarRequest(request);

            // Buscar produto adequado
            Produto produto = produtoRepository.findByTipo(request.getTipoProduto(), request.getValor(), request.getPrazoMeses())
                    .orElseThrow(() -> new IllegalArgumentException("Nenhum produto encontrado para os parâmetros fornecidos"));

            // Calcular simulação
            double valorFinal = calcularValorFinal(request.getValor(), produto.getRentabilidade(), request.getPrazoMeses());
            double rentabilidadeEfetiva = produto.getRentabilidade();

            // Persistir simulação
            Simulacao simulacao = new Simulacao();
            simulacao.setClienteId(request.getClienteId());
            simulacao.setProduto(produto);
            simulacao.setValorInvestido(request.getValor());
            simulacao.setValorFinal(valorFinal);
            simulacao.setPrazoMeses(request.getPrazoMeses());
            simulacao.setDataSimulacao(LocalDateTime.now());
            simulacao.setRentabilidadeEfetiva(rentabilidadeEfetiva);

            simulacaoRepository.persist(simulacao);

            // Montar resposta
            SimulacaoResponse response = new SimulacaoResponse();
            
            SimulacaoResponse.ProdutoValidado produtoValidado = new SimulacaoResponse.ProdutoValidado();
            produtoValidado.setId(produto.getId());
            produtoValidado.setNome(produto.getNome());
            produtoValidado.setTipo(produto.getTipo());
            produtoValidado.setRentabilidade(produto.getRentabilidade());
            produtoValidado.setRisco(produto.getRisco());
            response.setProdutoValidado(produtoValidado);

            SimulacaoResponse.ResultadoSimulacao resultado = new SimulacaoResponse.ResultadoSimulacao();
            resultado.setValorFinal(valorFinal);
            resultado.setRentabilidadeEfetiva(rentabilidadeEfetiva);
            resultado.setPrazoMeses(request.getPrazoMeses());
            response.setResultadoSimulacao(resultado);

            response.setDataSimulacao(simulacao.getDataSimulacao());

            return response;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("simular-investimento", duration);
        }
    }

    private void validarRequest(SimulacaoRequest request) {
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("ClienteId é obrigatório");
        }
        if (request.getValor() == null || request.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        if (request.getPrazoMeses() == null || request.getPrazoMeses() <= 0) {
            throw new IllegalArgumentException("Prazo deve ser maior que zero");
        }
        if (request.getTipoProduto() == null || request.getTipoProduto().trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de produto é obrigatório");
        }
    }

    private double calcularValorFinal(double valorInicial, double rentabilidade, int prazoMeses) {
        // Cálculo com juros compostos: M = C * (1 + i)^t
        // onde i é a taxa mensal e t é o prazo em meses
        double taxaMensal = rentabilidade / 12;
        return valorInicial * Math.pow(1 + taxaMensal, prazoMeses);
    }

    public List<SimulacaoHistoricoResponse> listarSimulacoes() {
        long startTime = System.currentTimeMillis();
        
        try {
            return simulacaoRepository.listAll().stream()
                    .map(this::toHistoricoResponse)
                    .collect(Collectors.toList());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("listar-simulacoes", duration);
        }
    }

    public List<SimulacaoPorProdutoDiaResponse> listarSimulacoesPorProdutoDia() {
        long startTime = System.currentTimeMillis();
        
        try {
            List<Object[]> resultados = simulacaoRepository.findAgregadoPorProdutoEDia();
            
            return resultados.stream()
                    .map(r -> {
                        SimulacaoPorProdutoDiaResponse response = new SimulacaoPorProdutoDiaResponse();
                        response.setProduto((String) r[0]);
                        // Parse the date string to LocalDate
                        Object dateObj = r[1];
                        LocalDate date;
                        if (dateObj instanceof LocalDate) {
                            date = (LocalDate) dateObj;
                        } else if (dateObj instanceof String) {
                            date = LocalDate.parse((String) dateObj);
                        } else {
                            date = LocalDate.now();
                        }
                        response.setData(date);
                        response.setQuantidadeSimulacoes(((Number) r[2]).intValue());
                        response.setMediaValorFinal(((Number) r[3]).doubleValue());
                        return response;
                    })
                    .collect(Collectors.toList());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("listar-por-produto-dia", duration);
        }
    }

    private SimulacaoHistoricoResponse toHistoricoResponse(Simulacao simulacao) {
        SimulacaoHistoricoResponse response = new SimulacaoHistoricoResponse();
        response.setId(simulacao.getId());
        response.setClienteId(simulacao.getClienteId());
        response.setProduto(simulacao.getProduto().getNome());
        response.setValorInvestido(simulacao.getValorInvestido());
        response.setValorFinal(simulacao.getValorFinal());
        response.setPrazoMeses(simulacao.getPrazoMeses());
        response.setDataSimulacao(simulacao.getDataSimulacao());
        return response;
    }
}
