package com.bdd.mer.components.attribute.rol;

import java.awt.*;

public class Alternative implements Rol {

    private static final Alternative instance = new Alternative();

    private Alternative() {}

    public static Alternative getInstance() {

        return instance;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
        g2.fillArc(x, y, doubleRadius, doubleRadius, 90, 180);  // Left middle filled.
    }
}
