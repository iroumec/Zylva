package com.iroumec.eerd;

import main.java.com.iroumec.GUI.Component;
import main.java.com.iroumec.GUI.components.line.guard.Guard;
import main.java.com.iroumec.bdd.eerd.components.EERComponent;
import main.java.com.iroumec.bdd.eerd.components.entity.EntityWrapper;
import main.java.com.iroumec.GUI.components.line.Line;
import main.java.com.iroumec.GUI.components.note.Note;
import main.java.com.iroumec.bdd.eerd.components.entity.Hierarchy;
import main.java.com.iroumec.bdd.eerd.components.relationship.Relationship;
import main.java.com.iroumec.GUI.Diagram;
import main.java.com.iroumec.GUI.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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
}
