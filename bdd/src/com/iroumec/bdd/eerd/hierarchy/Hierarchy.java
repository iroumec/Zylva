package com.iroumec.bdd.eerd.hierarchy;

import com.iroumec.bdd.derivation.elements.ElementDecorator;
import com.iroumec.bdd.derivation.elements.containers.Replacer;
import com.iroumec.core.Component;
import com.iroumec.core.Diagram;
import com.iroumec.components.Line;
import com.iroumec.components.line.lineMultiplicity.DoubleLine;
import com.iroumec.bdd.derivation.Derivable;
import com.iroumec.bdd.derivation.Derivation;
import com.iroumec.bdd.derivation.elements.SingleElement;
import com.iroumec.bdd.eerd.entity.EntityWrapper;
import com.iroumec.bdd.eerd.hierarchy.exclusivity.Discriminant;
import com.iroumec.bdd.eerd.hierarchy.exclusivity.Disjunct;
import com.iroumec.bdd.eerd.hierarchy.exclusivity.Exclusivity;
import com.iroumec.bdd.eerd.hierarchy.exclusivity.Overlap;
import com.iroumec.structures.Pair;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * In a total hierarchy, all instance of the parent must also be an instance of at least one of the children.
 * This is notated with a double line coming from the parent to the hierarchy icon. In the other hand, a partial
 * hierarchy is notated with a single line.
 * <p>
 * In an exclusive hierarchy (AKA disjunct hierarchy), the instances of the children are disjunct sets (this is, each
 * instance can only be an instance of one of the children). This is notated with the letter "d" (disjunct). In the
 * other hand, an overlapping hierarchy is notated with the letter "o" (overlapping).
 */
public final class Hierarchy extends Component implements Derivable {

    /**
     * The line to the parent.
     */
    private Line parentLine;

    /**
     * The parent entity in the hierarchy.
     */
    private final EntityWrapper parent;

    /**
     * Exclusivity of the hierarchy.
     */
    private Exclusivity exclusivity;

    /**
     * List of children entities in the hierarchy.
     */
    private final List<EntityWrapper> children;

    /**
     * Constructs a {@code Hierarchy}.
     *
     * @param exclusivity {@code Hierarchy}'s exclusivity.
     * @param parent {@code Hierarchy}'s parent entity.
     */
    private Hierarchy(Exclusivity exclusivity, EntityWrapper parent) {

        super(parent.getX(), parent.getY() + 60);

        this.exclusivity = exclusivity;
        this.parent = parent;
        this.children = new ArrayList<>();

        this.subscribeTo(parent, Subscription.DELETION);
    }

    /**
     * Adds a child to the hierarchy.
     *
     * @param entity Child to be added.
     */
    private void addChild(EntityWrapper entity) {
        this.children.add(entity);
        this.subscribeTo(entity, Subscription.DELETION);
        this.subscribeTo(parent, Subscription.REFERENCE);
    }

    /**
     *
     * @return A list containing the children of the hierarchy.
     */
    public List<EntityWrapper> getChildren() { return new ArrayList<>(this.children); }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is a children in the hierarchy.
     */
    public boolean isChild(EntityWrapper entity) {
        return this.children.contains(entity);
    }

    public boolean isChild(Hierarchy hierarchy) {
        return this.children.contains(hierarchy.parent);
    }

    /**
     *
     * @param entity Entity to be checked.
     * @return {@code TRUE} if the entity is the parent of the hierarchy.
     */
    public boolean isParent(EntityWrapper entity) {
        return this.parent.equals(entity);
    }

