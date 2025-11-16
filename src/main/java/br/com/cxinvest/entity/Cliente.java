package br.com.cxinvest.entity;

import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_clientes")
public class Cliente extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    public String nome;

    @NotBlank
    @Email
    @Column(name = "email",nullable = false)
    public String email;

    @NotNull
    @Column(name = "total_investido", nullable = false)
    public BigDecimal totalInvestido = BigDecimal.ZERO;

    //Frequencia de investimento - Alta, Media, Baixa
    @Enumerated(EnumType.STRING)
    @Column(name ="frequencia_investimento",nullable = false, length = 10)
    public FrequenciaInvestimento frequenciaInvestimento = FrequenciaInvestimento.MEDIA;


    //Liquidez ou Rentabilidade - Liquidez, Rentabilidade
    @Enumerated(EnumType.STRING)
    @Column(name ="preferencia_investimento",nullable = false, length = 10)
    public PreferenciaInvestimento preferenciaInvestimento = PreferenciaInvestimento.LIQUIDEZ;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    public Perfil perfilInvestimento;

    // Método para atualizar o perfil do cliente
    public void setPerfil(Perfil novoPerfil) {
        this.perfilInvestimento = novoPerfil;
    }

    /*
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ClientePerfilHistorico> clientePerfilHistorico = new ArrayList<>();

    public void addPerfilHistorico(ClientePerfilHistorico h) {
        perfilHistorico.add(h);
        h.cliente = this;
    }

     */
}
