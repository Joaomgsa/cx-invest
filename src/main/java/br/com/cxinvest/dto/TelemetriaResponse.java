package br.com.cxinvest.dto;

import java.time.LocalDate;
import java.util.List;

public class TelemetriaResponse {
    private List<ServicoTelemetria> servicos;
    private Periodo periodo;

    public List<ServicoTelemetria> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicoTelemetria> servicos) {
        this.servicos = servicos;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public static class ServicoTelemetria {
        private String nome;
        private Integer quantidadeChamadas;
        private Long mediaTempoRespostaMs;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Integer getQuantidadeChamadas() {
            return quantidadeChamadas;
        }

        public void setQuantidadeChamadas(Integer quantidadeChamadas) {
            this.quantidadeChamadas = quantidadeChamadas;
        }

        public Long getMediaTempoRespostaMs() {
            return mediaTempoRespostaMs;
        }

        public void setMediaTempoRespostaMs(Long mediaTempoRespostaMs) {
            this.mediaTempoRespostaMs = mediaTempoRespostaMs;
        }
    }

    public static class Periodo {
        private LocalDate inicio;
        private LocalDate fim;

        public LocalDate getInicio() {
            return inicio;
        }

        public void setInicio(LocalDate inicio) {
            this.inicio = inicio;
        }

        public LocalDate getFim() {
            return fim;
        }

        public void setFim(LocalDate fim) {
            this.fim = fim;
        }
    }
}
