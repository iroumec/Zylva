package com.bdd.mer.derivation.elements.singleElements.replacers;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.singleElements.Replacer;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Type;

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
    public List<Element> getElementsToReplace(Derivation derivation) {
        return List.of();
    }


    @Override
    public List<Replacer> getReplacementsNeeded() {
        return List.of();
    }

    @Override
    public Element getInstance() {
        return null;
    }
}
