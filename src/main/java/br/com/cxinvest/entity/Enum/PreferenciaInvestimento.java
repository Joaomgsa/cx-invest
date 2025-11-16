package br.com.cxinvest.entity.Enum;

public enum PreferenciaInvestimento {
    LIQUIDEZ,
    RENTABILIDADE;

    public String label() {
        return switch (this) {
            case LIQUIDEZ -> "Liquidez";
            case RENTABILIDADE -> "Rentabilidade";
        };
    }
}
