package com.bdd.mer.components.hierarchy;

import com.bdd.GUI.Component;
import com.bdd.GUI.components.line.guard.Discriminant;
import com.bdd.GUI.components.line.lineMultiplicity.DoubleLine;
import com.bdd.GUI.structures.Pair;
import com.bdd.mer.components.EERComponent;
import com.bdd.mer.components.entity.EntityWrapper;
import com.bdd.GUI.components.line.Line;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.derivationObjects.DerivationObject;
import com.bdd.mer.derivation.derivationObjects.SingularDerivation;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
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
public class Hierarchy extends EERComponent implements Derivable {

    /**
     * The symbol of the hierarchy, also denoting its exclusivity.
     */
    private HierarchySymbol symbol;

    /**
     * The parent entity in the hierarchy.
     */
    private final EntityWrapper parent;

    /**
     * The line to the parent.
     */
    private Line parentLine;

    /**
     * List of children entities in the hierarchy.
     */
    private final List<EntityWrapper> children;

    /**
     * Constructs a {@code Hierarchy}.
     *
     * @param symbol {@code Hierarchy}'s symbol, denoting its exclusivity.
     * @param parent {@code Hierarchy}'s parent entity.
     */
    private Hierarchy(HierarchySymbol symbol, EntityWrapper parent) {

        super(parent.getX(), parent.getY() + 60);

        this.symbol = symbol;
        this.parent = parent;
        this.children = new ArrayList<>();

        setDrawingPriority(5);
    }

    /**
     * Adds a child to the hierarchy.
     *
     * @param entity Child to be added.
     */
    public void addChild(EntityWrapper entity) {
        this.children.add(entity);
    }

    /**
     *
     * @return The parent of the hierarchy.
     */
    public EntityWrapper getParent() { return this.parent; }

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
    public int getNumberOfChildren() {
        return this.children.size();
    }

    /**
     * Sets the line to the parent.
     *
     * @param line New {@code Line} drawn to the parent.
     */
    public void setParentLine(Line line) {
        this.parentLine = line;
    }

    /**
     * Swaps the hierarchy's exclusivity.
     */
    private void swapExclusivity() {

        if (this.symbol.equals(HierarchySymbol.DISJUNCT)) {
            this.symbol = HierarchySymbol.OVERLAPPING;
        } else {
            this.symbol = HierarchySymbol.DISJUNCT;
        }

        this.diagram.repaint();

    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                            Add Hierarchy                                                       */
    /* -------------------------------------------------------------------------------------------------------------- */

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
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.threeEntities"));
            return;
        }

        EntityWrapper parent = selectParent(diagram, entities);

        // The action was canceled.
        if (parent == null) {
            return;
        }

