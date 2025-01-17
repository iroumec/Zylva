package com.bdd.mer.components.atributo.symbology;

import java.io.Serializable;

public enum AttributeSymbol implements Serializable {
    MAIN("● "),
    ALTERNATIVE("◐ "),
    COMMON("○ ");

    private final String symbol;

    AttributeSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return this.symbol; }

}
