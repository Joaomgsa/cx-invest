package br.com.cxinvest.entity;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_produtos")
public class Produto extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false)
    public String nome;

    @NotBlank
    @Column(nullable = false)
    public String tipo;

    @Column(name = "rentabilidade_mensal", nullable = false)
    public BigDecimal rentabilidadeMensal;

    @Enumerated(EnumType.STRING)
    @Column(name = "classe_risco", nullable = false)
    public ClasseRisco classeRisco;

    public enum ClasseRisco {
        CONSERVADOR,
        MODERADO,
        AGRESSIVO
    }

    // getters/setters opcionais
}

