package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;

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

public class Static extends Replacer{

    public Static(String name, Type type) {
        super(name, type);
    }

    @Override
    public List<Constraint> replace(List<Derivation> derivationsToCheck) {

        return new ArrayList<>();
    }
}
