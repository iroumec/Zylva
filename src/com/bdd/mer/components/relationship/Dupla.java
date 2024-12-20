package com.bdd.mer.components.relationship;

import java.io.Serializable;

public class Dupla<T, U> implements Serializable {
    private T primero;
    private U segundo;

    public Dupla(T primero, U segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }

    public T getPrimero() {
        return primero;
    }

    public U getSegundo() {
        return segundo;
    }

    public void setPrimero(T newFirst) {
        this.primero = newFirst;
    }

    public void setSegundo(U segundo) {
        this.segundo = segundo;
    }
}
