package com.bdd.mer.components.hierarchy;

public enum HierarchyType {

    DISJUNCT("d"),
    OVERLAPPING("o");

    final String symbol;

    HierarchyType(String symbol) { this. symbol = symbol; }

    public String getSymbol() { return this.symbol; }

}
