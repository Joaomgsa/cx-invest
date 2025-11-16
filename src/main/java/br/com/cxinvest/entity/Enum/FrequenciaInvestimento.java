package br.com.cxinvest.entity.Enum;

public enum FrequenciaInvestimento {
    ALTA,
    MEDIA,
    BAIXA;

    public String label() {
        return switch (this) {
            case ALTA -> "Alta";
            case MEDIA -> "Média";
            case BAIXA -> "Baixa";
        };
    }
}
