package com.iroumec.gui;

import com.iroumec.GUI.components.note.Note;
import com.iroumec.GUI.userPreferences.LanguageManager;
import main.java.com.iroumec.bdd.eerd.EERDiagram;
import main.java.com.iroumec.bdd.eerd.components.entity.EntityWrapper;
import main.java.com.iroumec.bdd.eerd.components.entity.Hierarchy;
import main.java.com.iroumec.bdd.eerd.components.relationship.Relationship;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private final EERDiagram diagram;
    private final MenuBar menuBar;

    public MainFrame() {

        setUndecorated(false);  // Removing of the title bar.
        setTitle("Zylva EERD");
        setSize(800, 600);
        setLocationRelativeTo(null);

        /* ---------------------------------------------------------------------------------------------------------- */

        // Creation of the drawing panel, the bar menu and the menu.
        this.diagram = new EERDiagram();
        this.menuBar = new MenuBar(this, diagram);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        menu.setBackground(Color.LIGHT_GRAY);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                         Add Entity Key                                                     */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addEntityKey = new JButton("Add entity");
        addEntityKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "actionE");
        addEntityKey.getActionMap().put("actionE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityWrapper.addEntity(diagram);
                diagram.cleanSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                      Add Relationship Key                                                  */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addRelationshipKey = new JButton("Add relationship");
        addRelationshipKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "actionR");
        addRelationshipKey.getActionMap().put("actionR", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addRelationship(diagram, diagram.getSelectedComponents());
                diagram.cleanSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                           Add Dependency Key                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addDependencyKey= new JButton("Add dependency");
        addDependencyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "actionD");
        addDependencyKey.getActionMap().put("actionD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Relationship.addDependency(diagram, diagram.getSelectedComponents());
                diagram.cleanSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                       Add Hierarchy Key                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */


        JButton addHierarchyKey= new JButton("Add hierarchy");
        addHierarchyKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "actionH");
        addHierarchyKey.getActionMap().put("actionH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hierarchy.addHierarchy(diagram, diagram.getSelectedComponents());
                diagram.cleanSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                          Add Note Key                                                      */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addNoteKey= new JButton("Add note");
        addNoteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "actionN");
        addNoteKey.getActionMap().put("actionN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Note.addNote(diagram);
                diagram.cleanSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Delete Key                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton deleteKey = new JButton("Delete key");
        deleteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Supr");
        deleteKey.getActionMap().put("Supr", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                int confirmation = JOptionPane.showConfirmDialog(
                        diagram,
                        LanguageManager.getMessage("input.delete"),
                        LanguageManager.getMessage("title.delete"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirmation == JOptionPane.YES_OPTION) {

                    for (Component component : diagram.getSelectedComponents()) {

                        if (!component.canBeDeleted()) {

                            diagram.cleanSelectedComponents();
                            return;
                        }
                    }

                    List<Component> componentsToRemove = new ArrayList<>(diagram.getSelectedComponents());

                    for (Component component : componentsToRemove) {

                        component.setForDelete();
                    }
                }
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                            Clean Diagram                                                   */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton cleanKey = new JButton("Clean key");
        cleanKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), "cleanDiagram");
        cleanKey.getActionMap().put("cleanDiagram", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                diagram.reset();
            }
        });

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

        // Adding the keys to the frame's functionalities.
        getContentPane().add(addEntityKey);
        getContentPane().add(addRelationshipKey);
        getContentPane().add(addDependencyKey);
        getContentPane().add(addHierarchyKey);
        getContentPane().add(addNoteKey);
        getContentPane().add(deleteKey);
        getContentPane().add(cleanKey);
//        getContentPane().add(addAssociationKey);

        add(diagram, BorderLayout.CENTER);
        setJMenuBar(menuBar);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void resetLanguage() {
        this.menuBar.resetLanguage();
        this.diagram.resetLanguage();
    }

}
