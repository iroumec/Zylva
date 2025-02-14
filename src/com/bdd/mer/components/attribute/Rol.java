package com.bdd.mer.components.attribute;

import javax.swing.*;
import java.awt.*;

public interface Rol {

    /**
     * @param attribute Attribute in which the JPopupMenu will take effect.
     * @return A {@code JPopupMenu} according to the rol, taking into consideration the attribute.
     */
    JPopupMenu getPopupMenu(Attribute attribute);

    void draw(Graphics2D g2, int x, int y, int radius);
}
