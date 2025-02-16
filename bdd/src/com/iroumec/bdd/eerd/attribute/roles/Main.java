package com.iroumec.bdd.eerd.attribute.roles;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.derivation.elements.containers.Final;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import com.iroumec.bdd.eerd.attribute.Attribute;
import com.iroumec.bdd.eerd.attribute.DescriptiveAttributable;
import com.iroumec.bdd.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.bdd.eerd.attribute.cardinalities.Univalued;
import com.iroumec.bdd.eerd.attribute.presences.Obligatory;
import com.iroumec.bdd.eerd.attribute.presences.Presence;
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
        Derivation derivation = new Derivation(owner.getText());

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

