package com.iroumec.bdd.derivation.elements;

import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.containers.Final;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SingleElement extends Element {

    private final String name;
    private final Holder holder;

    public SingleElement(String name) {
        this(name, Final.getInstance());
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
    public boolean removeElement(Element element) {
        return false;
    }

    @Override
    public void clearAllDecorations() {
        super.emptyDecorations();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SingleElement element = (SingleElement) o;
        return Objects.equals(name, element.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
