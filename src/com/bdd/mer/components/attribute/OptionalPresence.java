package com.bdd.mer.components.attribute;

import java.awt.*;

public final class OptionalPresence implements Presence {

    private final static OptionalPresence instance = new OptionalPresence();

    private OptionalPresence() {}

    public static OptionalPresence getInstance() {

        return instance;
    }

    @Override
    public Presence getOpposite() {

        return ObligatoryPresence.getInstance();
    }

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        Stroke previousStroke = g2.getStroke();

        // Dashed pattern.
        // TODO: constants should be converted to variables.
        float[] dashPattern = {2f, 2f};  // 5 pixels on, 5 pixels off
        BasicStroke dashedStroke = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10f, dashPattern,
                0f
        );

        g2.setStroke(dashedStroke);

        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(previousStroke);
    }
}
