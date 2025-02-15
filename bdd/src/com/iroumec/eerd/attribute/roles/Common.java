package com.iroumec.eerd.attribute.roles;

import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.Element;
import com.iroumec.derivation.elements.SingleElement;
import com.iroumec.derivation.elements.containers.Final;
import com.iroumec.derivation.elements.containers.Holder;
import com.iroumec.eerd.attribute.Attribute;
import com.iroumec.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.eerd.attribute.DescAttrEERComp;
import com.iroumec.eerd.attribute.presences.Presence;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Common implements Rol {

    private final static Common instance = new Common();

    private Common() {}

    public static Common getInstance() {

        return instance;
    }

    @Override
    public JPopupMenu getPopupMenu(Attribute attribute) {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem item = new JMenuItem("action.addAttribute");
        item.addActionListener(_ -> attribute.addAttribute());
        popupMenu.add(item);

        item = new JMenuItem("action.swapOptionality");
        item.addActionListener(_ -> attribute.swapPresence());
        popupMenu.add(item);

        item = new JMenuItem("action.swapMultivalued");
        item.addActionListener(_ -> attribute.swapCardinality());
        popupMenu.add(item);

        return popupMenu;
    }

    @Override
    @SuppressWarnings("unused")
    public List<Derivation> getDerivations(@NotNull DescAttrEERComp owner,
                                           @NotNull Attribute attribute,
                                           @NotNull Presence presence,
                                           @NotNull Cardinality cardinality) {



        if (cardinality.generatesDerivation()) {
            return cardinality.getDerivations(owner, attribute);
        } else {

            List<Derivation> out = new ArrayList<>();

            Derivation derivation = new Derivation(owner.getIdentifier());

            Holder holder;
            if (attribute.isCompound()) {
                // The presence will be always obligatory.
                holder = presence.getHolder();
            } else {
                holder = Final.getInstance();
            }

            Element element = new SingleElement(attribute.getIdentifier(), holder);
            derivation.addCommonElement(element);

            out.add(derivation);

            return out;
        }
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int radius) {

        int doubleRadius = radius * 2;

        g2.drawOval(x, y, doubleRadius, doubleRadius);
    }
}
