package br.com.cxinvest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Double rentabilidade;

    @Column(nullable = false)
    private String risco;

    @Column
    private Integer prazoMinMeses;

    @Column
    private Integer prazoMaxMeses;

    @Column
    private Double valorMinimo;

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

    public Integer getPrazoMinMeses() {
        return prazoMinMeses;
    }

    public void setPrazoMinMeses(Integer prazoMinMeses) {
        this.prazoMinMeses = prazoMinMeses;
    }

    public Integer getPrazoMaxMeses() {
        return prazoMaxMeses;
    }

    public void setPrazoMaxMeses(Integer prazoMaxMeses) {
        this.prazoMaxMeses = prazoMaxMeses;
    }

    public Double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(Double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }
}
