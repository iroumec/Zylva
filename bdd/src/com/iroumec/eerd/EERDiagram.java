package com.iroumec.eerd;

import com.iroumec.components.Component;
import com.iroumec.components.Diagram;
import com.iroumec.components.basicComponents.Note;
import com.iroumec.eerd.components.EERComponent;
import com.iroumec.eerd.components.entity.EntityWrapper;
import com.iroumec.eerd.components.entity.Hierarchy;
import com.iroumec.eerd.components.relationship.Relationship;
import com.iroumec.executables.Button;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.security.Guard;
import java.util.List;

public final class EERDiagram extends Diagram {

    @Override
    protected void addComponent(@NotNull Component component) {
        if (!(component instanceof EERComponent)
                && !(component instanceof Line) && !(component instanceof Guard) && !(component instanceof Note)) {
            throw new IllegalArgumentException(
                    "The component must be a type of EERComponent or an utility component such as Line, Guard or Note."
            );
        }
        super.addComponent(component);
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                Get JPopupMenu                                                  */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public JPopupMenu getBackgroundPopupMenu() {

        JPopupMenu backgroundPopupMenu = new JPopupMenu();

        JMenuItem addEntity = new JMenuItem(LanguageManager.getMessage("option.addEntity"));
        addEntity.addActionListener(_ -> {
            EntityWrapper.addEntity(this);
            cleanSelectedComponents();
        });

        JMenuItem addRelationship = new JMenuItem(LanguageManager.getMessage("option.addRelationship"));
        addRelationship.addActionListener(_ -> {
            Relationship.addRelationship(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addDependency = new JMenuItem(LanguageManager.getMessage("option.addDependency"));
        addDependency.addActionListener(_ -> {
            Relationship.addDependency(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addHierarchy = new JMenuItem(LanguageManager.getMessage("option.addHierarchy"));
        addHierarchy.addActionListener(_ -> {
            Hierarchy.addHierarchy(this, this.getSelectedComponents());
            cleanSelectedComponents();
        });

        JMenuItem addNote = new JMenuItem(LanguageManager.getMessage("option.addNote"));
        addNote.addActionListener(_ -> {
            Note.addNote(this);
            cleanSelectedComponents();
        });

        backgroundPopupMenu.add(addEntity);
        backgroundPopupMenu.add(addRelationship);
        backgroundPopupMenu.add(addDependency);
        backgroundPopupMenu.add(addHierarchy);
        backgroundPopupMenu.add(addNote);

        return backgroundPopupMenu;
    }

    @Override
    public List<Button> getMainFrameKeys() {

        List<Button> out = super.getMainFrameKeys();

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                         Add Entity Key                                                     */
        /* ---------------------------------------------------------------------------------------------------------- */

        Button addEntityKey = new Button();
        addEntityKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "actionE");
        addEntityKey.getActionMap().put("actionE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityWrapper.addEntity(EERDiagram.this);
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addEntityKey);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                      Add Relationship Key                                                  */
        /* ---------------------------------------------------------------------------------------------------------- */

        Button addRelationshipKey = new Button();
        addRelationshipKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "actionR");
        addRelationshipKey.getActionMap().put("actionR", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addRelationship(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addRelationshipKey);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Dependency Key                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        Button addDependencyKey= new Button();
        addDependencyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "actionD");
        addDependencyKey.getActionMap().put("actionD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addDependency(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addDependencyKey);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                       Add Hierarchy Key                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */


        Button addHierarchyKey= new Button();
        addHierarchyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "actionH");
        addHierarchyKey.getActionMap().put("actionH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hierarchy.addHierarchy(EERDiagram.this, EERDiagram.this.getSelectedComponents());
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addHierarchyKey);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                          Add Note Key                                                      */
        /* ---------------------------------------------------------------------------------------------------------- */

        Button addNoteKey= new Button();
        addNoteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "actionN");
        addNoteKey.getActionMap().put("actionN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Note.addNote(EERDiagram.this);
                EERDiagram.this.cleanSelectedComponents();
            }
        });
        out.add(addNoteKey);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                            Add Association Key                                             */
        /* ---------------------------------------------------------------------------------------------------------- */

//        JButton addAssociationKey = new JButton("Add association key");
//        addAssociationKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "ActionA");
//        addAssociationKey.getActionMap().put("ActionA", new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                diagram.addAssociation();
//            }
//        });

        return out;
    }
}
