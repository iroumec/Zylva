package com.bdd.mer.components.attribute.rol;

import java.awt.*;

public class Common implements Rol {

    private final static Common instance = new Common();

    private Common() {}

    public static Common getInstance() {

        return instance;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
    }
}
