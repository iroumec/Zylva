package com.iroumec.eerd.components.attribute.internal.presences;

import main.java.com.iroumec.bdd.derivation.elements.Element;
import main.java.com.iroumec.bdd.derivation.elements.containers.Holder;
import main.java.com.iroumec.bdd.derivation.elements.containers.Replacer;
import main.java.com.iroumec.bdd.derivation.elements.containers.sources.Common;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class Obligatory implements Presence {

    private static final Obligatory instance = new Obligatory();

    private Obligatory() {}

    public static Obligatory getInstance() {
        return instance;
    }

    @Override
    public Holder getHolder() {
        return new Replacer(Common.getInstance());
    }

    @Override
    public void addDecoration(@NotNull Element element) {
        // Empty on purpose.
    }

    @Override
    public Presence getOpposite() {

        return Optional.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        g2.drawLine(x1, y1, x2, y2);
    }
}
