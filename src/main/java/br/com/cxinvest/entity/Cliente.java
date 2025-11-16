package br.com.cxinvest.entity;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tb_clientes")
public class Cliente extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false)
    public String nome;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_risco", nullable = false)
    public PerfilRisco perfilRisco;

    public enum PerfilRisco {
        CONSERVADOR,
        MODERADO,
        AGRESSIVO
    }

}
