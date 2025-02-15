package com.iroumec.eerd.attribute.roles;

import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.SingleElement;
import com.iroumec.derivation.elements.containers.Final;
import com.iroumec.derivation.elements.containers.Holder;
import com.iroumec.eerd.attribute.Attribute;
import com.iroumec.eerd.attribute.DescriptiveAttributable;
import com.iroumec.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.eerd.attribute.cardinalities.Univalued;
import com.iroumec.eerd.attribute.presences.Obligatory;
import com.iroumec.eerd.attribute.presences.Presence;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Main implements Rol {

    private final static Main INSTANCE = new Main();

    private Main() {}

    public static Main getInstance() { return INSTANCE; }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("action.addAttribute");
        menuItem.addActionListener(_ -> attribute.addAttribute(
                Obligatory.getInstance(),
                Univalued.getInstance()
        ));
        popupMenu.add(menuItem);

        return popupMenu;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner,
                                           @NotNull Attribute attribute,
                                           @NotNull Presence presence,
                                           @NotNull Cardinality cardinality) {

        List<Derivation> out = new ArrayList<>();
        Derivation derivation = new Derivation(owner.getIdentifier());

        Holder holder;
        if (attribute.isCompound()) {
            // The presence will be always obligatory.
            holder = presence.getHolder();
        } else {
            holder = Final.getInstance();
        }

        derivation.addIdentificationElement(new SingleElement(attribute.getIdentifier(), holder));
        out.add(derivation);

        return out;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.fillOval(x, y, doubleRadius, doubleRadius);

    }
}

