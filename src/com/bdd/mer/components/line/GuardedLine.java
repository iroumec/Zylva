package com.bdd.mer.components.line;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.lineMultiplicity.LineMultiplicity;
import com.bdd.mer.components.line.lineShape.LineShape;
import com.bdd.mer.components.relationship.cardinality.Cardinality;
import com.bdd.mer.frame.DrawingPanel;

import java.util.ArrayList;
import java.util.List;

public class GuardedLine extends Line {

    private final Cardinality guard;

    public GuardedLine(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, LineShape lineShape, LineMultiplicity lineMultiplicity, Cardinality guard) {
        super(drawingPanel, firstComponent, secondComponent, lineShape, lineMultiplicity);
        this.guard = guard;
        guard.setLine(this);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = new ArrayList<>(guard.getComponentsForRemoval());
        out.add(guard);

        return out;

    }

}
