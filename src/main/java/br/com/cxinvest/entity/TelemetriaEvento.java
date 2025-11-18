package br.com.cxinvest.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_telemetria_events")
public class TelemetriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "servico", nullable = false)
    public String servico;

    @Column(name = "tempo_resposta_ms", nullable = false)
    public Integer tempoRespostaMs;

    @Column(name = "data_evento", nullable = false)
    public OffsetDateTime dataEvento;

}

