package com.bdd.mer.components.atributo;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.actions.Action;

import javax.swing.*;

public class MainAttribute extends Attribute {

    /**
     * Constructs a {@code MainAttribute}.
     *
     * @param owner {@code Attribute}'s owner.
     * @param text {@code Attributes}'s text.
     * @param drawingPanel {@code DrawingPanel} where the {@code Attribute} lives.
     */
    public MainAttribute(AttributableComponent owner, String text, DrawingPanel drawingPanel) {
        super(owner, text, AttributeSymbol.MAIN, AttributeArrow.NON_OPTIONAL, AttributeEnding.NON_MULTIVALUED, drawingPanel);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_ATTRIBUTE,
                Action.RENAME,
                Action.DELETE
        );

    }

}
