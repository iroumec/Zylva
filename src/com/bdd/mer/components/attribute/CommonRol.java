package com.bdd.mer.components.attribute;

import javax.swing.*;
import java.awt.*;

final class CommonRol implements Rol {

    private final static CommonRol instance = new CommonRol();

    private CommonRol() {}

    static CommonRol getInstance() {

        return instance;
    }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> attribute.addAttribute());
        popupMenu.add(item);

        item = new JMenuItem("action.swapOptionality");
        item.addActionListener(_ -> attribute.swapPresence());
        popupMenu.add(item);

        item = new JMenuItem("action.swapMultivalued");
        item.addActionListener(_ -> attribute.swapCardinality());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
    }
}
