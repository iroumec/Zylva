package com.bdd.mer.frame;

import com.bdd.mer.actions.Actioner;
import com.bdd.mer.components.Component;
import com.bdd.mer.frame.menuBar.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        setUndecorated(false);  // Removing of the title bar.
        setTitle("Zylva DERExt");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Creation of the drawing panel, the bar menu and the menu.
        DrawingPanel drawingPanel = new DrawingPanel();
        MenuBar menuBar = new MenuBar(this, drawingPanel);
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        Dimension dimension = new Dimension(120, 30);

        menu.setPreferredSize(dimension);
        menu.setBackground(Color.LIGHT_GRAY);

        /* ---------------------------------------------------------------------------------------------------------- */

        // It contains the action both the drawing panel and the frame can do.
        Actioner actioner = new Actioner(drawingPanel);
        drawingPanel.setActioner(actioner);

        // Chequear después
        Component.setPanelDibujo(drawingPanel);

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                         Add Entity Key                                                     */
        /* ---------------------------------------------------------------------------------------------------------- */

        JButton addEntityKey = new JButton("Add entity");
        addEntityKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "actionE");
        addEntityKey.getActionMap().put("actionE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actioner.addEntity();
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
                actioner.addRelationship();
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
                actioner.addDependency();
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
                actioner.addHierarchy();
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
                actioner.addNote();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                              Delete Key                                                    */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Tecla para eliminar (en realidad, botón oculto que se activa al presionar una tecla).
        JButton deleteKey = new JButton("Delete key");
        deleteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Suprimir presionado");
        deleteKey.getActionMap().put("Suprimir presionado", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                actioner.deleteSelectedComponents();
            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */
        /*                                            Add MacroEntity Key                                             */
        /* ---------------------------------------------------------------------------------------------------------- */

        // Tecla para eliminar (en realidad, botón oculto que se activa al presionar una tecla).
        JButton addMacroEntityKey = new JButton("Add macro-entity key");
        deleteKey.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "ActionM");
        deleteKey.getActionMap().put("ActionM", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                actioner.addMacroEntity();
            }
        });

        // Adding the keys to the frame's functionalities.
        getContentPane().add(addEntityKey);
        getContentPane().add(addRelationshipKey);
        getContentPane().add(addDependencyKey);
        getContentPane().add(addHierarchyKey);
        getContentPane().add(addNoteKey);
        getContentPane().add(deleteKey);
        getContentPane().add(addMacroEntityKey);

        // Agrega el panel de dibujo y el menú al marco
        add(drawingPanel, BorderLayout.CENTER);
        //add(menu, BorderLayout.WEST);
        // Añade la barra de menú a la ventana
        setJMenuBar(menuBar);
    }

}
