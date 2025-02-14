package com.iroumec.eerd.components.attribute;

import com.iroumec.components.Component;
import com.iroumec.derivation.Derivable;
import com.iroumec.derivation.Derivation;
import com.iroumec.eerd.components.EERComponent;
import com.iroumec.eerd.components.attribute.cardinalities.Cardinality;
import com.iroumec.eerd.components.attribute.cardinalities.Multivalued;
import com.iroumec.eerd.components.attribute.cardinalities.Univalued;
import com.iroumec.eerd.components.attribute.presences.Obligatory;
import com.iroumec.eerd.components.attribute.presences.Optional;
import com.iroumec.eerd.components.attribute.presences.Presence;
import com.iroumec.eerd.components.attribute.roles.Common;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Descriptive Attributable EER Component.
 */
public abstract class DescAttrEERComp extends EERComponent implements Derivable {

    /**
     * List of {@code Attribute} of the component.
     */
    private final List<Attribute> attributes;

    /**
     * Constructs an {@code DescAttrEERComp}.
     *
     * @param text Component's name.
     * @param x X coordinate value in the {@code Diagram}.
     * @param y Y coordinate value in the {@code Diagram}.
     */
    protected DescAttrEERComp(String text, int x, int y) {
        super(text, x, y);

        this.attributes = new ArrayList<>();
    }

    /**
     * @param attribute {@code Attribute} whose position is wanted to be known.
     * @return Position of the attribute in the hierarchy.
     * <p></p>
     * This allows complex trees of attributes to be drawn.
     * It analyzes all the levels of the hierarchy.
     */
    protected int getAbsoluteAttributePosition(Attribute attribute) {

        int out = 0;

        for (Attribute attributeInEntity : this.attributes) {

            if (attributeInEntity.equals(attribute)) {
                return out;
            }

            out += attributeInEntity.getNumberOfAttributes();
            out++;
        }

        return out;
    }

    /**
     * Just considers the position below its owner.
     *
     * @param attribute {@code Attribute} whose relative position is wanted to be known.
     * @return Relative position of the attribute.
     */
    protected int getRelativeAttributePosition(Attribute attribute) {

        int out = 0;

        for (Attribute attributeInEntity : this.attributes) {

            if (attributeInEntity.equals(attribute)) {
                return out;
            }

            out++;
        }

        return out;
    }

    protected Rectangle getAttributeBounds(int attributePosition) {

        if (attributePosition > attributes.size() - 1) {
            throw new IndexOutOfBoundsException();
        }

        return attributes.get(attributePosition).getBounds();
    }

    /**
     * The first level is 1.
     *
     * @return {@code List<Attribute>} containing all the attributes of the component in the specified level.
     */
    protected List<Attribute> getAttributes(int level) {

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

        // TODO: I don't understand why this method cannot be protected.

        List<Attribute> out = new ArrayList<>(this.attributes);

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getAttributes());
        }

        return out;
    }

    /**
     *
     * @return The number of attributes in the component.
     */
    protected int getNumberOfAttributes() {

        int out = this.attributes.size();

        for (Attribute attribute : attributes) {
            out += attribute.getNumberOfAttributes();
        }

        return out;
    }

    /**
     *
     * @return {@code TRUE} if the component has at least one attribute.
     */
    protected boolean hasAttributes() { return !this.attributes.isEmpty(); }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Add Attribute                                                   */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Given an attributable component, this method adds it an attribute according to the specified symbol.
     */
    public void addAttribute() {

        // Create the components of the panel
        JCheckBox boxOptional = new JCheckBox(LanguageManager.getMessage("attribute.optional"));
        JCheckBox boxMultivalued = new JCheckBox(LanguageManager.getMessage("attribute.multivalued"));

        // Array of the components of the panel
        Object[] options = {
                boxOptional, boxMultivalued
        };

        // Show the confirmation dialog
        // noinspection DuplicatedCode
        JOptionPane pane = new JOptionPane(
                options,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Create the dialog directly
        JDialog dialog = pane.createDialog(this.diagram, LanguageManager.getMessage("input.attributeInformation"));

        dialog.setVisible(true);

        // TODO: Why an object?
        Object selectedValue = pane.getValue();

        if (selectedValue == null || (int) selectedValue != JOptionPane.OK_OPTION) {
            return;
        }

        Presence presence = (boxOptional.isSelected()) ? Optional.getInstance() : Obligatory.getInstance();
        Cardinality cardinality = (boxMultivalued.isSelected()) ? Multivalued.getInstance() : Univalued.getInstance();

        this.addAttribute(presence, cardinality);
    }

    /**
     * Asks for the attribute's presence and creates and attribute with that information.
     *
     * @param cardinality Cardinality of the attribute.
     */
    public void addAttribute(Cardinality cardinality) {

        JCheckBox boxOptional = new JCheckBox(LanguageManager.getMessage("attribute.optional"));

        // Array of the components of the panel
        Object[] options = {
                boxOptional
        };

        // Show the confirmation dialog
        // noinspection DuplicatedCode
        JOptionPane pane = new JOptionPane(
                options,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Create the dialog directly
        JDialog dialog = pane.createDialog(this.diagram, LanguageManager.getMessage("input.attributeInformation"));

        dialog.setVisible(true);

        // TODO: Why an object?
        Object selectedValue = pane.getValue();

        if (selectedValue == null || (int) selectedValue != JOptionPane.OK_OPTION) {
            return;
        }

        Presence presence = (boxOptional.isSelected()) ? Optional.getInstance() : Obligatory.getInstance();

        this.addAttribute(presence, cardinality);
    }

    public void addAttribute(Presence presence, Cardinality cardinality) {

        String name = getValidName(this.diagram);

        if (name == null) {
            return;
        }

        this.addAttribute(new Attribute.Builder(Common.getInstance(), name, this)
                .presence(presence)
                .cardinality(cardinality)
                .build()
        );
    }

    /**
     * Adds an attribute in a way that it's correctly drawn.
     * <p>
     * This is important, so the things depending on the attribute (such as associations and other
     * attributes) are drawn correctly in the start and not in the second drawing where the shape is set.
     *
     * @param attribute Attribute to be added.
     */
    final void addAttribute(Attribute attribute) {

        this.attributes.add(attribute);

        makeCorrectionInDiagram(attribute);
    }

    final void makeCorrectionInDiagram(Attribute attribute) {

        Component.addComponent(attribute, diagram);

        // This is necessary due to the repaint will not be done until this method ends, because it's asynchronous.
        // Maybe it would be good to search other possible solutions because this is not so efficient...
        this.diagram.paintImmediately(diagram.getBounds());

        attribute.setDrawingPriority(4);

        this.diagram.sortComponents();

        this.diagram.repaint();
    }

    protected int getLevel() {
        return 0;
    }

    protected abstract void drawStartLineToAttribute(Graphics2D g2, Point textPosition);

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected void setSelected(boolean isSelected) {

        super.setSelected(isSelected);

        for (Attribute attribute : this.attributes) {
            attribute.setSelected(isSelected);
        }

    }

    @Override
    protected void cleanReferencesTo(Component component) {

        if (component instanceof Attribute attribute) {
            this.attributes.remove(attribute);
        }
    }

    @Override
    public List<Derivation> getDerivations() {

        List<Derivation> out = new ArrayList<>();

        for (Attribute attribute : this.attributes) {
            out.addAll(attribute.getDerivations());
        }

        return out;
    }
}
