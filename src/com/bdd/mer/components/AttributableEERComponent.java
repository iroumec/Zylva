package com.bdd.mer.components;

import com.bdd.GUI.Component;
import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.attribute.MainAttribute;
import com.bdd.mer.components.attribute.symbology.AttributeArrow;
import com.bdd.mer.components.attribute.symbology.AttributeEnding;
import com.bdd.mer.components.attribute.symbology.AttributeSymbol;
import com.bdd.mer.derivation.Derivable;
import com.bdd.GUI.Diagram;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.derivationObjects.SingularDerivation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AttributableEERComponent extends EERComponent implements Derivable {

    /**
     * List of {@code Attribute} of the component.
     */
    private final List<Attribute> attributes;

    /**
     * Constructs an {@code AttributableEERComponent}.
     *
     * @param text Component's name.
     * @param x X coordinate value in the {@code Diagram}.
     * @param y Y coordinate value in the {@code Diagram}.
     * @param diagram {@code Diagram} where the component lives.
     */
    protected AttributableEERComponent(String text, int x, int y, Diagram diagram) {
        super(text, x, y, diagram);

        this.attributes = new ArrayList<>();
    }

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

        return out;

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

        List<Attribute> attributes = this.getAttributes();

        for (Attribute attribute : attributes) {
            if (attribute.isMain()) {
                return true;
            }
        }

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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Add Attribute                                                   */
    /* -------------------------------------------------------------------------------------------------------------- */

    public void addRawAttribute() {

        String name = getValidName(this.diagram);

        if (name == null) {
            return;
        }

        Attribute attribute = new Attribute(this, name, AttributeSymbol.COMMON, AttributeArrow.NON_OPTIONAL,
                AttributeEnding.NON_MULTIVALUED, this.diagram);

        this.addAttribute(attribute);
    }

    /**
     * This method adds it a common attribute.
     */
    public void addAttribute() {
        addAttribute(AttributeSymbol.COMMON);
    }

    /**
     * Given an attributable component, this method adds it an attribute according to the specified symbol.
     *
     * @param attributeSymbol The type of the attribute.
     */
    public void addAttribute(AttributeSymbol attributeSymbol) {

        // Create the components of the panel
        JCheckBox boxOptional = new JCheckBox(LanguageManager.getMessage("attribute.optional"));
        JCheckBox boxMultivalued = new JCheckBox(LanguageManager.getMessage("attribute.multivalued"));

        // Array of the components of the panel
        Object[] options = {
                boxOptional, boxMultivalued
        };

        // Show the confirmation dialog
        JOptionPane pane = new JOptionPane(
                options,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        // Create the dialog directly
        JDialog dialog = pane.createDialog(this.diagram, LanguageManager.getMessage("input.attributeInformation"));

        dialog.setVisible(true);

        // Get the selected value after the dialog is closed
        Object selectedValue = pane.getValue();
        if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {

            String name = getValidName(this.diagram);

            if (name == null) {
                return;
            }

            AttributeArrow arrowBody = (boxOptional.isSelected()) ? AttributeArrow.OPTIONAL : AttributeArrow.NON_OPTIONAL;
            AttributeEnding arrowEnding = (boxMultivalued.isSelected()) ? AttributeEnding.MULTIVALUED : AttributeEnding.NON_MULTIVALUED;

            Attribute newAttribute = new Attribute(this, name, attributeSymbol, arrowBody, arrowEnding, this.diagram);

            this.addAttribute(newAttribute);
        }
    }

    /**
     * Given an attributable component, this method allows selecting a type of attribute so it can be added later.
     */
    public void addComplexAttribute() {

        AttributeSymbol attributeSymbol = selectAttributeType();

        if (attributeSymbol == null) {
            return; // The option was canceled.
        }

        if (attributeSymbol.equals(AttributeSymbol.MAIN)) {

            if (this.hasMainAttribute()) {
                JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("warning.mainAttribute"));
                return;
            }

            String name = getValidName(this.diagram);

            if (name == null) {
                return;
            }

            Attribute newAttribute = new MainAttribute(this, name, this.diagram);

            this.addAttribute(newAttribute);

        } else {

            addAttribute(attributeSymbol);
        }
    }

    /**
     * Adds an attribute in a way that it's correctly drawn.
     * <p>
     * This is important, so the things depending on the attribute (such as associations and other
     * attributes) are drawn correctly in the start and not in the second drawing where the shape is set.
     *
     * @param attribute Attribute to be added.
     */
    private void addAttribute(Attribute attribute) {

        attribute.setDrawingPriority(0);

        this.attributes.add(attribute);

        this.diagram.addComponent(attribute);

        // This is necessary due to the repaint will not be done until this method ends, because it's asynchronous.
        // Maybe it would be good to search other possible solutions because this is not so efficient...
        this.diagram.paintImmediately(diagram.getBounds());

        attribute.setDrawingPriority(4);

        this.diagram.sortComponents();

        this.diagram.repaint();
    }

    /**
     * This method allows the user to select an attribute type.
     *
     * @return The attribute symbol selected.
     */
    @SuppressWarnings("Duplicates")
    private AttributeSymbol selectAttributeType() {

        // The radio buttons are created.
        JRadioButton commonAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.common"), true);
        JRadioButton alternativeAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.alternative"));
        JRadioButton mainAttributeOption = new JRadioButton(LanguageManager.getMessage("attribute.main"));

        // The radio buttons are grouped so only one can be selected at the same time.
        ButtonGroup group = new ButtonGroup();
        group.add(mainAttributeOption);
        group.add(alternativeAttributeOption);
        group.add(commonAttributeOption);

        // A panel for containing the radio buttons is created.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // A panel for the group of radio buttons is created.
        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAttribute.add(mainAttributeOption);
        panelAttribute.add(alternativeAttributeOption);
        panelAttribute.add(commonAttributeOption);

        // The pair of options are added to the panel.
        panel.add(panelAttribute);

        int result = JOptionPane.showOptionDialog(null, panel, LanguageManager.getMessage("input.attributeType"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // In case the user closes the dialog...
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        if (commonAttributeOption.isSelected()) {
            return AttributeSymbol.COMMON;
        } else if (alternativeAttributeOption.isSelected()) {
            return AttributeSymbol.ALTERNATIVE;
        } else {
            return AttributeSymbol.MAIN;
        }
    }

    @Override
    protected void cleanReferencesTo(Component component) {

        if (component instanceof Attribute attribute) {
            this.attributes.remove(attribute);
        }
    }

    @Override
    public List<DerivationObject> getDerivationObjects() {

        // noinspection Dup
        List<DerivationObject> out = new ArrayList<>();

        DerivationObject derivation = new SingularDerivation(this.getIdentifier());

        for (Attribute attribute : this.getAttributes(1)) {
            derivation.addAttribute(this, attribute);
        }

        out.add(derivation);

        return out;
    }
}
