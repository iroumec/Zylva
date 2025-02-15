package com.iroumec.eerd.hierarchy;

import java.io.Serializable;

public enum HierarchySymbol implements Serializable {

    // TODO: this must be improved.

    DISJUNCT("d"),
    OVERLAPPING("o");

    final String symbol;

    HierarchySymbol(String symbol) { this. symbol = symbol; }

    @Override
    public String toString() {
        return this.symbol;
    }
}
