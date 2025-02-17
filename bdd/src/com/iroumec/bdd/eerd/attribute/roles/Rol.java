package com.iroumec.bdd.eerd.attribute.roles;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.containers.Final;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import com.iroumec.bdd.eerd.attribute.Attribute;
import com.iroumec.bdd.eerd.attribute.DescriptiveAttributable;
import com.iroumec.bdd.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.bdd.eerd.attribute.presences.Presence;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPopupMenu;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.List;

public interface Rol extends Serializable {

    /**
     * @param attribute Attribute in which the JPopupMenu will take effect.
     * @return A {@code JPopupMenu} according to the rol, taking into consideration the attribute.
     */
    JPopupMenu getPopupMenu(Attribute attribute);

    List<Derivation> getDerivations(@NotNull DescriptiveAttributable owner,
                                    @NotNull Attribute attribute,
                                    @NotNull Presence presence,
                                    @NotNull Cardinality cardinality);

    void draw(Graphics2D g2, int x, int y, int radius);

    default Holder getHolder(@NotNull Attribute attribute, @NotNull Presence presence) {

        if (attribute.isCompound()) {
            // The presence will be always obligatory.
            return presence.getHolder();
        }

        return Final.getInstance();
    }
}
