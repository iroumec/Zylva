package com.bdd.mer.components.attribute;

import com.bdd.mer.components.attribute.symbology.AttributeOptionality;
import com.bdd.mer.components.attribute.symbology.AttributeMultivalued;
import com.bdd.mer.components.attribute.symbology.AttributeType;

import javax.swing.*;

public class MainAttribute extends Attribute {

    /**
     * Constructs a {@code MainAttribute}.
     *
     * @param owner {@code Attribute}'s owner.
     * @param text {@code Attributes}'s text.
     */
    public MainAttribute(AttributableEERComponent owner, String text) {
        super(owner, text, AttributeType.MAIN, AttributeOptionality.NON_OPTIONAL, AttributeMultivalued.NON_MULTIVALUED);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("action.addAttribute");
        menuItem.addActionListener(_ -> this.addRawAttribute());
        // noinspection DuplicatedCode
        popupMenu.add(menuItem);

        // noinspection DuplicatedCode
        menuItem = new JMenuItem("action.rename");
        menuItem.addActionListener(_ -> this.rename());
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("action.setForDelete");
        menuItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(menuItem);

        return popupMenu;
    }

    public boolean isMain() { return true; }
}
