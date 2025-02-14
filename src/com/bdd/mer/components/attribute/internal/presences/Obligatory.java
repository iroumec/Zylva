package com.bdd.mer.components.attribute.internal.presences;

import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.containers.Holder;
import com.bdd.mer.derivation.elements.containers.Replacer;
import com.bdd.mer.derivation.elements.containers.sources.Common;
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
