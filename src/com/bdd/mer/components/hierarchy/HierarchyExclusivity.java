package com.bdd.mer.components.hierarchy;

public enum HierarchyExclusivity {

    DISJUNCT("d"),
    OVERLAPPING("o");

    final String symbol;

    HierarchyExclusivity(String symbol) { this. symbol = symbol; }

    public String getSymbol() { return this.symbol; }

}
