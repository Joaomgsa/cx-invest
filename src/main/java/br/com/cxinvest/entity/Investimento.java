package br.com.cxinvest.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.Instant;
import java.time.ZoneOffset;

@Entity
@Table(name = "tb_investimentos")
public class Investimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    public Produto produto;

    @Column(name = "valor_investido", nullable = false)
    public BigDecimal valorInvestido;

    @Column(name = "rentabilidade", nullable = false)
    public BigDecimal rentabilidade;

    @Column(name = "data_investimento", nullable = false)
    public OffsetDateTime dataInvestimento;

    // Compatibilidade binária: alguns componentes esperam getDataInvestimento() retornando java.time.Instant
    public Instant getDataInvestimento() {
        return this.dataInvestimento == null ? null : this.dataInvestimento.toInstant();
    }

    public void setDataInvestimento(Instant instant) {
        this.dataInvestimento = instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


}
