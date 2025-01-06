package com.bdd.mer.components.hierarchy;

public enum HierarchyType {

    DISJUNT("d"),
    OVERLAPPING("o");

    String symbol;

    HierarchyType(String symbol) { this. symbol = symbol; }

    public String getSymbol() { return this.symbol; }

}
