package com.bdd.mer.derivation.elements.container.replacers;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.elements.container.Replacer;
import com.bdd.mer.derivation.elements.container.replacers.types.Type;

import java.util.ArrayList;
import java.util.List;

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

    public Static(Type type) {
        super(type);
    }

    @Override
    public List<Constraint> getGeneratedConstraints() {
        return new ArrayList<>();
    }
}
