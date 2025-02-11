package com.bdd.mer;

import com.bdd.GUI.components.Component;
import com.bdd.mer.components.EERComponent;
import com.bdd.mer.components.entity.EntityWrapper;
import com.bdd.GUI.components.line.Line;
import com.bdd.GUI.components.note.Note;
import com.bdd.mer.components.hierarchy.Hierarchy;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EERDiagram extends Diagram {

    @Override
    public void addComponent(@NotNull Component component) {
        if (!(component instanceof EERComponent) && !(component instanceof Line) && !(component instanceof Note)) {
            throw new IllegalArgumentException(
                    "The component must be a type of EERComponent or an utility component such as Line or Note."
            );
        }
        super.addComponent(component);
    }

//    /* -------------------------------------------------------------------------------------------------------------- */
//    /*                                         Delete Selected Components                                             */
//    /* -------------------------------------------------------------------------------------------------------------- */
//
//    /**
//     * Deletes all the selected components and their close-related components.
//     * <p></p>
//     * At least a component must be selected.
//     */
//    public void deleteSelectedComponents() {
//
//        List<Component> selectedComponents = this.getSelectedComponents();
//
//        if (!selectedComponents.isEmpty()) {
//
//            int confirmation = JOptionPane.showConfirmDialog(
//                    this,
//                    LanguageManager.getMessage("input.delete"),
//                    LanguageManager.getMessage("title.delete"),
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE
//            );
//
//            if (confirmation == JOptionPane.YES_OPTION) {
//
//                Set<Component> componentsForRemoval = new HashSet<>();
//
//                for (Component component : selectedComponents) {
//
//                    if (!component.canBeDeleted()) {
//                        return;
//                    }
//
//                    componentsForRemoval.addAll(component.getComponentsForRemoval());
//                    componentsForRemoval.add(component);
//                }
//
//                for (Component component : componentsForRemoval) {
//                    this.removeComponent(component);
//                }
//
//            }
//
//            this.repaint();
//        } else {
//            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.delete"));
//        }
//
//        // Desactiva el modo de selección
//        this.cleanSelectedComponents();
//    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Get JPopupMenu                                                  */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public JPopupMenu getBackgroundPopupMenu() {

        JPopupMenu backgroundPopupMenu = new JPopupMenu();

        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("option.addEntity"));
        addEntity.addActionListener(_ -> EntityWrapper.createEntity(this));

        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("option.addRelationship"));
        addRelationship.addActionListener(_ -> Relationship.addRelationship(
                this,
                this.getSelectedComponents().toArray(Component[]::new)
        ));

        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("option.addDependency"));
        addDependency.addActionListener(_ -> Relationship.addDependency(
                this,
                this.getSelectedComponents().toArray(Component[]::new)
        ));

        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("option.addHierarchy"));
        addHierarchy.addActionListener(_ -> Hierarchy.addHierarchy(
                this,
                this.getSelectedComponents().toArray(Component[]::new)
        ));

        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("option.addNote"));
        addNote.addActionListener(_ -> Note.addNote(this));

        backgroundPopupMenu.add(addEntity);
        backgroundPopupMenu.add(addRelationship);
        backgroundPopupMenu.add(addDependency);
        backgroundPopupMenu.add(addHierarchy);
        backgroundPopupMenu.add(addNote);

        return backgroundPopupMenu;
    }
}
