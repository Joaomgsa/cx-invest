package br.com.cxinvest.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "request_metrics")
public class RequestMetric extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String path;

    @Column(nullable = false)
    public String method;

    @Column(nullable = false)
    public int status;

    @Column(name = "response_time_ms", nullable = false)
    public long responseTimeMs;

    @Column(nullable = false)
    public Instant timestamp;


    public RequestMetric() {}

    public RequestMetric(String path, String method, int status, long responseTimeMs, Instant timestamp) {
        this.path = path;
        this.method = method;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.timestamp = timestamp;
    }
}
