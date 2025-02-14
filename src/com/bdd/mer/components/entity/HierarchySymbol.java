package com.bdd.mer.components.entity;

import java.io.Serializable;

public enum HierarchySymbol implements Serializable {

    DISJUNCT("d"),
    OVERLAPPING("o");

    final String symbol;

    HierarchySymbol(String symbol) { this. symbol = symbol; }

    @Override
    public String toString() {
        return this.symbol;
    }
}
