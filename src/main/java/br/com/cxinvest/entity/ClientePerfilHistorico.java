package br.com.cxinvest.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_cliente_perfil_historico")
public class ClientePerfilHistorico {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    public Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_anterior_id")
    public Perfil perfilAnterior;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_novo_id", nullable = false)
    public Perfil perfilNovo;

    @Column(nullable = false)
    public Instant criadoEm = Instant.now();

    @Column(length = 1000)
    public String motivo;

    @Column(columnDefinition = "Text")
    public String metadados;

    @Column
    public Integer pontuacaoNoMomento;

    public ClientePerfilHistorico() {}

    // Construtor usado pelo serviço: (cliente, perfilAnterior, perfilNovo, motivo, metadados, pontuacaoNoMomento)
    public ClientePerfilHistorico(Cliente cliente, Perfil perfilAnterior, Perfil perfilNovo, String motivo, String metadados, Integer pontuacaoNoMomento) {
        this.cliente = cliente;
        this.perfilAnterior = perfilAnterior;
        this.perfilNovo = perfilNovo;
        this.criadoEm = Instant.now();
        this.motivo = motivo;
        this.metadados = metadados;
        this.pontuacaoNoMomento = pontuacaoNoMomento;
    }

    // Optional: construtor completo
    public ClientePerfilHistorico(Long id, Cliente cliente, Perfil perfilAnterior, Perfil perfilNovo, Instant criadoEm, String motivo, String metadados, Integer pontuacaoNoMomento) {
        this.id = id;
        this.cliente = cliente;
        this.perfilAnterior = perfilAnterior;
        this.perfilNovo = perfilNovo;
        this.criadoEm = criadoEm != null ? criadoEm : Instant.now();
        this.motivo = motivo;
        this.metadados = metadados;
        this.pontuacaoNoMomento = pontuacaoNoMomento;
    }

}
