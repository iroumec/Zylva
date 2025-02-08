package com.bdd.mer.derivation.elements;

import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;

import java.util.List;

/**
 * Reference Identifier:
 * Employee([MainAttribute]ID, salary) & Boss(Employee) ->
 * -> Employee([MainAttribute]ID, salary) & Boss([MainAttribute][ForeignKey]ID)
 * <p></p>
 * Reference Common:
 * Employee([MainAttribute]ID, salary) & Boss(Employee) ->
 * -> Employee([MainAttribute]ID, salary) & Boss([ForeignKey]salary)
 */
public class Reference extends Replacer {

    public Reference(String name, Type type) {
        super(name, type);
    }

    @Override
    public List<Constraint> replace(List<Derivation> derivationsToCheck) {

        for (Derivation derivation : derivationsToCheck) {
            //if (derivation)
        }

        return new java.util.ArrayList<>();
    }


}
