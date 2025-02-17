package com.iroumec.bdd.eerd.relationship;

import com.iroumec.core.Component;
import com.iroumec.components.Line;
import com.iroumec.components.line.lineMultiplicity.DoubleLine;
import com.iroumec.components.line.lineShape.SquaredLine;
import com.iroumec.bdd.EERDiagram;
import com.iroumec.bdd.derivation.Derivable;
import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.derivation.elements.containers.Holder;
import com.iroumec.bdd.derivation.elements.containers.Replacer;
import com.iroumec.bdd.derivation.elements.containers.sources.Common;
import com.iroumec.bdd.eerd.attribute.DescriptiveAttributable;
import com.iroumec.bdd.eerd.entity.EntityWrapper;
import com.iroumec.bdd.eerd.relationship.cardinalities.Dynamic;
import com.iroumec.bdd.eerd.relationship.cardinalities.Static;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public final class Relationship extends DescriptiveAttributable implements Derivable {

    /**
     * Members of the relationship. A map is used for efficiency purposes.
     */
    private final Map<Relatable, Member> members;
    private int horizontalDiagonal, verticalDiagonal; // Posición del centro del rombo
    private final Polygon shape;
    private Association association;

    /**
     * Constructs a {@code Relationship}.
     *
     * @param text Name of the relationship.
     * @param x X coordinate of the relationship.
     * @param y Y coordinate of the relationship.
     */
    private Relationship(String text, int x, int y) {
        super(text, x, y);
        this.members = new HashMap<>();
        this.shape = new Polygon();
    }

    private void addMember(Relatable relatableComponent, Cardinality cardinality) {

        Member member = this.members.get(relatableComponent);

        if (member == null) {
            member = new Member(relatableComponent);
            this.members.put(relatableComponent, member);

            // If the relatable component will be deleted, notify me, so I can evaluate if a need to be also
            // deleted.
            this.subscribeTo((Component) relatableComponent, Subscription.DELETION);

            // If the relatable component was deleted, notify me as well so I can clean my references to it.
            this.subscribeTo((Component) relatableComponent, Subscription.REFERENCE);
        }

        member.addCardinality(cardinality);
    }

    /**
     * Updates the diagonals forming the relationship's shape.
     */
    private void updateDiagonals(int textWidth, int textHeight, int margin) {
        horizontalDiagonal = textWidth + 2 * margin; // Diagonal horizontal basada en el ancho del texto
        verticalDiagonal = textHeight + 2 * margin; // Diagonal vertical basada en el alto del texto
    }

    public boolean allDerivedMaxCardinalitiesAreEqualToN() {

        if (members.isEmpty()) {
            return false;
        }

        for (Member member : this.members.values()) {
            if (!member.areAllDerivedMaxCardinalitiesEqualToN()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Surrounds the relationships with an association.
     */
    private void createAssociation() {

        // TODO: is the condition interpreted from the derivation point of view?
        if (this.allDerivedMaxCardinalitiesAreEqualToN()) {

            Association association = new Association(this);

            Component.addComponent(association, diagram);
            this.association = association;
            this.subscribeTo(association, Subscription.REFERENCE);
            this.resetPopupMenu();
            this.diagram.repaint();
        } else {

            JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("association.warning"));
        }
    }

    public List<Rectangle> getAssociationBounds() {

        List<Rectangle> out = super.getAttributeBounds();

        this.members.keySet().forEach(member -> out.addAll(member.getAssociationBounds()));

        out.add(this.getBounds());

        return out;
    }

    /**
     * Adds a new <Code>Relationship</Code> to the <Code>this</Code>.
     * <p></p>
     * Between one and three entities (strong or weak) or associations must be selected.
     */
    public static void addRelationship(EERDiagram diagram, List<Component> components) {

        List<Relatable> relatableComponents = components.stream()
                .filter(c -> c instanceof EntityWrapper || c instanceof Association)
                .map(c -> (Relatable) c)
                .toList();

        int numberOfComponents = relatableComponents.size();

        // Not all the components are relatable.
        if (numberOfComponents != components.size() || numberOfComponents < 1 || numberOfComponents > 3) {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("relationship.warning"));
            return;
        }

        String name = Relationship.getValidText(diagram);

        if (name == null) {
            return;
        }

        Relationship newRelationship;

        List<Component> newComponents = new ArrayList<>();

        if (numberOfComponents >= 2) { // Number of components in equal to two or three.

            Point center = diagram.getCenterOfComponents(components);

            newRelationship = new Relationship(name, center.x, center.y);

            for (Relatable relatable : relatableComponents) {

                Line line = new Line.Builder(
                        (Component) relatable,
                        newRelationship).build();
                newComponents.add(line);

                Cardinality cardinality;

                if (numberOfComponents == 2) {
                    cardinality = new Cardinality("1", "N", line, Dynamic.getInstance());
                } else {
                    cardinality = new Cardinality("0", "N", line, Dynamic.getInstance());
                }
                newComponents.add(cardinality);

                newRelationship.addMember(relatable, cardinality);
            }

            newComponents.add(newRelationship);

        } else { // Number of components is equals to one.

            addReflexiveRelationship(diagram, relatableComponents.getFirst());
        }

        for (Component newComponent : newComponents) {
            Component.addComponent(newComponent, diagram);
        }
    }

    public static void addReflexiveRelationship(EERDiagram diagram, Relatable relatable) {

        List<Component> newComponents = new ArrayList<>();

        String name = Relationship.getValidText(diagram);

        if (name == null) {
            return;
        }

        Relationship newRelationship = new Relationship(
                name,
                diagram.getMouseX() + 90,
                diagram.getMouseY() - 90
        );

        Line firstLine = new Line.Builder(
                (Component) relatable,
                newRelationship).lineShape(new SquaredLine()).build();
        newComponents.add(firstLine);

        Line secondLine = new Line.Builder(
                newRelationship,
                (Component) relatable).lineShape(new SquaredLine()).build();
        newComponents.add(secondLine);

        Cardinality firstCardinality = new Cardinality("1", "N", firstLine, Dynamic.getInstance());
        Cardinality secondCardinality = new Cardinality("1", "N", secondLine, Dynamic.getInstance());

        newComponents.add(firstCardinality);
        newComponents.add(secondCardinality);

        newRelationship.addMember(relatable, firstCardinality);
        newRelationship.addMember(relatable, secondCardinality);

        newComponents.add(newRelationship);

        for (Component newComponent : newComponents) {
            Component.addComponent(newComponent, diagram);
        }
    }

    /**
     * Adds a new <Code>Dependency</Code> to the <Code>Diagram</Code>.
     * <p></p>
     * Two strong entities must be selected.
     */
    public static void addDependency(EERDiagram diagram, List<Component> components) {

        List<EntityWrapper> entities = components.stream()
                .filter(c -> c instanceof EntityWrapper)
                .map(c -> (EntityWrapper) c)
                .toList();

        // entities.size() != components.length when all at least one of the components passed is not an instance
        // of entity wrapper.
        if (entities.size() != components.size() || entities.size() != 2) {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("dependency.warning"));
            return;
        }

        String name = Relationship.getValidText(diagram);

        Point center = diagram.getCenterOfComponents(components);

        Relationship newRelationship = new Relationship(name, center.x, center.y);

        EntityWrapper entitySelected = selectWeakEntity(entities, diagram);

        if (entitySelected != null) {

            entitySelected.setWeakVersion(newRelationship);

            List<Component> componentsToAdd = new ArrayList<>();

            for (EntityWrapper entity : entities) {

                if (entity.equals(entitySelected)) {

                    Line strongLine = new Line.Builder(
                            entity,
                            newRelationship
                    ).lineMultiplicity(new DoubleLine(3)).build();
                    componentsToAdd.add(strongLine);

                    Cardinality cardinality = new Cardinality("1", "N", strongLine, Dynamic.getInstance());
                    componentsToAdd.add(cardinality);

                    newRelationship.addMember(entity, cardinality);
                } else {

                    Line weakLine = new Line.Builder(
                            entity,
                            newRelationship
                    ).build();
                    componentsToAdd.add(weakLine);

                    // A weak entity can only be related to a strong entity if the latter has a 1:1 cardinality.
                    Cardinality staticCardinality = new Cardinality("1", "1", weakLine, Static.getInstance());
                    componentsToAdd.add(staticCardinality);

                    newRelationship.addMember(entity, staticCardinality);
                }
            }

            componentsToAdd.add(newRelationship);

            for (Component newComponent : componentsToAdd) {
                Component.addComponent(newComponent, diagram);
            }
        }
    }

    /**
     * From the list of selected entities, allows the user to select the weak entity.
     *
     * @return {@code Entity} to be the weak entity of the dependency.
     */
    private static EntityWrapper selectWeakEntity(List<EntityWrapper> entities, EERDiagram diagram) {

        Object[] opciones = {entities.getFirst().getText(),
                entities.getLast().getText()};

        // THe JOptionPane with buttons is shown.
        int selection = JOptionPane.showOptionDialog(
                diagram,
                LanguageManager.getMessage("weakEntity.input"),
                LanguageManager.getMessage("input.selectAnOption"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        return switch (selection) {
            case 0 -> (entities.getFirst());
            case 1 -> (entities.getLast());
            default -> {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("weakEntity.input"));
                yield selectWeakEntity(entities, diagram);
            }
        };
    }

    private static class Member implements Serializable {

        private final Relatable relatable;
        private final List<Cardinality> cardinalities;

        public Member(@NotNull Relatable relatable) {
            this.relatable = relatable;
            this.cardinalities = new ArrayList<>();
        }

        public void addCardinality(@NotNull Cardinality cardinality) {
            this.cardinalities.add(cardinality);
        }

        public List<Line> getLines() {

            List<Line> out = new ArrayList<>();

            for (Cardinality cardinality : this.cardinalities) {
                out.add(cardinality.getLine());
            }

            return out;
        }

        public boolean areAllDerivedMaxCardinalitiesEqualToN() {

            for (Cardinality cardinality : this.cardinalities) {

                String maxCardinality = cardinality.getMaximumCardinality();

                // Letters or numbers greater than one.
                if (!maxCardinality.matches("[a-zA-Z]|[2-9]\\d*")) {
                    return false;
                }
            }

            return true;
        }

        public List<DerivationMember> getDerivationMembers() {

            List<DerivationMember> out = new ArrayList<>();

            for (Cardinality cardinality : this.cardinalities) {
                out.add(new DerivationMember(
                        relatable.getIdentifier(),
                        cardinality.getMinimumCardinality(),
                        cardinality.getMaximumCardinality())
                );
            }

            return out;
        }
    }

    public Relatable getOppositeMember(Relatable relatable) {

        Optional<Relatable> result = this.members.keySet().stream()
                .filter(member -> !member.equals(relatable))
                .findFirst(); // It returns the first member that is different from relatable.

        return result.orElse(null);
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

        shape.reset();
        shape.addPoint(getX(), getY() - verticalDiagonal / 2); // Upper point
        shape.addPoint(getX() + horizontalDiagonal / 2, getY()); // Right point
        shape.addPoint(getX(), getY() + verticalDiagonal / 2); // Lower point
        shape.addPoint(getX() - horizontalDiagonal / 2, getY()); // Left point

        g2.setColor(Color.WHITE);
        g2.fillPolygon(shape);

        g2.setColor(Color.BLACK);
        g2.drawString(this.getText(), xTexto, yTexto);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.drawPolygon(shape);
        this.setShape(shape);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem(LanguageManager.getMessage("action.addAttribute"));
        actionItem.addActionListener(_ -> this.addAttribute());
        popupMenu.add(actionItem);

        if (association == null) {

            actionItem = new JMenuItem(LanguageManager.getMessage("action.addAssociation"));
            actionItem.addActionListener(_ -> this.createAssociation());
            popupMenu.add(actionItem);
        }

        //noinspection DuplicatedCode
        actionItem = new JMenuItem(LanguageManager.getMessage("action.rename"));
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem(LanguageManager.getMessage("action.delete"));
        actionItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(actionItem);

        return popupMenu;
    }

    /* ---------------------------------------------------------------------------------------------------------- */

    @Override
    public int getDrawingPriority() {

        int drawingPriority = 0;

        for (Relatable relatable : this.members.keySet()) {
            drawingPriority = Math.min(((Component) relatable).getDrawingPriority(), drawingPriority);
        }

        return drawingPriority - 1;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanReferencesTo(Component component) {

        super.cleanReferencesTo(component);

        if (component instanceof Relatable relatable) {

            // If component is a participant, it is assumed that its lines were removed in the
            // notifyRemovingOf() method.
            this.members.remove(relatable);
        }

        if (component instanceof Association) {
            this.association = null;
            this.resetPopupMenu();
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */

    @Override
    public void notifyRemovingOf(Component component) {

        if (component instanceof Relatable relatable && this.members.containsKey(relatable)) {

            if (this.members.size() <= 2) {

                this.setForDelete();
            } else {

                Member member = this.members.get(relatable);

                for (Line line : member.getLines()) {
                    line.setForDelete();
                }
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------------- */

    @Override
    protected void drawStartLineToAttribute(Graphics2D g2, Point textPosition) {

        Rectangle bounds = this.getBounds();

        // Vertical line that comes from inside the relationship (in entities is not visible).
        int x = ((int) bounds.getCenterX() + (int) bounds.getMaxX()) / 2;
        int y = (int) bounds.getCenterY();
        g2.drawLine(x, y, x, textPosition.y);

        // Horizontal line that comes from inside the attributable component.
        g2.drawLine(x, textPosition.y, textPosition.x, textPosition.y);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Derivation> getDerivations() {

        List<Derivation> out = new ArrayList<>();

        Derivation mainDerivation = new Derivation(this.getIdentifier());

        out.add(mainDerivation);

        List<DerivationMember> derivationMembers = new ArrayList<>();

        for (Member member : this.members.values()) {
            derivationMembers.addAll(member.getDerivationMembers());
        }

        switch (derivationMembers.size()) {
            case 2: out.addAll(getBinaryRelationshipDerivation(mainDerivation, derivationMembers)); break;
            case 3: out.addAll(getTernaryRelationshipDerivation(mainDerivation, derivationMembers)); break;
            default: throw new RuntimeException("Invalid number of members for a plural derivation.");
        }

        return out;
    }

    /* ---------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() {
        return this.getText();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                          Derivation Related                                                    */
    /* -------------------------------------------------------------------------------------------------------------- */

    // TODO: return all possible combinations.
    private List<Derivation> getTernaryRelationshipDerivation(Derivation mainDerivation,
                                                              List<DerivationMember> members) {

        int oneCount = 0;

        DerivationMember oneCardinalityMember = null;

        for (DerivationMember member : members) {

            if (member.maxCardinality.equals("1")) {

                oneCount++;

                if (oneCardinalityMember == null) {
                    oneCardinalityMember = member;
                }
            }
        }

        Holder mostUsedHolder = new Replacer(ElementDecorator.FOREIGN);

        switch (oneCount) {
            case 0: // N:N:N

                mainDerivation.addIdentificationElement(
                        new SingleElement(members.getFirst().name, mostUsedHolder)
                );
                mainDerivation.addIdentificationElement(
                        new SingleElement(members.get(1).name, mostUsedHolder)
                );
                mainDerivation.addIdentificationElement(
                        new SingleElement(members.getLast().name, mostUsedHolder)
                );

                break;
            case 1, 2, 3: // 1:N:N

                for (DerivationMember member : members) {

                    if (!member.equals(oneCardinalityMember)) {

                        mainDerivation.addIdentificationElement(
                                new SingleElement(member.name, mostUsedHolder)
                        );
                    } else {

                        mainDerivation.addCommonElement(
                                new SingleElement(member.name, new Replacer(ElementDecorator.FOREIGN))
                        );
                    }
                }
                break;
            // 1:1:N -- The difference here is that there are more possible combinations. But the logic is the same as case 1 if we only want one combination.
            //case 2: break;
            // 1:1:1 -- Again, more possible, combinations.
            //case 3: break;
            default: throw new RuntimeException("Invalid cardinality for ternary relationship.");
        }

        return new ArrayList<>();
    }

    private List<Derivation> getBinaryRelationshipDerivation(Derivation mainDerivation,
                                                             List<DerivationMember> members) {

        List<Derivation> out = new ArrayList<>();

        DerivationMember firstMember = members.getFirst();
        DerivationMember secondMember = members.getLast();

        if (firstMember.name.equals(secondMember.name)) {
            out.addAll(getUnaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
            return out;
        }

        if (firstMember.maxCardinality.equals("1") || secondMember.maxCardinality.equals("1")) {

            if (firstMember.maxCardinality.equals("1") && secondMember.maxCardinality.equals("1")) {
                out.addAll(get1to1BinaryRelationshipDerivations(firstMember, secondMember));
            } else {
                out.addAll(get1toNBinaryRelationshipDerivations(firstMember, secondMember));
            }
        } else {
            out.addAll(getNtoNBinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
        }

        return out;
    }

    private List<Derivation> getUnaryRelationshipDerivations(Derivation mainDerivation,
                                                             DerivationMember firstMember,
                                                             DerivationMember secondMember) {

        List<Derivation> out = new ArrayList<>();

        if (firstMember.maxCardinality.equals("1") || secondMember.maxCardinality.equals("1")) {

            if (firstMember.maxCardinality.equals("1") && secondMember.maxCardinality.equals("1")) {
                // TODO: I couldn't find any rules for this kind of derivation. Is it possible?
                out.addAll(get1to1BinaryRelationshipDerivations(firstMember, secondMember));
            } else {
                out.addAll(get1toNUnaryRelationshipDerivations(firstMember, secondMember));
            }
        } else {
            // There is no much difference between the unary and binary NtoN derivation.
            out.addAll(getNtoNBinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
        }

        return out;
    }

    private List<Derivation> get1toNUnaryRelationshipDerivations(DerivationMember firstMember,
                                                                 DerivationMember secondMember) {

        String minCardinality = (firstMember.maxCardinality.equals("1")) ? firstMember.minCardinality : secondMember.minCardinality;

        Derivation derivation = new Derivation(firstMember.name);

        if (minCardinality.equals("0")) {

            derivation.addCommonElement(new SingleElement(this.getIdentifier(),
                    new Replacer(new Common(), ElementDecorator.OPTIONAL)));

            derivation.addCommonElement(new SingleElement(secondMember.name,
                    new Replacer(ElementDecorator.DUPLICATED, ElementDecorator.OPTIONAL, ElementDecorator.FOREIGN)));

        } else { // It's equal to 1.

            derivation.addCommonElement(new SingleElement(this.getIdentifier(), new Replacer(new Common())));

            derivation.addIdentificationElement(new SingleElement(secondMember.name,
                    new Replacer(ElementDecorator.DUPLICATED, ElementDecorator.FOREIGN)));
        }

        return List.of(derivation);
    }

    private List<Derivation> get1toNBinaryRelationshipDerivations(DerivationMember firstMember,
                                                                  DerivationMember secondMember) {

        DerivationMember oneSideMember = (firstMember.maxCardinality.equals("1")) ? firstMember : secondMember;
        DerivationMember nSideMember = (firstMember.maxCardinality.equals("1")) ? secondMember : firstMember;

        Derivation derivation = new Derivation(nSideMember.name);

        List<ElementDecorator> decorators = new ArrayList<>();

        // If the one side member has a cardinality of zero,
        // the relationship's attributes are also optional.
        if (oneSideMember.minCardinality.equals("0")) {
            decorators.add(ElementDecorator.OPTIONAL);
        }

        // The derivation takes the attributes of the relationship.
        derivation.addCommonElement(new SingleElement(this.getIdentifier(),
                new Replacer(new Common(), decorators.toArray(new ElementDecorator[0]))));

        decorators.add(ElementDecorator.FOREIGN);

        derivation.addCommonElement(new SingleElement(oneSideMember.name,
                new Replacer(decorators.toArray(new ElementDecorator[0]))));

        return List.of(derivation);
    }

    private List<Derivation> get1to1BinaryRelationshipDerivations(DerivationMember firstMember,
                                                                  DerivationMember secondMember) {

        List<Derivation> out = new ArrayList<>();

        // The member who takes the identification attributes of the other member.
        DerivationMember mainMember, secondaryMember;
        Derivation derivation;
        List<ElementDecorator> decorators = new ArrayList<>();
        decorators.add(ElementDecorator.FOREIGN);
        decorators.add(ElementDecorator.ALTERNATIVE);

        // Case (0,1):(1,1).
        if (!firstMember.minCardinality.equals(secondMember.minCardinality)) {

            mainMember = (firstMember.minCardinality.equals("0")) ? firstMember : secondMember;

        } else { // Cases (0,1):(0,1) and (1,1):(1,1). Here the member chosen doesn't matter. There are two valid derivations.

            if (firstMember.minCardinality.equals("0")) { // Case (0,1):(0,1).
                decorators.add(ElementDecorator.OPTIONAL);
                decorators.remove(ElementDecorator.ALTERNATIVE);
            }

            Random selector = new Random();

            mainMember = selector.nextBoolean() ? firstMember : secondMember;
        }

        secondaryMember = (mainMember.equals(firstMember)) ? secondMember : firstMember;

        derivation = new Derivation(mainMember.name);

        derivation.addCommonElement(
                new SingleElement(secondaryMember.name,
                        new Replacer(decorators.toArray(new ElementDecorator[0])))
        );

        // The derivation member has all the common attributes of the relationship (if it has).
        derivation.addCommonElement(new SingleElement(this.getIdentifier(), new Replacer(new Common())));

        out.add(derivation);

        return out;
    }

    private List<Derivation> getNtoNBinaryRelationshipDerivations(Derivation mainDerivation,
                                                                  DerivationMember firstMember,
                                                                  DerivationMember secondMember) {

        // The names of the members will be replaced with their identification attributes.
        mainDerivation.addIdentificationElement(new SingleElement(firstMember.name,
                new Replacer(ElementDecorator.FOREIGN)));

        List<ElementDecorator> decoratorsForSecondMember = new ArrayList<>();
        decoratorsForSecondMember.add(ElementDecorator.FOREIGN);

        // N:N unary relationship.
        if (firstMember.name.equals(secondMember.name)) { decoratorsForSecondMember.add(ElementDecorator.DUPLICATED); }

        mainDerivation.addIdentificationElement(new SingleElement(secondMember.name,
                new Replacer(decoratorsForSecondMember.toArray(new ElementDecorator[0]))));

        return new ArrayList<>();
    }

    private record DerivationMember(String name, String minCardinality, String maxCardinality) {

        private DerivationMember(@NotNull String name, @NotNull String minCardinality, @NotNull String maxCardinality) {
            this.name = name;
            this.minCardinality = adaptMinCardinality(minCardinality);
            this.maxCardinality = adaptMaxCardinality(maxCardinality);
        }

        /**
         * Minimum cardinalities, in the derivation format, can only take two values: 0 or 1.
         * If the minimum cardinality has a value greater than one, it'll be replaced by 1 and
         * the user will have to specify a business rule for that minimum.
         *
         * @param minCardinality Minimum cardinality.
         * @return The minimum cardinality adapted to the derivation rules.
         */
        private String adaptMinCardinality(@NotNull String minCardinality) {

            if (minCardinality.isEmpty()) {
                throw new IllegalArgumentException("Min cardinality cannot be empty.");
            }

            try {
                int minCardinalityParse = Integer.parseInt(minCardinality);

                if (minCardinalityParse < 0) {
                    throw new IllegalArgumentException("Min cardinality cannot be negative.");
                }

                if (minCardinalityParse > 1) {
                    return "1";
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Min cardinality must be a number.");
            }

            return minCardinality;
        }

        /**
         * Minimum cardinalities, in the derivation format, can only take two values: 1 or N (another letter can be used).
         * If the maximum cardinality has a value greater than one and different from N, it'll be replaced by N and
         * the user will have to specify a business rule for that maximum.
         *
         * @param maxCardinality Maximum cardinality.
         * @return The maximum cardinality adapted to the derivation rules.
         */
        private String adaptMaxCardinality(@NotNull String maxCardinality) {

            if (maxCardinality.isEmpty()) {
                throw new IllegalArgumentException("Min cardinality cannot be empty.");
            }

            try { // It's a number.
                int maxCardinalityParse = Integer.parseInt(maxCardinality);

                if (maxCardinalityParse < 1) {
                    throw new IllegalArgumentException("Max cardinality cannot be less than 1.");
                }

                if (maxCardinalityParse > 1) {
                    return "N";
                }
            } catch (NumberFormatException e) { // It's okay if it's a number.
                return maxCardinality;
            }

            return maxCardinality;
        }
    }
}