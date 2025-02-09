package com.bdd.mer.derivation.elements.container.replacers;

import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.container.Replacer;
import com.bdd.mer.derivation.elements.container.replacers.types.Identifier;
import com.bdd.mer.derivation.elements.container.replacers.types.Source;

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

    public Reference() {
        this(new Identifier());
    }

    public Reference(Source source) {
        super(source);
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {

        Element out = super.abstractReplacement(derivation);

        if (out != null) {
            out.addDecoration(ElementDecorator.FOREIGN_ATTRIBUTE);
        }

        return out;
    }

    @Override
    public boolean generatesConstraints() {
        return true;
    }
}