    /**
     *
     * @return {@code TRUE} if the hierarchy is affected by multiple inheritance.
     */
    private boolean isThereMultipleInheritance() {

        for (EntityWrapper firstChild : this.children) {
            for (EntityWrapper secondChild : this.children) {
                if (!firstChild.equals(secondChild) && firstChild.shareHierarchicalChild(secondChild)) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     *
     * @return Number of children in the hierarchy.
     */
    private int getNumberOfChildren() {
        return this.children.size();
    }

    /**
     * Sets the line to the parent.
     *
     * @param line New {@code Line} drawn to the parent.
     */
    private void setParentLine(Line line) {
        this.parentLine = line;
    }

    /**
     * Swaps the hierarchy's exclusivity.
     */
    private void swapExclusivity() {

        if (this.exclusivity.equals(Overlap.getInstance())) {

            Discriminant discriminant = getDiscriminant(this.diagram, this.parentLine);

            if (discriminant == null) {
                return;
            }

            this.exclusivity = Disjunct.getInstance().setDiscriminant(discriminant);
        } else {

            Discriminant discriminant = this.exclusivity.getDiscriminant();

            if (discriminant != null) {
                discriminant.setForDelete();
            }

            this.exclusivity = Overlap.getInstance();
        }

        this.diagram.repaint();
    }

    /**
     * Adds a new <Code>Hierarchy</Code> to the <Code>this</Code>.
     * <p></p>
     * At least three strong or weak entities must be selected.
     */
    public static void addHierarchy(Diagram diagram, List<Component> components) {

        List<EntityWrapper> entities = components.stream()
                .filter(c -> c instanceof EntityWrapper)
                .map(c -> (EntityWrapper) c)
                .toList();

        int numberOfEntities = entities.size();
        int numberOfComponents = components.size();

        // The number of entities and components are different in case not all the components are entities.
        if (numberOfEntities != numberOfComponents || numberOfEntities < 3) {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("hierarchy.creationWarning.input"));
            return;
        }

        EntityWrapper parent = selectParent(diagram, entities);

        // The action was canceled.
        if (parent == null) {
            return;
        }

        if (parent.isAlreadyParent()) {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("hierarchy.creationWarning.alreadyParent"));
            return;
        }

        List<EntityWrapper> children = getChildrenList(parent, entities);

        for (EntityWrapper child : children) {

            if (child.isThereMultipleInheritanceConflict(parent)) {

                String message = LanguageManager.getMessage("hierarchy.creationWarning.theEntity") + " "
                        + '\"' + child.getText() + '\"'
                        + " " + LanguageManager.getMessage("hierarchy.creationWarning.alreadyParticipatesInHierarchy") + " "
                        + LanguageManager.getMessage("hierarchy.creationWarning.multipleInheritance");

                JOptionPane.showMessageDialog(diagram, message);

                return;
            }
        }

        Pair<Hierarchy, List<Component>> newHierarchyData = getHierarchy(diagram, parent);

        if (newHierarchyData == null) {
            return;
        }

        Hierarchy newHierarchy = newHierarchyData.first();
        List<Component> data = newHierarchyData.second();

        for (EntityWrapper child : children) {
            newHierarchy.addChild(child);
            child.addHierarchy(newHierarchy);
        }

        parent.addHierarchy(newHierarchy);

        for (Component component : data) {
            Component.addComponent(component, diagram);
        }

        Component.addComponent(newHierarchy, diagram);
    }

    /**
     * Creates a {@code Hierarchy} according to the options selected by the user.
     *
     * @param parent Entity parent of the hierarchy.
     * @return {@code Hierarchy} according to the options selected by the user.
     */
    private static Pair<Hierarchy, List<Component>> getHierarchy(Diagram diagram, EntityWrapper parent) {

        // The radio buttons are created.
        JRadioButton exclusiveButton = new JRadioButton(LanguageManager.getMessage("hierarchy.exclusive"), true);
        JRadioButton overlapButton = new JRadioButton(LanguageManager.getMessage("hierarchy.overlap"));
        JRadioButton totalButton = new JRadioButton(LanguageManager.getMessage("hierarchy.total"), true);
        JRadioButton partialButton = new JRadioButton(LanguageManager.getMessage("hierarchy.partial"));

        // The radio buttons are grouped so that only one can be selected at the same time.
        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
        groupExclusivaCompartida.add(exclusiveButton);
        groupExclusivaCompartida.add(overlapButton);

        ButtonGroup groupTotalExclusiva = new ButtonGroup();
        groupTotalExclusiva.add(totalButton);
        groupTotalExclusiva.add(partialButton);

        // A panel to contain the radio buttons is created.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // A panel for each group of radio buttons is created.
        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEC.add(exclusiveButton);
        panelEC.add(overlapButton);

        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTP.add(totalButton);
        panelTP.add(partialButton);

        panel.add(panelEC);
        panel.add(panelTP);

        int option = JOptionPane.showOptionDialog(diagram, panel, LanguageManager.getMessage("input.selectAnOption"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // If the user clicked "Cancel" or closed the window.
        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
            return null; // The process is canceled.
        }

        Exclusivity exclusivity = (exclusiveButton.isSelected())
                ? Disjunct.getInstance() : Overlap.getInstance();

        Hierarchy newHierarchy = new Hierarchy(exclusivity, parent);

        List<Component> componentsToAdd = new ArrayList<>();

        Line parentLine;
        if (totalButton.isSelected()) {

            parentLine = new Line.Builder(parent, newHierarchy)
                    .lineMultiplicity(new DoubleLine(3)).build();
        } else {

            parentLine = new Line.Builder(parent, newHierarchy)
                    .strokeWidth(2).build();
            // This way, increasing the stroke, it's noticeable who is the parent of the hierarchy.
        }
        componentsToAdd.add(parentLine);
        newHierarchy.setParentLine(parentLine);

        if (exclusiveButton.isSelected()) {

            Discriminant discriminant = getDiscriminant(diagram, parentLine);

            if (discriminant == null) {
                return null;
            }

            newHierarchy.exclusivity = ((Disjunct) newHierarchy.exclusivity).setDiscriminant(discriminant);

            componentsToAdd.add(discriminant);
        }

        return new Pair<>(newHierarchy, componentsToAdd);
    }

    private static @Nullable Discriminant getDiscriminant(Diagram diagram, Line line) {

        String discriminantText = getValidText(diagram, LanguageManager.getMessage("hierarchy.input.discriminant"));

        // The action was canceled.
        if (discriminantText == null) {
            return null;
        }

        return new Discriminant(discriminantText, line);
    }

    /**
     * Allows the user to select, from the selected entities, an {@code Entity} to be the parent.
     *
     * @return {@code Hierarchy} selected to be the parent of the {@code Hierarchy}.
     */
    private static @Nullable EntityWrapper selectParent(Diagram diagram, List<EntityWrapper> entities) {

        Object[] opciones = new Object[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            opciones[i] = (entities.get(i)).getText();
        }

        // Muestra el JOptionPane con los botones
        int selection = JOptionPane.showOptionDialog(diagram, LanguageManager.getMessage("hierarchy.input.selectParent"), LanguageManager.getMessage("input.selectAnOption"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        try {

            return entities.get(selection);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * Given the parent of the hierarchy, it returns a list containing its children.
     *
     * @param parent {@code Entity} chosen as the parent of the hierarchy.
     * @return {@code List<Entity>} containing the children entities of the hierarchy.
     */
    private static List<EntityWrapper> getChildrenList(EntityWrapper parent, List<EntityWrapper> entities) {

        List<EntityWrapper> out = new ArrayList<>(entities);
        out.remove((parent));

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem(LanguageManager.getMessage("action.delete"));
        actionItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem(LanguageManager.getMessage("action.swapExclusivity"));
        actionItem.addActionListener(_ -> this.swapExclusivity());
        popupMenu.add(actionItem);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public int getDrawingPriority() {
        return this.parent.getDrawingPriority() - 1;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.exclusivity.getSymbol());
        int textHeight = fm.getHeight();

        // The diameter is calculated according to the text size.
        int diameter = Math.max(textWidth, textHeight) + 10;

        // Radio of the oval.
        int radio = diameter / 2;

        // Child lines.
        for (EntityWrapper e : children) {
            g2.drawLine(this.getX(), this.getY(), e.getX(), e.getY());
        }

        Ellipse2D.Double ovalShape = new Ellipse2D.Double(
                this.getX() - radio,
                this.getY() - radio,
                diameter,
                diameter
        );
        this.setShape(ovalShape);

        g2.setColor(Color.WHITE);
        g2.fill(ovalShape);

        g2.setColor(Color.BLACK);

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.draw(ovalShape);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawString(this.exclusivity.getSymbol(), this.getX() - 4, this.getY() + 4);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<Derivation> getDerivations() {

        List<Derivation> out = new ArrayList<>(this.exclusivity.getDerivations(this.parent));

        for (EntityWrapper child : this.children) {

            Derivation childDerivation = new Derivation(child.getIdentifier());
            childDerivation.addIdentificationElement(
                    new SingleElement(this.parent.getIdentifier(),
                    new Replacer(ElementDecorator.FOREIGN))
            );
            out.add(childDerivation);
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeDeleted() {

        if (isThereMultipleInheritance()) {

            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("hierarchy.deleteWarning.multipleInheritance"));
            return false;
        }

        return true;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void cleanReferencesTo(Component component) {

        if (component instanceof EntityWrapper entity) {
            this.children.remove(entity);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void notifyRemovingOf(Component component) {

        // If a hierarchy has three or more children, if I setForDelete one, it can still exist.
        if (component instanceof EntityWrapper entity) {

            if (this.isParent(entity) || (this.isChild(entity) && this.getNumberOfChildren() <= 2)) {

                this.setForDelete();
            }
        }
    }
}
