package com.bdd.mer.components.attribute;

import javax.swing.*;
import java.awt.*;

final class MainRol implements Rol {

    private final static MainRol instance = new MainRol();

    private MainRol() {}

    static MainRol getInstance() {

        return instance;
    }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("action.addAttribute");
        menuItem.addActionListener(_ -> attribute.addAttribute(
                ObligatoryPresence.getInstance(),
                UnivaluedCardinality.getInstance()
        ));
        popupMenu.add(menuItem);

        return popupMenu;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.fillOval(x, y, doubleRadius, doubleRadius);

    }
}

