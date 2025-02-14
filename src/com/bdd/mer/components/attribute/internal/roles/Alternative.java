package com.bdd.mer.components.attribute.internal.roles;

import com.bdd.mer.components.attribute.external.Attribute;
import com.bdd.mer.components.attribute.external.DescAttrEERComp;
import com.bdd.mer.components.attribute.internal.cardinalities.Cardinality;
import com.bdd.mer.components.attribute.internal.cardinalities.Univalued;
import com.bdd.mer.components.attribute.internal.presences.Presence;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.elements.containers.Final;
import com.bdd.mer.derivation.elements.containers.Holder;
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

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> attribute.addAttribute(Univalued.getInstance()));
        popupMenu.add(item);

        item = new JMenuItem("action.swapOptionality");
        item.addActionListener(_ -> attribute.swapPresence());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Derivation> getDerivations(@NotNull DescAttrEERComp owner,
                                           @NotNull Attribute attribute,
                                           @NotNull Presence presence,
                                           @NotNull Cardinality cardinality) {

        List<Derivation> out = new ArrayList<>();
        Derivation derivation = new Derivation(owner.getIdentifier());

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
