package com.bdd.mer.estatica.coleccion;

import java.io.Serializable;

public class Dupla<T, U> implements Serializable {
    private final T primero;
    private final U segundo;

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
}
