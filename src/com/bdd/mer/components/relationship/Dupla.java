package com.bdd.mer.components.relationship;

import java.io.Serializable;

public class Dupla<T, U> implements Serializable {
    private T first;
    private U second;

    public Dupla(T first, U second) {
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
