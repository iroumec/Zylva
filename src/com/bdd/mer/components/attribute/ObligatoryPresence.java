package com.bdd.mer.components.attribute;

import java.awt.*;

public final class ObligatoryPresence implements Presence {

    private static final ObligatoryPresence instance = new ObligatoryPresence();

    private ObligatoryPresence() {}

    public static ObligatoryPresence getInstance() {
        return instance;
    }

    @Override
    public Presence getOpposite() {

        return OptionalPresence.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        g2.drawLine(x1, y1, x2, y2);
    }
}
