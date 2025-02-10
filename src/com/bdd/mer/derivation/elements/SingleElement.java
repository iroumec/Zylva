package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.container.Final;
import com.bdd.mer.derivation.elements.container.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SingleElement extends Element {

    private final String name;
    private final Holder holder;

    public SingleElement(String name) {
        this(name, new Final());
    }

    public SingleElement(String name, Holder holder) {
        this.name = name;
        this.holder = holder;
    }

    public String getName() {
        return this.name;
    }

    public boolean generatesConstraints() {
        return holder.generatesConstraints();
    }

    public boolean needsRename() { return holder.needsRename(); }

    @Nullable
    public Element abstractElements(Derivation derivation) {
        return holder.abstractReplacement(derivation);
    }

    @Override
    public String toString() {

        return super.toString() + "[" + this.name + "]";
    }

    @Override
    public String formatToHTML() {
        return super.applyDecorators(this.name);
    }

    @Override
    public int getNumberOfElements() {
        return 1;
    }

    @Override
    public List<SingleElement> getPartitions() {

        List<SingleElement> out = new ArrayList<>();

        out.add(this);

        return out;
    }

    @Override
    public void replace(SingleElement element, Element replacement) {
        // Do nothing.
    }

    @Override
    public List<SingleElement> getReplacementsNeeded() {

        List<SingleElement> out = new ArrayList<>();

        if (holder.mayNeedReplacement()) {
            out.add(this);
        }

        return out;
    }

    @Override
    public Element getCopy() {

        Element copy = new SingleElement(this.name, this.holder);
        this.copyDecoratorsTo(copy);

        return copy;
    }
}
