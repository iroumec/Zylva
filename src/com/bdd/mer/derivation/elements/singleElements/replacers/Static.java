package com.bdd.mer.derivation.elements.singleElements.replacers;

import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.singleElements.Replacer;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Type;

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

    public Static(String name, Type type) {
        super(name, type);
    }

    @Override
    public Element getInstance() {
        return new Static(name, type);
    }
}
