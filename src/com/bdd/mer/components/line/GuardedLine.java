package com.bdd.mer.components.line;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.line.guard.Guard;
import com.bdd.mer.components.line.guard.cardinality.Cardinality;
import com.bdd.mer.frame.DrawingPanel;

import java.util.ArrayList;
import java.util.List;

public class GuardedLine extends Line {

    private final Guard guard;

     GuardedLine(Init<?> init) {
        super(init);
        this.guard = init.guard;
        this.guard.setLine(this);
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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                     Builder                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    protected static abstract class Init<T extends Init<T>> extends Line.Init<T> {

        // Required parameter.
        private final Guard guard;

        public Init(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, Guard guard) {
            super(drawingPanel, firstComponent, secondComponent);
            this.guard = guard;
        }

        public GuardedLine build() {
            return new GuardedLine(this);
        }
    }

    public static class Builder extends Init<Builder> {

        public Builder(DrawingPanel drawingPanel, Component firstComponent, Component secondComponent, Guard guard) {
            super(drawingPanel, firstComponent, secondComponent, guard);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    @Override
    public String toString() {
        return this.guard.toString();
    }

    @Override
    public String getText() { return this.guard.getText(); }
}
