package br.com.cxinvest.service;

import br.com.cxinvest.dto.PerfilRiscoResponse;
import br.com.cxinvest.entity.Investimento;
import br.com.cxinvest.entity.Simulacao;
import br.com.cxinvest.repository.InvestimentoRepository;
import br.com.cxinvest.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    InvestimentoRepository investimentoRepository;

    @Inject
    TelemetriaService telemetriaService;

    public PerfilRiscoResponse calcularPerfilRisco(Long clienteId) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<Simulacao> simulacoes = simulacaoRepository.findByClienteId(clienteId);
            List<Investimento> investimentos = investimentoRepository.findByClienteId(clienteId);

            int pontuacao = calcularPontuacao(simulacoes, investimentos);
            String perfil = determinarPerfil(pontuacao);
            String descricao = obterDescricao(perfil);

            PerfilRiscoResponse response = new PerfilRiscoResponse();
            response.setClienteId(clienteId);
            response.setPerfil(perfil);
            response.setPontuacao(pontuacao);
            response.setDescricao(descricao);

            return response;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("perfil-risco", duration);
        }
    }

    private int calcularPontuacao(List<Simulacao> simulacoes, List<Investimento> investimentos) {
        int pontos = 50; // Base inicial

        // Volume de investimentos (0-30 pontos)
        double volumeTotal = investimentos.stream()
                .mapToDouble(Investimento::getValor)
                .sum();
        
        if (volumeTotal > 100000) {
            pontos += 30;
        } else if (volumeTotal > 50000) {
            pontos += 20;
        } else if (volumeTotal > 10000) {
            pontos += 10;
        }

        // Frequência de movimentações (0-20 pontos)
        int quantidadeOperacoes = simulacoes.size() + investimentos.size();
        if (quantidadeOperacoes > 20) {
            pontos += 20;
        } else if (quantidadeOperacoes > 10) {
            pontos += 15;
        } else if (quantidadeOperacoes > 5) {
            pontos += 10;
        }

        // Preferência por rentabilidade vs liquidez (0-20 pontos)
        double rentabilidadeMedia = investimentos.stream()
                .mapToDouble(Investimento::getRentabilidade)
                .average()
                .orElse(0.0);
        
        if (rentabilidadeMedia > 0.15) {
            pontos += 20;
        } else if (rentabilidadeMedia > 0.10) {
            pontos += 10;
        }

        return Math.min(100, pontos);
    }

    private String determinarPerfil(int pontuacao) {
        if (pontuacao >= 70) {
            return "Agressivo";
        } else if (pontuacao >= 40) {
            return "Moderado";
        } else {
            return "Conservador";
        }
    }

    private String obterDescricao(String perfil) {
        switch (perfil) {
            case "Conservador":
                return "Perfil com baixa movimentação e foco em liquidez.";
            case "Moderado":
                return "Perfil equilibrado entre segurança e rentabilidade.";
            case "Agressivo":
                return "Perfil que busca alta rentabilidade e aceita maior risco.";
            default:
                return "Perfil não definido.";
        }
    }
}
