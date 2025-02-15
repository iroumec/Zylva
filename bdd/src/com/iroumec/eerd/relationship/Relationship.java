package com.iroumec.eerd.relationship;

import com.iroumec.components.Component;
import com.iroumec.components.basicComponents.Line;
import com.iroumec.components.basicComponents.line.lineMultiplicity.DoubleLine;
import com.iroumec.components.basicComponents.line.lineShape.SquaredLine;
import com.iroumec.EERDiagram;
import com.iroumec.derivation.Derivable;
import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.ElementDecorator;
import com.iroumec.derivation.elements.SingleElement;
import com.iroumec.derivation.elements.containers.Holder;
import com.iroumec.derivation.elements.containers.Replacer;
import com.iroumec.derivation.elements.containers.sources.Common;
import com.iroumec.eerd.association.Association;
import com.iroumec.eerd.attribute.DescriptiveAttributable;
import com.iroumec.eerd.entity.EntityWrapper;
import com.iroumec.eerd.relationship.cardinalities.Dynamic;
import com.iroumec.eerd.relationship.cardinalities.Static;
import com.iroumec.eerd.relationship.relatable.Relatable;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class Relationship extends DescriptiveAttributable {

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
        setDrawingPriority(6);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    private void addMember(Relatable relatableComponent, Cardinality cardinality) {

        Member member = this.members.get(relatableComponent);

        if (member == null) {
            member = new Member(relatableComponent);
            this.members.put(relatableComponent, member);
            relatableComponent.addRelationship(this);
        }

        member.addCardinality(cardinality);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public Set<Component> getRelatedComponents() {

        Set<Component> out = new HashSet<>(this.getAttributes());

        for (Map.Entry<Relatable, Member> participant : this.members.entrySet()) {

            out.add((Component) participant.getKey());

            // TODO: wrong because of the instance of and because of the access to the attributes.
            // TODO: replace with getMin and getMax point occupied or something similar.
            if (participant.getKey() instanceof DescriptiveAttributable descriptiveAttributable) {
                out.addAll(descriptiveAttributable.getAttributes());
            }
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Relatable> getParticipants() {
        return new ArrayList<>(this.members.keySet());
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
            this.addComponent(association);
            this.diagram.repaint();
        } else {

            JOptionPane.showMessageDialog(this.diagram, LanguageManager.getMessage("association.warning"));
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

        JMenuItem actionItem = new JMenuItem("action.addAttribute");
        actionItem.addActionListener(_ -> this.addAttribute());
        popupMenu.add(actionItem);

        if (association == null) {

            actionItem = new JMenuItem("action.addAssociation");
            actionItem.addActionListener(_ -> this.createAssociation());
            popupMenu.add(actionItem);
        }

        //noinspection DuplicatedCode
        actionItem = new JMenuItem("action.rename");
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem("action.delete");
        actionItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(actionItem);

        return popupMenu;
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

    }

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

        List<Derivation> out = super.getDerivations();

        Derivation mainDerivation = new Derivation(this.getIdentifier());

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

        String name = getValidName(diagram);

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
                        diagram,
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

                // This must be improved later.
                // If an association is related, the line cannot wait until then the association is drawn.
                // It must be drawn first.
                if (relatable instanceof Association) {
                    line.setDrawingPriority(0);
                }

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

        String name = getValidName(diagram);

        if (name == null) {
            return;
        }

        Relationship newRelationship = new Relationship(
                name,
                diagram.getMouseX() + 90,
                diagram.getMouseY() - 90
        );

        Line firstLine = new Line.Builder(
                diagram,
                (Component) relatable,
                newRelationship).lineShape(new SquaredLine()).build();
        newComponents.add(firstLine);

        Line secondLine = new Line.Builder(
                diagram,
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

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Add Dependency                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Adds a new <Code>Dependency</Code> to the <Code>this</Code>.
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

        String name = getValidName(diagram);

        Point center = diagram.getCenterOfComponents(components);

        Relationship newRelationship = new Relationship(name, center.x, center.y);

        EntityWrapper entitySelected = selectWeakEntity(entities, diagram);

        if (entitySelected != null) {

            entitySelected.setWeakVersion(newRelationship);

            List<Component> componentsToAdd = new ArrayList<>();

            for (EntityWrapper entity : entities) {

                if (entity.equals(entitySelected)) {

                    Line strongLine = new Line.Builder(
                            diagram,
                            entity,
                            newRelationship
                    ).lineMultiplicity(new DoubleLine(3)).build();
                    componentsToAdd.add(strongLine);

                    Cardinality cardinality = new Cardinality("1", "N", strongLine, Dynamic.getInstance());
                    componentsToAdd.add(cardinality);

                    newRelationship.addMember(entity, cardinality);
                } else {

                    Line weakLine = new Line.Builder(
                            diagram,
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

    // TODO: return all possible combinations.
    // TODO: why does it return always an empty list?
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
                out.addAll(get1to1BinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
            } else {
                out.addAll(get1toNBinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
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
                out.addAll(get1to1BinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
            } else {
                out.addAll(get1toNUnaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
            }
        } else {
            // There is no much difference between the unary and binary NtoN derivation.
            out.addAll(getNtoNBinaryRelationshipDerivations(mainDerivation, firstMember, secondMember));
        }

        return out;
    }

    private List<Derivation> get1toNUnaryRelationshipDerivations(Derivation mainDerivation,
                                                                 DerivationMember firstMember,
                                                                 DerivationMember secondMember) {

        String minCardinality = (firstMember.maxCardinality.equals("1")) ? firstMember.minCardinality : secondMember.minCardinality;

        Derivation derivation = new Derivation(firstMember.name);

        if (minCardinality.equals("0")) {

            if (!mainDerivation.isEmpty()) {
                derivation.addCommonElement(new SingleElement(this.getIdentifier(),
                        new Replacer(new Common(), ElementDecorator.OPTIONAL)));
            }

            derivation.addCommonElement(
                    new SingleElement(secondMember.name,
                            new Replacer(ElementDecorator.FOREIGN, ElementDecorator.OPTIONAL, ElementDecorator.DUPLICATED))
            );

        } else { // It's equal to 1.

            if (!mainDerivation.isEmpty()) {
                derivation.addCommonElement(new SingleElement(this.getIdentifier(),
                        new Replacer(new Common())));
            }

            derivation.addIdentificationElement(
                    new SingleElement(secondMember.name,
                            new Replacer(ElementDecorator.FOREIGN, ElementDecorator.DUPLICATED))
            );
        }

        return List.of(derivation);
    }

    private List<Derivation> get1toNBinaryRelationshipDerivations(Derivation mainDerivation,
                                                                  DerivationMember firstMember,
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

        // If the main derivation has common attributes, the derivation will take them.
        if (!mainDerivation.isEmpty()) {
            derivation.addCommonElement(
                    new SingleElement(
                            this.getIdentifier(),
                            new Replacer(new Common(), decorators.toArray(new ElementDecorator[0]))
                    )
            );
        }

        decorators.add(ElementDecorator.FOREIGN);

        derivation.addCommonElement(
                new SingleElement(
                        oneSideMember.name,
                        new Replacer(decorators.toArray(new ElementDecorator[0]))
                )
        );

        return List.of(derivation);
    }

    private List<Derivation> get1to1BinaryRelationshipDerivations(Derivation mainDerivation,
                                                                  DerivationMember firstMember,
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

        if (!mainDerivation.isEmpty()) {

            // The derivation member has all the common attributes of the relationship.
            derivation.addCommonElement(
                    new SingleElement(this.getIdentifier(), new Replacer(new Common()))
            );
        }

        out.add(derivation);

        return out;
    }

    private List<Derivation> getNtoNBinaryRelationshipDerivations(Derivation mainDerivation,
                                                                  DerivationMember firstMember,
                                                                  DerivationMember secondMember) {

        // The names of the members will be replaced with their identification attributes.
        mainDerivation.addIdentificationElement(
                new SingleElement(firstMember.name, new Replacer(ElementDecorator.FOREIGN))
        );

        List<ElementDecorator> decoratorsForSecondMember = new ArrayList<>();
        decoratorsForSecondMember.add(ElementDecorator.FOREIGN);

        // N:N unary relationship.
        if (firstMember.name.equals(secondMember.name)) {
            decoratorsForSecondMember.add(ElementDecorator.DUPLICATED);
        }

        mainDerivation.addIdentificationElement(
                new SingleElement(secondMember.name,
                        new Replacer(decoratorsForSecondMember.toArray(new ElementDecorator[0])))
        );

        // TODO: improve this.
        return new ArrayList<>();
    }

    private static class Member {

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

            return this.cardinalities.size() == 1;
        }

        public List<DerivationMember> getDerivationMembers() {

            List<DerivationMember> out = new ArrayList<>();

            for (Cardinality cardinality : this.cardinalities) {
                out.add(new DerivationMember(
                        ((Derivable) relatable).getIdentifier(),
                        cardinality.getMinimumCardinality(),
                        cardinality.getMaximumCardinality())
                );
            }

            return out;
        }
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