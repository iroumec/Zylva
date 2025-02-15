package com.iroumec.eerd;

import com.iroumec.components.Component;
import com.iroumec.components.Diagram;
import com.iroumec.EERDiagram;
import org.jetbrains.annotations.NotNull;

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

    /**
     * @return A boolean indicating if the entity can be deleted or not.
     * If the subclass doesn't implement this method,
     * the default value returned will be {@code TRUE}.
     */
    @Override
    public boolean canBeDeleted() {
        return true;
    }
}
