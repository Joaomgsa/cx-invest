package br.com.cxinvest.dto;

import java.time.LocalDate;

public class SimulacaoPorProdutoDiaResponse {
    private String produto;
    private LocalDate data;
    private Integer quantidadeSimulacoes;
    private Double mediaValorFinal;

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getQuantidadeSimulacoes() {
        return quantidadeSimulacoes;
    }

    public void setQuantidadeSimulacoes(Integer quantidadeSimulacoes) {
        this.quantidadeSimulacoes = quantidadeSimulacoes;
    }

    public Double getMediaValorFinal() {
        return mediaValorFinal;
    }

    public void setMediaValorFinal(Double mediaValorFinal) {
        this.mediaValorFinal = mediaValorFinal;
    }
}
