package com.bdd.mer.components.atributo;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.actions.Action;

import javax.swing.*;

public class MainAttribute extends Attribute {

    public MainAttribute(AttributableComponent owner, String text, DrawingPanel drawingPanel) {
        super(owner, text, AttributeSymbol.MAIN, AttributeArrow.NON_OPTIONAL, AttributeEnding.NON_MULTIVALUED, drawingPanel);
    }

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
