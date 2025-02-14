package com.iroumec.eerd;

import com.iroumec.components.Component;
import com.iroumec.components.Diagram;
import com.iroumec.components.basicComponents.Note;
import com.iroumec.eerd.components.EERComponent;
import com.iroumec.eerd.components.entity.EntityWrapper;
import com.iroumec.eerd.components.entity.Hierarchy;
import com.iroumec.eerd.components.relationship.Relationship;
import com.iroumec.userPreferences.LanguageManager;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.security.Guard;

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