        main: if (!parent.isAlreadyParent()) {

            List<EntityWrapper> subtipos = getChildrenList(parent, entities);

            Pair<Hierarchy, List<Component>> newHierarchyData = getHierarchy(diagram, parent);

            if (newHierarchyData == null) {
                return;
            }

            Hierarchy newHierarchy = newHierarchyData.first();
            List<Component> componentsToAdd = newHierarchyData.second();

            try {
                for (EntityWrapper subtipo : subtipos) {
                    newHierarchy.addChild(subtipo);

                    if (!subtipo.addHierarchy(newHierarchy)) {

                        // Repairing action.
                        parent.removeHierarchy(newHierarchy);
                        for (EntityWrapper s : subtipos) {
                            s.removeHierarchy(newHierarchy);

                            String message = LanguageManager.getMessage("warning.theEntity") + " "
                                    + '\"' + subtipo.getText() + '\"'
                                    + " " + LanguageManager.getMessage("warning.alreadyParticipatesInHierarchy") + " "
                                    + LanguageManager.getMessage("warning.multipleInheritanceOnlyAllowed");

                            JOptionPane.showMessageDialog(diagram, message);

                            // Exit.
                            break main;
                        }
                    }
                }

                parent.addHierarchy(newHierarchy);
            } catch (Exception e) {

                JOptionPane.showMessageDialog(diagram, e);
                parent.removeHierarchy(newHierarchy);

                for (EntityWrapper s : subtipos) {
                    s.removeHierarchy(newHierarchy);
                }
            }

            for (Component component : componentsToAdd) {
                Component.addComponent(component, diagram);
            }

            Component.addComponent(newHierarchy, diagram);
        } else {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.alreadyParent"));
        }
    }

    /**
     * Creates a {@code Hierarchy} according to the options selected by the user.
     *
     * @param parent Entity parent of the hierarchy.
     * @return {@code Hierarchy} according to the options selected by the user.
     */
    public static Pair<Hierarchy, List<Component>> getHierarchy(Diagram diagram, EntityWrapper parent) {

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

        int option = JOptionPane.showOptionDialog(null, panel, LanguageManager.getMessage("input.option"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        // If the user clicked "Cancel" or closed the window.
        if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION) {
            return null; // The process is canceled.
        }

        HierarchySymbol symbol = (exclusiveButton.isSelected()) ? HierarchySymbol.DISJUNCT : HierarchySymbol.OVERLAPPING;

        Hierarchy newHierarchy = new Hierarchy(symbol, parent);

        Line parentLine;

        if (totalButton.isSelected()) {

            parentLine = new Line.Builder(diagram, parent, newHierarchy)
                    .lineMultiplicity(new DoubleLine(3)).build();

        } else {

            parentLine = new Line.Builder(diagram, parent, newHierarchy)
                    .strokeWidth(2).build();

            // This way, increasing the stroke, it's noticeable who is the parent of the hierarchy.
        }

        newHierarchy.setParentLine(parentLine);

        List<Component> componentsToAdd = new ArrayList<>();

        componentsToAdd.add(parentLine);

        if (symbol.equals(HierarchySymbol.DISJUNCT)) {

            String discriminantText = JOptionPane.showInputDialog(
                    diagram,
                    null,
                    "Enter a discriminant",
                    JOptionPane.QUESTION_MESSAGE // Message Source.
            );

            // The action was canceled.
            if (discriminantText == null) {
                return null;
            }

            Discriminant discriminant = new Discriminant(discriminantText, parentLine, diagram);

            componentsToAdd.add(discriminant);
        }

        return new Pair<>(newHierarchy, componentsToAdd);
    }

    /**
     * Allows the user to select, from the selected entities, an {@code Entity} to be the parent.
     *
     * @return {@code Hierarchy} selected to be the parent of the {@code Hierarchy}.
     */
    public static @Nullable EntityWrapper selectParent(Diagram diagram, List<EntityWrapper> entities) {

        Object[] opciones = new Object[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            opciones[i] = (entities.get(i)).getText();
        }

        // Muestra el JOptionPane con los botones
        int selection = JOptionPane.showOptionDialog(diagram, LanguageManager.getMessage("hierarchy.selectParent"), "Selección",
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
    public static List<EntityWrapper> getChildrenList(EntityWrapper parent, List<EntityWrapper> entities) {

        List<EntityWrapper> out = new ArrayList<>(entities);
        out.remove((parent));

        return out;
    }

    /**
     *
     * @return {@code TRUE} if the hierarchy is exclusive or disjunct.
     */
    public boolean isExclusive() {
        return this.isDisjunct();
    }

    /**
     *
     * @return {@code TRUE} if the hierarchy is exclusive or disjunct.
     */
    public boolean isDisjunct() {
        return this.symbol == HierarchySymbol.DISJUNCT;
    }

    /**
     *
     * @return The discriminant of the hierarchy.
     */
    public String getDiscriminant() {
        return this.parentLine.getText();
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem("action.setForDelete");
        actionItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem("action.swapExclusivity");
        actionItem.addActionListener(_ -> this.swapExclusivity());
        popupMenu.add(actionItem);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.symbol.toString());
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
        g2.drawString(this.symbol.toString(), this.getX() - 4, this.getY() + 4);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public String getIdentifier() {
        return "";
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<DerivationObject> getDerivationObjects() {

        List<DerivationObject> out = new ArrayList<>();

        if (this.isExclusive()) {
            DerivationObject parentDerivation = new SingularDerivation(this.parent.getIdentifier());
            parentDerivation.addAttribute(this.getDiscriminant());
            out.add(parentDerivation);
        }

        for (EntityWrapper child : this.children) {

            DerivationObject childDerivation = new SingularDerivation(child.getIdentifier(), this.parent);

            out.add(childDerivation);
        }

        return out;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean canBeDeleted() {

        if (isThereMultipleInheritance()) {

            JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.multipleInheritance"));
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
    protected void notifyRemovingOf(Component component) {

        // If a hierarchy has three or more children, if I setForDelete one, it can still exist.
        if (component instanceof EntityWrapper entity) {

            if (this.isParent(entity) || (this.isChild(entity) && this.getNumberOfChildren() <= 2)) {

                this.setForDelete();
            }
        }
    }
}
