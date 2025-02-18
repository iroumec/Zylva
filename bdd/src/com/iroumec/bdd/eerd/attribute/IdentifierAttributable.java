package com.iroumec.bdd.eerd.attribute;

import com.iroumec.core.Component;
import com.iroumec.bdd.eerd.attribute.cardinalities.Cardinality;
import com.iroumec.bdd.eerd.attribute.cardinalities.Univalued;
import com.iroumec.bdd.eerd.attribute.presences.Obligatory;
import com.iroumec.bdd.eerd.attribute.presences.Optional;
import com.iroumec.bdd.eerd.attribute.presences.Presence;
import com.iroumec.bdd.eerd.attribute.roles.Alternative;
import com.iroumec.bdd.eerd.attribute.roles.Main;
import com.iroumec.bdd.eerd.attribute.roles.Rol;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Identifier Attributable EER Component.
 * <p>
 * It represents a component that can have identifier attributes, such as alternatives and main.
 */
public abstract class IdentifierAttributable extends DescriptiveAttributable {

    private Attribute mainAttribute;

    /**
     * Constructs an {@code DescriptiveAttributable}.
     *
     * @param text Component's name.
     * @param x    X coordinate value in the {@code Diagram}.
     * @param y    Y coordinate value in the {@code Diagram}.
     */
    protected IdentifierAttributable(String text, int x, int y) {
        super(text, x, y);
        this.mainAttribute = null;
    }

    /**
     * @return {@code TRUE} if the component has a main attribute.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasMainAttribute() { return mainAttribute != null; }

    /**
     * Asks for the attribute's presence and creates and attribute with that information.
     *
     * @param rol Rol of the attribute.
     * @param cardinality Cardinality of the attribute.
     */
    @SuppressWarnings("Duplicates")
    protected void addAttribute(Rol rol, Cardinality cardinality) {

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
        JDialog dialog = pane.createDialog(this.diagram, LanguageManager.getMessage("attribute.input"));

        dialog.setVisible(true);

        // TODO: Why an object?
        Object selectedValue = pane.getValue();

        if (selectedValue == null || (int) selectedValue != JOptionPane.OK_OPTION) {
            return;
        }

        Presence presence = (boxOptional.isSelected()) ? Optional.getInstance() : Obligatory.getInstance();

        this.addAttribute(rol, presence, cardinality);
    }

    protected void addAttribute(Rol rol, Presence presence, Cardinality cardinality) {

        String name = IdentifierAttributable.getValidText(this.diagram);

        if (name == null) {
            return;
        }

        this.addAttribute(new Attribute.Builder(rol, name, this)
                .presence(presence)
                .cardinality(cardinality)
                .build()
        );
    }

    protected void addMainAttribute() {

        if (this.mainAttribute != null) {

            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("attribute.warning"));
        }

        String name = IdentifierAttributable.getValidText(this.diagram);

        if (name == null) {
            return;
        }

        this.mainAttribute = new Attribute.Builder(Main.getInstance(), name, this).build();
        this.subscribeTo(mainAttribute, Subscription.REFERENCE);

        this.makeCorrectionInDiagram(mainAttribute);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    @SuppressWarnings("Duplicates")
    public void addAttribute() {

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

        int result = JOptionPane.showOptionDialog(this.diagram, panel, LanguageManager.getMessage("attribute.input.rol"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // In case the user closes the dialog...
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (commonAttributeOption.isSelected()) {
            super.addAttribute();
        } else if (alternativeAttributeOption.isSelected()) {
            addAttribute(Alternative.getInstance(), Univalued.getInstance()); // Alternative attributes cannot be multivalued.
        } else {
            addMainAttribute();
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Rectangle> getAttributeBounds() {

        List<Rectangle> out = super.getAttributeBounds();

        if (this.mainAttribute != null) {
            out.add(this.mainAttribute.getBounds());
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected int getAbsoluteAttributePosition(Attribute attribute) {

        if (this.mainAttribute != null) {

            if (attribute.equals(this.mainAttribute)) {

                return 0;
            } else {

                return super.getAbsoluteAttributePosition(attribute) + mainAttribute.getNumberOfAttributes() + 1;
            }
        }

        return super.getAbsoluteAttributePosition(attribute);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected int getRelativeAttributePosition(Attribute attribute) {

        if (this.mainAttribute != null) {

            if (attribute.equals(this.mainAttribute)) {

                return 0;
            } else {

                return super.getRelativeAttributePosition(attribute) + 1;
            }
        }

        return super.getRelativeAttributePosition(attribute);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected Rectangle getAttributeBounds(int attributePosition) {

        if (this.mainAttribute != null) {

            if (attributePosition == 0) {

                return mainAttribute.getBounds();
            } else {

                return super.getAttributeBounds(attributePosition - 1);
            }

        }

        return super.getAttributeBounds(attributePosition);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Attribute> getAttributes() {

        List<Attribute> out = new ArrayList<>(super.getAttributes());

        if (this.mainAttribute != null) {

            out.addFirst(mainAttribute);
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected int getNumberOfAttributes() {

        int out = super.getNumberOfAttributes();

        if (this.mainAttribute != null) {

            out++;
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected boolean hasAttributes() { return super.hasAttributes() || this.mainAttribute != null; }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void setSelected(boolean isSelected) {

        super.setSelected(isSelected);

        if (mainAttribute != null) {

            mainAttribute.setSelected(isSelected);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanReferencesTo(Component component) {
        if (component instanceof Attribute attribute) {
            if (attribute.equals(this.mainAttribute)) {
                this.mainAttribute = null;
            } else {
                super.cleanReferencesTo(component);
            }
        }
    }
}
