package com.bdd.mer.components.atributo;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.atributo.symbology.AttributeArrow;
import com.bdd.mer.components.atributo.symbology.AttributeEnding;
import com.bdd.mer.components.atributo.symbology.AttributeSymbol;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;

public class MainAttribute extends Attribute {

    public MainAttribute(AttributableComponent owner, String text) {
        super(owner, text, AttributeSymbol.MAIN, AttributeArrow.NON_OPTIONAL, AttributeEnding.NON_MULTIVALUED);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu mainAttributePopupMenu = new PopupMenu(drawingPanel);

        JMenuItem renameAttribute = new JMenuItem("Rename");
        renameAttribute.addActionListener(_ -> drawingPanel.getActioner().renameComponent(this));

        JMenuItem deleteAttribute = new JMenuItem("Delete");
        deleteAttribute.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        JMenuItem addAttribute = new JMenuItem("Add attribute");
        addAttribute.addActionListener(_ -> drawingPanel.getActioner().addAttribute(this, AttributeSymbol.COMMON));

        mainAttributePopupMenu.addOption(renameAttribute);
        mainAttributePopupMenu.addOption(deleteAttribute);
        mainAttributePopupMenu.addOption(addAttribute);

        return mainAttributePopupMenu;

    }

}
