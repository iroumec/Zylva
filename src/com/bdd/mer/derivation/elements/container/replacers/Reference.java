package com.bdd.mer.derivation.elements.container.replacers;

import com.bdd.mer.derivation.AttributeDecorator;
import com.bdd.mer.derivation.Constraint;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.Element;
import com.bdd.mer.derivation.elements.container.Replacer;
import com.bdd.mer.derivation.elements.container.replacers.types.Identifier;
import com.bdd.mer.derivation.elements.container.replacers.types.Type;

import java.util.ArrayList;
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

    private List<Constraint> constraints;

    public Reference() {
        this(new Identifier());
    }

    public Reference(Type type) {
        super(type);
        this.constraints = new ArrayList<>();
    }

    @Override
    public Element abstractReplacement(Derivation derivation) {

        Element out = super.abstractReplacement(derivation);

        if (out != null) {
            out.addDecoration(AttributeDecorator.FOREIGN_ATTRIBUTE);
        }

        return out;
    }

    @Override
    public List<Constraint> getGeneratedConstraints() {
        return new ArrayList<>(constraints);
    }
}
