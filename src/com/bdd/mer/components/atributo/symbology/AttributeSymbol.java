package com.bdd.mer.components.atributo.symbology;

public enum AttributeSymbol {
    MAIN("● "),
    ALTERNATIVE("◐ "),
    COMMON("○ ");

    private final String symbol;

    AttributeSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return this.symbol; }

}
