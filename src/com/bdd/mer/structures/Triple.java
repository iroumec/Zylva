package com.bdd.mer.structures;

import java.io.Serializable;

public class Triple<F, S, T> implements Serializable {
    private F first;
    private S second;
    private T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public void setFirst(F newFirst) {
        this.first = newFirst;
    }

    @SuppressWarnings("unused")
    public void setSecond(S newSecond) {
        this.second = newSecond;
    }

    @SuppressWarnings("unused")
    public void setThird(T newThird) {
        this.third = newThird;
    }
}
