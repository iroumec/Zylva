package com.bdd.mer.components.relationship;

import com.bdd.mer.components.AttributableComponent;
import com.bdd.mer.components.Component;
import com.bdd.mer.components.association.Association;
import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.line.GuardedLine;
import com.bdd.mer.components.line.Line;
import com.bdd.mer.components.line.guard.cardinality.Cardinality;
import com.bdd.mer.components.relationship.relatable.Relatable;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.derivationObjects.PluralDerivation;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.actions.Action;
import com.bdd.mer.structures.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Relationship extends AttributableComponent {

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
     * @param drawingPanel {@code DrawingPanel} in which the relationship lives.
     */
    public Relationship(String text, int x, int y, DrawingPanel drawingPanel) {

        super(text, x, y, drawingPanel);

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

            if (participant.getKey() instanceof AttributableComponent attributableComponent) {
                out.addAll(attributableComponent.getAttributes());
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

        if (association == null) {
            return this.getActionManager().getPopupMenu(
                    this,
                    Action.ADD_ATTRIBUTE,
                    Action.ADD_ASSOCIATION,
                    Action.RENAME,
                    Action.DELETE
            );
        } else {
            return this.getActionManager().getPopupMenu(
                    this,
                    Action.ADD_ATTRIBUTE,
                    Action.RENAME,
                    Action.DELETE
            );
        }

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
}