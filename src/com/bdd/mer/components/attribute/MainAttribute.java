package com.bdd.mer.components.attribute;

import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.GUI.Diagram;

import javax.swing.*;

public class MainAttribute extends Attribute {

    /**
     * Constructs a {@code MainAttribute}.
     *
     * @param owner {@code Attribute}'s owner.
     * @param text {@code Attributes}'s text.
     * @param diagram {@code Diagram} where the {@code Attribute} lives.
     */
    public MainAttribute(AttributableEERComponent owner, String text, Diagram diagram) {
        super(owner, text, AttributeSymbol.MAIN, AttributeArrow.NON_OPTIONAL, AttributeEnding.NON_MULTIVALUED, diagram);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("action.addAttribute");
        menuItem.addActionListener(_ -> this.addRawAttribute());
        popupMenu.add(menuItem);

        // noinspection DuplicatedCode
        menuItem = new JMenuItem("action.rename");
        menuItem.addActionListener(_ -> this.rename());
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("action.delete");
        menuItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(menuItem);

        return popupMenu;
    }
}
