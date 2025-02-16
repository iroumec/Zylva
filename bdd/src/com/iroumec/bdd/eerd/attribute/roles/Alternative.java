package com.iroumec.bdd.eerd.attribute.roles;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.Element;
import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.derivation.elements.containers.Final;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import com.iroumec.bdd.eerd.attribute.Attribute;
import com.iroumec.bdd.eerd.attribute.DescriptiveAttributable;
import com.iroumec.bdd.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.bdd.eerd.attribute.cardinalities.Univalued;
import com.iroumec.bdd.eerd.attribute.presences.Presence;
import com.iroumec.userPreferences.LanguageManager;
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
    @SuppressWarnings("Duplicates")
    public List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner,
                                           @NotNull Attribute attribute,
                                           @NotNull Presence presence,
                                           @NotNull Cardinality cardinality) {

        List<Derivation> out = new ArrayList<>();
        Derivation derivation = new Derivation(owner.getText());

        // TODO: transform this in function.
        Holder holder;
        if (attribute.isCompound()) {
            // The presence will be always obligatory.
            holder = presence.getHolder();
        } else {
            holder = Final.getInstance();
        }

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
