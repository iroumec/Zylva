package com.bdd.mer.components;

import com.bdd.GUI.Component;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;
import com.bdd.mer.EERDiagram;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class EERComponent extends Component {

    public EERComponent(@NotNull Diagram diagram) {
        super(diagram);
        ensureEERDiagram(diagram);
    }

    @SuppressWarnings("unused")
    public EERComponent(@NotNull String text, @NotNull Diagram diagram) {
        super(text, diagram);
        ensureEERDiagram(diagram);
    }

    public EERComponent(int x, int y, @NotNull Diagram diagram) {
        super(x, y, diagram);
        ensureEERDiagram(diagram);
    }

    public EERComponent(@NotNull String text, int x, int y, @NotNull Diagram diagram) {
        super(text, x, y, diagram);
        ensureEERDiagram(diagram);
    }

    private void ensureEERDiagram(@NotNull Diagram diagram) {
        if (!(diagram instanceof EERDiagram)) {
            throw new IllegalArgumentException("Only instances of EERDiagram are supported.");
        }
    }

    /**
     * It makes sure to return a non-empty name.
     *
     * @return {@code String} entered by the user.
     */
    protected static String getValidName(Diagram diagram) {

        String name = JOptionPane.showInputDialog(
                diagram,
                null,
                LanguageManager.getMessage("input.name"),
                JOptionPane.QUESTION_MESSAGE
        );

        boolean nameIsEmpty = false;
        boolean nameIsDuplicated = false;

        if (name != null) {

            nameIsEmpty = name.trim().isEmpty();

            if (!nameIsEmpty) {

                nameIsDuplicated = diagram.existsComponent(name);
            }
        }

        while (name != null && (nameIsEmpty || nameIsDuplicated)) {

            if (nameIsEmpty) {

                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.emptyName"));
            } else {

                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.nameDuplicated"));
            }

            name = JOptionPane.showInputDialog(
                    diagram,
                    null,
                    LanguageManager.getMessage("input.name"),
                    JOptionPane.QUESTION_MESSAGE
            );

            if (name != null) {

                nameIsEmpty = name.trim().isEmpty();

                if (!nameIsEmpty) {

                    nameIsDuplicated = diagram.existsComponent(name);
                }
            }
        }

        return name;
    }

    // TODO: this must be improved.
    @Override
    public boolean canBeDeleted() {
        return true;
    }
}
