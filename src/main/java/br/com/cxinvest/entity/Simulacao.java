package br.com.cxinvest.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.math.BigDecimal;
import java.time.Instant;
import br.com.cxinvest.converter.InstantAttributeConverter;

@Entity
@Table(name = "tb_simulacoes")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    public Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @Column(name = "rentabilidade_efetiva", nullable = false)
    public BigDecimal rentabilidadeEfetiva;

    @Column(name = "valor_simulacao", nullable = false)
    public BigDecimal valorSimulacao;

    @Column(name = "valor_final", nullable = false)
    public BigDecimal valorFinal;

    @Column(name = "prazo_meses", nullable = false)
    public Integer prazoMeses;

    @CreationTimestamp
    @Convert(converter = InstantAttributeConverter.class)
    @Column(name = "data_simulacao", nullable = false, columnDefinition = "TEXT")
    public Instant dataSimulacao;


}
