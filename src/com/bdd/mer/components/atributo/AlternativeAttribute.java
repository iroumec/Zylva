package com.bdd.mer.components.atributo;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;

public class AlternativeAttribute extends Attribute{

    public AlternativeAttribute(AttributableComponent owner, String text, AttributeArrow arrow) {
        super(owner, text, AttributeSymbol.ALTERNATIVE, arrow, AttributeEnding.NON_MULTIVALUED);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu alternativeAttributePopupMenu = new PopupMenu(drawingPanel);

        JMenuItem renameAttribute = new JMenuItem("Rename");
        renameAttribute.addActionListener(_ -> drawingPanel.getActioner().renameComponent(this));

        JMenuItem deleteAttribute = new JMenuItem("Delete");
        deleteAttribute.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        JMenuItem addAttribute = new JMenuItem("Add attribute");
        addAttribute.addActionListener(_ -> drawingPanel.getActioner().addAttribute(this, AttributeSymbol.COMMON));

        JMenuItem swapOptionality = new JMenuItem("Swap optionality");
        swapOptionality.addActionListener(_ -> drawingPanel.getActioner().changeOptionality(this));

        alternativeAttributePopupMenu.addOption(renameAttribute);
        alternativeAttributePopupMenu.addOption(deleteAttribute);
        alternativeAttributePopupMenu.addOption(addAttribute);
        alternativeAttributePopupMenu.addOption(swapOptionality);

        return alternativeAttributePopupMenu;

    }

}
