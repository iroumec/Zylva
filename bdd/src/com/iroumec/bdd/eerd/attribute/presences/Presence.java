package com.iroumec.bdd.eerd.attribute.presences;

import com.iroumec.bdd.derivation.elements.Element;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;

public interface Presence extends Serializable {

    Holder getHolder();

    default void addDecoration(@NotNull Element element) {}

    Presence getOpposite();

    void draw(Graphics2D g2, int x1, int y1, int x2, int y2);
}
