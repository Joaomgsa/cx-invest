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

    @ManyToOne
    @JoinColumn(name = "perfil_id", nullable = false)
    public Perfil perfilInvestimento;

    // getters/setters opcionais
}
