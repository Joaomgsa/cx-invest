package br.com.cxinvest.dto.produto;

/**
 * Enum que representa o nível de risco utilizado nas recomendações de produto.
 */
public enum Risco {
    BAIXO,
    MEDIO,
    ALTO;

    /**
     * Faz o mapeamento do nome do perfil para o nível de risco.
     * Aceita comparação case-insensitive.
     *
     * @param nome nome do perfil (ex.: "Conservador")
     * @return Risco correspondente, ou MEDIO como default quando desconhecido
     */
    public static Risco fromPerfilNome(String nome) {
        if (nome == null) {
            return MEDIO; // default
        }
        return switch (nome.trim().toLowerCase()) {
            case "conservador" -> BAIXO;
            case "moderado" -> MEDIO;
            case "agressivo" -> ALTO;
            default -> MEDIO;
        };
    }
}

