package br.com.cxinvest.dto;

import java.time.LocalDateTime;

public class SimulacaoResponse {
    private ProdutoValidado produtoValidado;
    private ResultadoSimulacao resultadoSimulacao;
    private LocalDateTime dataSimulacao;

    public ProdutoValidado getProdutoValidado() {
        return produtoValidado;
    }

    public void setProdutoValidado(ProdutoValidado produtoValidado) {
        this.produtoValidado = produtoValidado;
    }

    public ResultadoSimulacao getResultadoSimulacao() {
        return resultadoSimulacao;
    }

    public void setResultadoSimulacao(ResultadoSimulacao resultadoSimulacao) {
        this.resultadoSimulacao = resultadoSimulacao;
    }

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }

    public static class ProdutoValidado {
        private Long id;
        private String nome;
        private String tipo;
        private Double rentabilidade;
        private String risco;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Double getRentabilidade() {
            return rentabilidade;
        }

        public void setRentabilidade(Double rentabilidade) {
            this.rentabilidade = rentabilidade;
        }

        public String getRisco() {
            return risco;
        }

        public void setRisco(String risco) {
            this.risco = risco;
        }
    }

    public static class ResultadoSimulacao {
        private Double valorFinal;
        private Double rentabilidadeEfetiva;
        private Integer prazoMeses;

        public Double getValorFinal() {
            return valorFinal;
        }

        public void setValorFinal(Double valorFinal) {
            this.valorFinal = valorFinal;
        }

        public Double getRentabilidadeEfetiva() {
            return rentabilidadeEfetiva;
        }

        public void setRentabilidadeEfetiva(Double rentabilidadeEfetiva) {
            this.rentabilidadeEfetiva = rentabilidadeEfetiva;
        }

        public Integer getPrazoMeses() {
            return prazoMeses;
        }

        public void setPrazoMeses(Integer prazoMeses) {
            this.prazoMeses = prazoMeses;
        }
    }
}
