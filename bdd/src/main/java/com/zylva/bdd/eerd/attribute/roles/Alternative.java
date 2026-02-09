package com.zylva.bdd.eerd.attribute.roles;

import com.zylva.bdd.derivation.Derivation;
import com.zylva.bdd.derivation.elements.Element;
import com.zylva.bdd.derivation.elements.ElementDecorator;
import com.zylva.bdd.derivation.elements.SingleElement;
import com.zylva.bdd.derivation.elements.containers.Holder;
import com.zylva.bdd.eerd.attribute.Attribute;
import com.zylva.bdd.eerd.attribute.DescriptiveAttributable;
import com.zylva.bdd.eerd.attribute.cardinalities.Cardinality;
import com.zylva.bdd.eerd.attribute.cardinalities.Univalued;
import com.zylva.bdd.eerd.attribute.presences.Presence;
import com.zylva.common.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Alternative implements Rol {

    private static final Alternative instance = new Alternative();

    private Alternative() {}

    public static Alternative getInstance() {

        return instance;
    }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem(LanguageManager.getMessage("action.addAttribute"));
        item.addActionListener(_ -> attribute.addAttribute(Univalued.getInstance()));
        popupMenu.add(item);

        item = new JMenuItem(LanguageManager.getMessage("action.swapPresence"));
        item.addActionListener(_ -> attribute.swapPresence());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    public List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner,
                                           @NotNull Attribute attribute,
                                           @NotNull Presence presence,
                                           @NotNull Cardinality cardinality) {

        List<Derivation> out = new ArrayList<>();
        Derivation derivation = new Derivation(owner.getText());

        Holder holder = this.getHolder(attribute, presence);

        Element element = new SingleElement(attribute.getIdentifier(), holder);
        element.addDecoration(ElementDecorator.ALTERNATIVE);
        presence.addDecoration(element);

        derivation.addCommonElement(element);
        out.add(derivation);

        return out;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
        g2.fillArc(x, y, doubleRadius, doubleRadius, 90, 180);  // Left middle filled.
    }
}
