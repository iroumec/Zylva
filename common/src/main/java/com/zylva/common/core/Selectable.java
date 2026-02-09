package com.zylva.common.core;

import java.awt.*;

public interface Selectable {

    /**
     * @return {@code TRUE} if the entity is being selected.
     */
    boolean isSelected();

    /**
     * Changes the selection state of the component.
     *
     * @param isSelected New selection state.
     */
    void setSelected(boolean isSelected);

    default boolean canBeSelectedBySelectionArea() { return true; }

    /**
     * Changes the color and the stroke to highlight that the component is being selected.
     */
    default void setSelectionOptions(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(120, 190, 235));
        graphics2D.setStroke(new BasicStroke(2));
    }
}
