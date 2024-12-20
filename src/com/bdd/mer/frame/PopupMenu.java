package com.bdd.mer.frame;

import com.bdd.mer.components.Component;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {

    DrawingPanel drawingPanel;
    Component component;

    public PopupMenu(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    public void addOption(JMenuItem option) {
        this.add(option);
    }

    public void setComponent(Component component) { this.component = component; }
}
