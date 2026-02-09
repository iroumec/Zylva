package com.zylva.bdd.eerd.hierarchy.exclusivity;

public final class Overlap implements Exclusivity {

    private final static String SYMBOL = "o";
    private final static Overlap INSTANCE = new Overlap();

    private Overlap() {}

    public static Overlap getInstance() { return INSTANCE; }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
