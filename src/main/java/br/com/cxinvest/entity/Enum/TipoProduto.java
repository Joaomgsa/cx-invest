package br.com.cxinvest.entity.Enum;

import java.util.Optional;

public enum TipoProduto {
    CDB,
    LCI,
    LCA,
    TESOURO_DIRETO,
    FUNDO;

    /**
     * Tenta mapear uma string (label) para um TipoProduto, tratando case e espaços.
     */
    public static Optional<TipoProduto> fromLabel(String label) {
        if (label == null) return Optional.empty();
        String normalized = label.trim().toUpperCase().replaceAll("[^A-Z0-9]", "_");
        // alguns labels podem ser "TESOURO DIRETO" ou "Tesouro Direto"
        if (normalized.equals("TESOURO_DIRETO") || normalized.equals("TESOURO_DIRETO")) {
            return Optional.of(TipoProduto.TESOURO_DIRETO);
        }
        try {
            return Optional.of(TipoProduto.valueOf(normalized));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

