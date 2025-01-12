package com.bdd.mer.structures;

import java.io.Serializable;

public class Pair<T, U> implements Serializable {
    private T first;
    private U second;

    public Pair(T first, U second) {
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

    @SuppressWarnings("unused")
    public void setSecond(U newSecond) { this.second = newSecond; }
}
