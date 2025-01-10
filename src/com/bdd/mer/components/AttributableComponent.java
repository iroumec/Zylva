package com.bdd.mer.components;

import com.bdd.mer.components.atributo.Attribute;
import com.bdd.mer.frame.DrawingPanel;

import java.util.ArrayList;
import java.util.List;

public abstract class AttributableComponent extends Component {

    private final List<Attribute> attributes;

    protected AttributableComponent(String text, int x, int y, DrawingPanel drawingPanel) {
        super(text, x, y, drawingPanel);

        this.attributes = new ArrayList<>();
    }

    public void addAttribute(Attribute attribute) { this.attributes.add(attribute); }

    public void removeAttribute(Attribute attribute) { this.attributes.remove(attribute); }

    public int getAttributePosition(Attribute attribute) {

        int out = 0;
        List<Attribute> attributes = this.getAttributes();

        for (Attribute attributeInEntity : attributes) {

            if (attributeInEntity.equals(attribute)) {
                return out;
            }

            out++;
            out += attributeInEntity.getNumberOfAttributes();

        }

        return -1;

    }

    public List<Attribute> getAttributes() {

        List<Attribute> out = new ArrayList<>(this.attributes);

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getAttributes());
        }

        return out;
    }

    public int getNumberOfAttributes() {

        int out = this.attributes.size();

        for (Attribute attribute : attributes) {
            out += attribute.getNumberOfAttributes();
        }

        return out;
    }

    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getComponentsForRemoval());
        }

        return out;
    }

    public void setSelected(boolean isSelected) {

        super.setSelected(isSelected);

        for (Attribute attribute : this.attributes) {
            attribute.setSelected(isSelected);
        }

    }

    public boolean hasMainAttribute() {
        return false;
    }

}
