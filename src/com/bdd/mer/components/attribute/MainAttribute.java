package com.bdd.mer.components.attribute;

import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.GUI.Diagram;
import com.bdd.mer.actions.Action;

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

        return this.getActionManager().getPopupMenu(
                this,
                Action.ADD_ATTRIBUTE,
                Action.RENAME,
                Action.DELETE
        );

    }
}
