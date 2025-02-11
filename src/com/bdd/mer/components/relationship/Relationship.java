package com.bdd.mer.components.relationship;

import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.mer.components.AttributableEERComponent;
import com.bdd.GUI.Component;
import com.bdd.mer.components.association.Association;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.entity.EntityWrapper;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.components.line.guard.cardinality.Cardinality;
import com.bdd.mer.components.line.guard.cardinality.StaticCardinality;
import com.bdd.mer.components.line.lineMultiplicity.DoubleLine;
import com.bdd.mer.components.line.lineShape.SquaredLine;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.derivationObjects.PluralDerivation;
import com.bdd.GUI.Diagram;
import com.bdd.structures.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Relationship extends AttributableEERComponent {

    /**
     * Participant of the relationship.
     */
    private final Map<Relatable, List<GuardedLine>> participants;
    private int horizontalDiagonal, verticalDiagonal; // Posición del centro del rombo
    private final Polygon forma;
    private Association association;

    /**
     * Constructs a {@code Relationship}.
     *
     * @param text Name of the relationship.
     * @param x X coordinate of the relationship.
     * @param y Y coordinate of the relationship.
     * @param diagram {@code Diagram} in which the relationship lives.
     */
    public Relationship(String text, int x, int y, Diagram diagram) {

        super(text, x, y, diagram);

        this.participants = new HashMap<>();
        this.forma = new Polygon();
        setDrawingPriority(6);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void addParticipant(Relatable relatableComponent, GuardedLine line) {

        List<GuardedLine> lines = this.participants.get(relatableComponent);

        // The participant doesn't exist.
        if (lines == null) {
            lines = new ArrayList<>();
            relatableComponent.addRelationship(this);
        }

        lines.add(line);

        this.participants.put(relatableComponent, lines);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeParticipant(Relatable relatable) {

        List<GuardedLine> lines = this.participants.get(relatable);

        this.participants.remove(relatable);
        relatable.removeRelationship(this);

        for (Line line : lines) {
            this.getPanelDibujo().removeComponent(line);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public Set<Component> getRelatedComponents() {

        Set<Component> out = new HashSet<>(this.getAttributes());

        for (Map.Entry<Relatable, List<GuardedLine>> participant : this.participants.entrySet()) {

            out.add((Component) participant.getKey());

            if (participant.getKey() instanceof AttributableEERComponent attributableEERComponent) {
                out.addAll(attributableEERComponent.getAttributes());
            }

            out.addAll(participant.getValue());
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * This method let you know the number of participants in the relationship.
     *
     * @return The number of participants in the relationship.
     */
    public int getNumberOfParticipants() {
        return this.participants.size();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void cleanRelatable(Relatable relatable) {

        if (getNumberOfParticipants() > 2) {
            this.removeParticipant(relatable);
        }

        // In another case, we don't have to do anything because, if cleanRelatable was called, it is because
        // the entity will be eliminated and, so, the relationship also if it doesn't enter the if statement's body.

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    private void updateDiagonals(int textWidth, int textHeight, int margin) {
        horizontalDiagonal = textWidth + 2 * margin; // Diagonal horizontal basada en el ancho del texto
        verticalDiagonal = textHeight + 2 * margin; // Diagonal vertical basada en el alto del texto
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void setAssociation(Association association) {
        this.association = association;
        this.resetPopupMenu();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean allMaxCardinalitiesAreN() {

        List<GuardedLine> guardedLines = this.getLines();

        for (GuardedLine guardedLine : guardedLines) {

            // I know all the guarded lines will have a cardinality guard.
            // Maybe it's not a bad idea to use a generic type to make sure...
            Pair<String, String> cardinality = Cardinality.removeFormat(guardedLine.getText());

            String maxCardinality = cardinality.second();

            if (!maxCardinality.matches("[a-zA-Z]")) {
                // If it's a number...
                return false;
            }
        }

        return true;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    private List<GuardedLine> getLines() {

        List<GuardedLine> out = new ArrayList<>();

        for (Map.Entry<Relatable, List<GuardedLine>> participant : this.participants.entrySet()) {
            out.addAll(participant.getValue());
        }

        return out;
    }

    /**
     * Add an association to the diagram.
     */
    // There must be selected at least an entity and a relationship (unary relationship)
    private void addAssociation() {

        if (this.allMaxCardinalitiesAreN()) {

            Association association = new Association(this, this.diagram);

            this.diagram.addComponent(association);
            this.diagram.repaint();
        } else {

            JOptionPane.showMessageDialog(this.diagram, "An association can only be created for N:N or N:N:N relationships.");
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();

        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        int xTexto = getX() - anchoTexto / 2;
        int yTexto = getY() + altoTexto / 4; // It's divided by four to compensate the text baseline.

        g2.setStroke(new BasicStroke(1));

        int margin = 15; // Margin around the text.

        // It is not necessary to do this all the time. Only if the text is changed.
        this.updateDiagonals(anchoTexto, altoTexto, margin);

        forma.reset();
        forma.addPoint(getX(), getY() - verticalDiagonal / 2); // Upper point
        forma.addPoint(getX() + horizontalDiagonal / 2, getY()); // Right point
        forma.addPoint(getX(), getY() + verticalDiagonal / 2); // Lower point
        forma.addPoint(getX() - horizontalDiagonal / 2, getY()); // Left point

        g2.setColor(Color.WHITE);
        g2.fillPolygon(forma);

        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.drawPolygon(forma);
        this.setShape(forma);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem("action.addAttribute");
        actionItem.addActionListener(_ -> this.addAttribute());
        popupMenu.add(actionItem);

        if (association == null) {

            actionItem = new JMenuItem("action.addAssociation");
            actionItem.addActionListener(_ -> this.addAssociation());
            popupMenu.add(actionItem);
        }

        actionItem = new JMenuItem("action.rename");
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem("action.delete");
        actionItem.addActionListener(_ -> this.delete());
        popupMenu.add(actionItem);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanPresence() {

        // We break the bound between the relationship and their participants.
        for (Map.Entry<Relatable, List<GuardedLine>> pair : this.participants.entrySet()) {
            pair.getKey().removeRelationship(this);
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Component> getComponentsForRemoval() {

        List<Component> out = super.getComponentsForRemoval();

        for (Map.Entry<Relatable, List<GuardedLine>> participant : this.participants.entrySet()) {

            List<GuardedLine> lines = participant.getValue();

            for (Line line : lines) {
                out.addAll(line.getComponentsForRemoval());
                out.add(line);
            }
        }

        if (this.association != null) {
            out.addAll(this.association.getComponentsForRemoval());
            out.add(this.association);
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<DerivationObject> getDerivationObjects() {

        List<DerivationObject> out = new ArrayList<>();

        PluralDerivation derivation = new PluralDerivation(this.getIdentifier());

        for (Attribute attribute : this.getAttributes(1)) {
            derivation.addAttribute(this, attribute);
        }

        for (Map.Entry<Relatable, List<GuardedLine>> participant : this.participants.entrySet()) {

            List<GuardedLine> lines = participant.getValue();

            for (GuardedLine line : lines) {

                try {
                    Pair<String, String> cardinalities = Cardinality.removeFormat(line.getText());

                    derivation.addMember(new PluralDerivation.Member(
                            ((Derivable) participant.getKey()).getIdentifier(),
                            cardinalities.first(),
                            cardinalities.second()
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        out.add(derivation);

        return out;
    }

    @Override
    public String getIdentifier() {
        return this.getText();
    }

    /* ---------------------------------------------------------------------------------------------------------- */
    /*                                           Add Relationship                                                 */
    /* ---------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Relationship</Code> to the <Code>this</Code>.
     * <p></p>
     * Between one and three entities (strong or weak) or associations must be selected.
     */
    public void addRelationship() {

        int selectedComponents = this.getSelectedComponents().size();

        if (this.onlyTheseClassesAreSelected(EntityWrapper.class, Association.class)
                && this.isNumberOfSelectedComponentsBetween(1, 3)) {

            String name = JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {
                if (!name.isEmpty()) {

                    Relationship newRelationship = null;

                    List<Line> lines = new ArrayList<>();

                    List<Cardinality> cardinalities = new ArrayList<>();

                    if (selectedComponents >= 2 && selectedComponents <= 3) {

                        Point center = this.getCenterOfSelectedComponents();

                        newRelationship = new Relationship(name, center.x, center.y, this);

                        for (Component component : this.getSelectedComponents()) {

                            // It's safe, due to I asked at the stat if only objects from the Entity and Association classes are selected.
                            Relatable castedComponent = (Relatable) component;

                            Cardinality cardinality;

                            if (selectedComponents == 2) {
                                cardinality = new Cardinality("1", "N", this);
                            } else {
                                cardinality = new Cardinality("0", "N", this);
                            }

                            cardinalities.add(cardinality);

                            GuardedLine guardedLine = new GuardedLine.Builder(
                                    this,
                                    (Component) castedComponent,
                                    newRelationship,
                                    cardinality).build();

                            // This must be improved later.
                            // If an association is related, the line cannot wait until then the association is drawn.
                            // It must be drawn first.
                            if (component instanceof Association) {
                                guardedLine.setDrawingPriority(0);
                            }

                            lines.add(guardedLine);

                            newRelationship.addParticipant(castedComponent, guardedLine);

                        }

                    } else if (selectedComponents == 1) {

                        newRelationship = new Relationship(
                                name,
                                this.getMouseX() + 90,
                                this.getMouseY() - 90,
                                this
                        );

                        Relatable castedComponent = (Relatable) this.getSelectedComponents().getFirst();

                        Cardinality firstCardinality = new Cardinality("1", "N", this);
                        Cardinality secondCardinality = new Cardinality("1", "N", this);

                        GuardedLine firstCardinalityLine = new GuardedLine.Builder(
                                this,
                                (Component) castedComponent,
                                newRelationship,
                                firstCardinality).lineShape(new SquaredLine()).build();
                        lines.add(firstCardinalityLine);

                        GuardedLine secondCardinalityLine = new GuardedLine.Builder(
                                this,
                                newRelationship,
                                (Component) castedComponent,
                                secondCardinality).lineShape(new SquaredLine()).build();
                        lines.add(secondCardinalityLine);

                        cardinalities.add(firstCardinality);
                        cardinalities.add(secondCardinality);

                        newRelationship.addParticipant(castedComponent, firstCardinalityLine);
                        newRelationship.addParticipant(castedComponent, secondCardinalityLine);
                    }

                    // It can never be null, due to it is asked the exact amount of components previously.
                    // But the IDE doesn't know it.
                    assert newRelationship != null;

                    for (Cardinality cardinality : cardinalities) {
                        this.addComponent(cardinality);
                    }

                    for (Line line : lines) {
                        this.addComponent(line);
                    }

                    this.addComponent(newRelationship);

                    this.cleanSelectedComponents();

                }
            } else {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.emptyName"));
            }
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.relationshipCreation"));
        }
    }

    /**
     * Given a cardinality, changes its values.
     *
     * @param cardinality <Code>Cardinality</Code> whose values will be changed.
     */
    public void changeCardinality(Cardinality cardinality) {

        JTextField cardinalidadMinimaCampo = new JTextField(3);
        JTextField cardinalidadMaximaCampo = new JTextField(3);

        JPanel miPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.minimum")));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.maximum")));
        miPanel.add(cardinalidadMaximaCampo);

        setFocus(cardinalidadMinimaCampo);

        int resultado = JOptionPane.showConfirmDialog(null, miPanel, LanguageManager.getMessage("input.twoValues"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String minText = cardinalidadMinimaCampo.getText().trim();
            String maxText = cardinalidadMaximaCampo.getText().trim();

            Optional<Integer> minValue = parseInteger(minText);

            // Validates if the fields are not empty.
            if (minText.isEmpty() || maxText.isEmpty()) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.emptyFields"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the minimum cardinality is a valid number.
            if (minValue.isEmpty() || minValue.get() < 0) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidMinimum"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the maximum cardinality is a valid number or a letter.
            if (!isIntegerOrLetter(maxText) || (isInteger(maxText) && Integer.parseInt(maxText) < 1)) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidMaximum"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If the maximum cardinality is a number, it must be greater than the minimum cardinality.
            if (isInteger(maxText)) {
                int maxValue = Integer.parseInt(maxText);
                if (minValue.get() > maxValue) {
                    JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.invalidRange"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // If everything is valid, the cardinality is updated.
            cardinality.setText(Cardinality.giveFormat(minText, maxText));

            // Here only the area of the cardinality could be repainted, but, if the cardinality now has a considerable
            // greater number, it'll lead to visual noise until all the panel is repainted.
            this.repaint();
        }
    }

    /**
     * It parses a text to <Code>Integer</Code> if it's possible.
     *
     * @param text Text to be parsed.
     * @return {@code Optional<Integer>} containing the parsed text if it was possible.
     */
    private Optional<Integer> parseInteger(String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Validates if a text is an integer xor a letter.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer xor a letter. It returns <Code>FALSE</Code> in any other
     * case.
     */
    private boolean isIntegerOrLetter(String text) {
        return text.matches("\\d+") || text.matches("[a-zA-Z]");
    }

    /**
     * Validates if a text is strictly a number.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer. It returns <Code>FALSE</Code> in any other case.
     */
    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Add Dependency                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Dependency</Code> to the <Code>this</Code>.
     * <p></p>
     * Two strong entities must be selected.
     */
    public void addDependency() {

        if (this.onlyTheseClassesAreSelected(EntityWrapper.class) && this.isNumberOfSelectedComponents(2)) {

            String name = JOptionPane.showInputDialog(
                    this,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {

                Point center = this.getCenterOfSelectedComponents();

                Relationship newRelationship = new Relationship(name, center.x, center.y, this);

                EntityWrapper entitySelected = selectWeakEntity();

                if (entitySelected != null) {

                    entitySelected.setWeakVersion(newRelationship);

                    Cardinality cardinality = null, staticCardinality = null;
                    GuardedLine strongLine = null, weakLine = null;

                    for (EntityWrapper entity : this.getSelectedComponentsByClass(EntityWrapper.class)) {

                        if (entity.equals(entitySelected)) {

                            cardinality = new Cardinality("1", "N", this);

                            strongLine = new GuardedLine.Builder(
                                    this,
                                    entity,
                                    newRelationship,
                                    cardinality
                            ).lineMultiplicity(new DoubleLine(3)).build();

                            newRelationship.addParticipant(entity, strongLine);

                        } else {

                            // A weak entity can only be related to a strong entity if the latter has a 1:1 cardinality.
                            staticCardinality = new StaticCardinality("1", "1", this);

                            weakLine = new GuardedLine.Builder(
                                    this,
                                    entity,
                                    newRelationship,
                                    staticCardinality
                            ).build();

                            newRelationship.addParticipant(entity, weakLine);
                        }
                    }

                    // These checks are only added so the IDE don't tell me they can be null.

                    if (weakLine != null) {
                        this.addComponent(weakLine);
                    }

                    if (strongLine != null) {
                        this.addComponent(strongLine);
                    }

                    if (cardinality != null) {
                        this.addComponent(cardinality);
                    }

                    if (staticCardinality != null) {
                        this.addComponent(staticCardinality);
                    }

                    this.addComponent(newRelationship);

                    this.cleanSelectedComponents();
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.dependencyCreation"));
        }
    }

    /**
     * From the list of selected entities, allows the user to select the weak entity.
     *
     * @return {@code Entity} to be the weak entity of the dependency.
     */
    private EntityWrapper selectWeakEntity() {

        Object[] opciones = {this.getSelectedComponentsByClass(EntityWrapper.class).getFirst().getText(),
                this.getSelectedComponentsByClass(EntityWrapper.class).getLast().getText()};

        // THe JOptionPane with buttons is shown.
        int selection = JOptionPane.showOptionDialog(
                this,
                LanguageManager.getMessage("input.weakEntity"),
                LanguageManager.getMessage("input.option"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return switch (selection) {
            case 0 -> (this.getSelectedComponentsByClass(EntityWrapper.class).getFirst());
            case 1 -> (this.getSelectedComponentsByClass(EntityWrapper.class).getLast());
            default -> {
                JOptionPane.showMessageDialog(this, LanguageManager.getMessage("input.weakEntity"));
                yield selectWeakEntity();
            }
        };
    }
}