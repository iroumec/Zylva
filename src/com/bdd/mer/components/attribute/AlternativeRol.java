package com.bdd.mer.components.attribute;

import javax.swing.*;
import java.awt.*;

final class AlternativeRol implements Rol {

    private static final AlternativeRol instance = new AlternativeRol();

    private AlternativeRol() {}

    static AlternativeRol getInstance() {

        return instance;
    }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> attribute.addAttribute(UnivaluedCardinality.getInstance()));
        popupMenu.add(item);

        item = new JMenuItem("action.swapOptionality");
        item.addActionListener(_ -> attribute.swapPresence());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
        g2.fillArc(x, y, doubleRadius, doubleRadius, 90, 180);  // Left middle filled.
    }
}
