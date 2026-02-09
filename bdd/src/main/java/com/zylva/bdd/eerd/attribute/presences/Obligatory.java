package com.zylva.bdd.eerd.attribute.presences;

import com.zylva.bdd.derivation.elements.containers.Holder;
import com.zylva.bdd.derivation.elements.containers.Replacer;
import com.zylva.bdd.derivation.elements.containers.sources.Common;

import java.awt.*;

public final class Obligatory implements Presence {

    private static final Obligatory INSTANCE = new Obligatory();

    private Obligatory() {}

    public static Obligatory getInstance() {
        return INSTANCE;
    }

    @Override
    public Holder getHolder() {
        return new Replacer(Common.getInstance());
    }

    @Override
    public Presence getOpposite() { return Optional.getInstance(); }

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) { g2.drawLine(x1, y1, x2, y2); }
}
