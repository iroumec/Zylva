package com.zylva.bdd.eerd.attribute.presences;

import com.zylva.bdd.derivation.elements.Element;
import com.zylva.bdd.derivation.elements.ElementDecorator;
import com.zylva.bdd.derivation.elements.containers.Holder;
import com.zylva.bdd.derivation.elements.containers.Replacer;
import com.zylva.bdd.derivation.elements.containers.sources.Common;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class Optional implements Presence {

    private final static Optional INSTANCE = new Optional();

    private Optional() {}

    public static Optional getInstance() { return INSTANCE; }

    @Override
    public Holder getHolder() {
        return new Replacer(Common.getInstance(), ElementDecorator.OPTIONAL);
    }

    @Override
    public void addDecoration(@NotNull Element element) {
        element.addDecoration(ElementDecorator.OPTIONAL);
    }

    @Override
    public Presence getOpposite() { return Obligatory.getInstance(); }

    @Override
    public void draw(Graphics2D g2, int x1, int y1, int x2, int y2) {

        Stroke previousStroke = g2.getStroke();

        // Dashed pattern.
        // TODO: I cannot make them final static due to serialization.
        float[] dashPattern = {2f, 2f};  // 5 pixels on, 5 pixels off
        BasicStroke dashedStroke = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10f, dashPattern,
                0f
        );

        g2.setStroke(dashedStroke);

        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(previousStroke);
    }
}
