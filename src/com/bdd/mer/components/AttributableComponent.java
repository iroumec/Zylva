package com.bdd.mer.components;

import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.frame.DrawingPanel;

import java.util.ArrayList;
import java.util.List;

public abstract class AttributableComponent extends Component implements Derivable {

    /**
     * List of {@code Attribute} of the component.
     */
    private final List<Attribute> attributes;

    /**
     * Constructs an {@code AttributableComponent}.
     *
     * @param text Component's name.
     * @param x X coordinate value in the {@code DrawingPanel}.
     * @param y Y coordinate value in the {@code DrawingPanel}.
     * @param drawingPanel {@code DrawingPanel} where the component lives.
     */
    protected AttributableComponent(String text, int x, int y, DrawingPanel drawingPanel) {
        super(text, x, y, drawingPanel);

        this.attributes = new ArrayList<>();
    }

    /**
     * Adds an {@code Attribute} to the component.
     *
     * @param attribute {@code Attribute} to be added.
     */
    public void addAttribute(Attribute attribute) { this.attributes.add(attribute); }

    /**
     * Removes an {@code Attribute} from the component.
     *
     * @param attribute {@code Attribute} to be removed.
     */
    public void removeAttribute(Attribute attribute) { this.attributes.remove(attribute); }

    /**
     * @param attribute {@code Attribute} whose position is wanted to be known.
     * @return Position of the attribute in the hierarchy.
     *
     * This allows complex trees of attributes to be drawn.
     */
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

    /**
     * The first level is 1.
     *
     * @return {@code List<Attribute>} containing all the attributes of the component in the specified level.
     */
    public List<Attribute> getAttributes(int level) {

        if (level < 0) {
            throw new IllegalArgumentException("The level must be a positive integer. It was " + level + ".");
        }

        if (level == 0) {
            return new ArrayList<>();
        }

        List<Attribute> out = new ArrayList<>();

        for (Attribute attribute : this.attributes) {
            out.add(attribute);
            out.addAll(attribute.getAttributes((level - 1)));
        }

        return out;
    }

    /**
     *
     * @return {@code List<Attribute>} containing all the attributes of the component.
     */
    public List<Attribute> getAttributes() {

        List<Attribute> out = new ArrayList<>(this.attributes);

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getAttributes());
        }

        return out;
    }

    public List<Attribute> getMainAttributes() {

        List<Attribute> out = new ArrayList<>();

        for (Attribute attribute : this.attributes) {
            if (attribute.isMain()) {
                out.add(attribute);
            }
        }

        return out;
    }

    /**
     *
     * @return The number of attributes in the component.
     */
    public int getNumberOfAttributes() {

        int out = this.attributes.size();

        for (Attribute attribute : attributes) {
            out += attribute.getNumberOfAttributes();
        }

        return out;
    }

    /**
     *
     * @return {@code TRUE} if the component has a {@code MainAttribute}. {@code FALSE} in any other case.
     */
    public boolean hasMainAttribute() {
        return false;
    }

    /**
     *
     * @return {@code TRUE} if the component has at least one attribute.
     */
    public boolean hasAttributes() { return !this.attributes.isEmpty(); }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getComponentsForRemoval());
            out.add(attribute);
        }

        return out;
    }

    @Override
    public void setSelected(boolean isSelected) {

        super.setSelected(isSelected);

        for (Attribute attribute : this.attributes) {
            attribute.setSelected(isSelected);
        }

    }

}
