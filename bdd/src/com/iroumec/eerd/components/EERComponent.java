package com.iroumec.eerd.components;

import com.iroumec.components.Component;
import com.iroumec.components.Diagram;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.eerd.EERDiagram;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Pattern;

public abstract class EERComponent extends Component {

    /**
     * Regex that defines if the name is valid.
     * <p>
     * Save as a constant due to it doesn't change and the compile process is expensive.
     */
    private final static Pattern validNamePattern = Pattern.compile("^[a-zA-Z0-9_-]+$");

    protected EERComponent() {
        super();
    }

    protected EERComponent(int x, int y) {
        super(x, y);
    }

    protected EERComponent(String text, int x, int y) {
        super(text, x, y);
    }

    @SuppressWarnings("unused")
    public EERComponent(@NotNull String text, @NotNull Diagram diagram) {
        super(text, diagram);
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
        boolean nameIsInvalid = false;

        if (name != null) {

            nameIsEmpty = name.trim().isEmpty();
            nameIsInvalid = !validNamePattern.matcher(name).matches();

            if (!nameIsEmpty && !nameIsInvalid) {
                nameIsDuplicated = diagram.existsComponent(name);
            }
        }

        while (name != null && (nameIsEmpty || nameIsDuplicated || nameIsInvalid)) {

            if (nameIsEmpty) {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.emptyName"));
            } else if (nameIsInvalid) {
                JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.invalidName"));
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
                nameIsInvalid = !validNamePattern.matcher(name).matches();

                if (!nameIsEmpty && !nameIsInvalid) {
                    nameIsDuplicated = diagram.existsComponent(name);
                }
            }
        }

        return name;
    }

    // TODO: this must be improved.
    @Override
    protected boolean canBeDeleted() {
        return true;
    }
}
