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

    // Retorna peso numérico para cálculo de perfil
    public int weight() {
        return switch (this) {
            case ALTA -> 2;
            case MEDIA -> 1;
            case BAIXA -> 0;
        };
    }
}
