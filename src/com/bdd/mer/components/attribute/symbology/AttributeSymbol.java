package com.bdd.mer.components.attribute.symbology;

import java.io.Serializable;

public enum AttributeSymbol implements Serializable {
    MAIN("● "),
    ALTERNATIVE("◐ "),
    COMMON("○ ");

    private final String symbol;

    AttributeSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }
}
