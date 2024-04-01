package com.bdd.mer.estatica.coleccion;

public class Dupla<T, U> {
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
