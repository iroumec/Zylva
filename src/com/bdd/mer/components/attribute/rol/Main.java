package com.bdd.mer.components.attribute.rol;

import java.awt.*;

public class Main implements Rol {

    private final static Main instance = new Main();

    private Main() {}

    public static Main getInstance() {

        return instance;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.fillOval(x, y, doubleRadius, doubleRadius);

    }
}

