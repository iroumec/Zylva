package com.bdd.mer.components.hierarchy;

import java.io.Serializable;

public enum HierarchyExclusivity implements Serializable {

    DISJUNCT("d"),
    OVERLAPPING("o");

    final String symbol;

    HierarchyExclusivity(String symbol) { this. symbol = symbol; }

    public String getSymbol() { return this.symbol; }

}
