package br.com.cxinvest.entity;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_perfil_investimento")
public class Perfil extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false)
    public String nome;

    @NotNull
    @Column(nullable = false)
    public Integer pontuacao;

    @Column(length = 1000)
    public String descricao;


}

