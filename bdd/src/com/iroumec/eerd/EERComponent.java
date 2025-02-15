package com.iroumec.eerd;

import com.iroumec.components.Component;

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
}
