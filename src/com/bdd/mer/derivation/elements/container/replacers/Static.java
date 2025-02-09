package com.bdd.mer.derivation.elements.container.replacers;

import com.bdd.mer.derivation.elements.container.Replacer;
import com.bdd.mer.derivation.elements.container.replacers.types.Source;

/**
 * Static Identifier:
 * Employee([MainAttribute]ID, salary) & Boss(Employee) ->
 * -> Employee([MainAttribute]ID, salary) & Boss([MainAttribute]ID)
 * <p></p>
 * Static Common:
 * Employee([MainAttribute]ID, salary) & Boss(Employee) ->
 * -> Employee([MainAttribute]ID, salary) & Boss(salary)
 */

public class Static extends Replacer {

    public Static(Source source) {
        super(source);
    }

    @Override
    public boolean generatesConstraints() {
        return false;
    }
}
