package com.iroumec.eerd.attribute.roles;

import com.iroumec.derivation.Derivation;
import com.iroumec.eerd.attribute.Attribute;
import com.iroumec.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.eerd.attribute.DescAttrEERComp;
import com.iroumec.eerd.attribute.presences.Presence;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPopupMenu;
import java.awt.Graphics2D;
import java.util.List;

public interface Rol {

    /**
     * @param attribute Attribute in which the JPopupMenu will take effect.
     * @return A {@code JPopupMenu} according to the rol, taking into consideration the attribute.
     */
    JPopupMenu getPopupMenu(Attribute attribute);

    // TODO: is necessary to pass the presence and cardinality or could they be obtained from the attribute?
    List<Derivation> getDerivations(@NotNull DescAttrEERComp owner,
                                    @NotNull Attribute attribute,
                                    @NotNull Presence presence,
                                    @NotNull Cardinality cardinality);

    void draw(Graphics2D g2, int x, int y, int radius);
}
