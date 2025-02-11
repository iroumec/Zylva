package com.bdd.GUI.structures;

import java.io.Serializable;

public record Pair<T, U>(T first, U second) implements Serializable { }