package com.bdd.mer.components.relationship;

import java.io.Serializable;

public class Duplex<T, U> implements Serializable {
    private T first;
    private U second;

    public Duplex(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public void setFirst(T newFirst) {
        this.first = newFirst;
    }

    public void setSecond(U newSecond) {
        this.second = newSecond;
    }
}
