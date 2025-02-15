package com.iroumec.eerd.components;

import com.iroumec.components.Component;
import com.iroumec.components.Diagram;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.eerd.EERDiagram;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Pattern;

public abstract class EERComponent extends Component {

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

    // TODO: this must be improved.
    @Override
    protected boolean canBeDeleted() {
        return true;
    }
}
