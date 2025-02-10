package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.*;
import com.bdd.mer.derivation.elements.containers.Final;
import com.bdd.mer.derivation.elements.containers.Holder;
import com.bdd.mer.derivation.elements.containers.Replacer;
import com.bdd.mer.derivation.elements.containers.sources.Common;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class DerivationObject {

    private final String name;
    private final List<Derivation> derivations;
    private final List<Element> commonElements;
    private final List<Element> identificationElements;

    public DerivationObject(String name) {
        this.name = name;
        this.commonElements = new ArrayList<>();
        this.derivations = new ArrayList<>();
        this.identificationElements = new ArrayList<>();
    }

    public void addAttribute(String attribute) {
        this.commonElements.add(new SingleElement(attribute));
    }

    public void addAttribute(Derivable owner, Attribute attribute) {

        // There is no distinction if the attribute is multivalued optional or not.
        // If it's multivalued, it can only be common.
        if (attribute.isMultivalued()) {
            Derivation multivaluedAttribute = new Derivation(attribute.getIdentifier());
            multivaluedAttribute.addIdentificationElement(new SingleElement(attribute.getIdentifier()));
            multivaluedAttribute.addIdentificationElement(new SingleElement(owner.getIdentifier(),
                    new Replacer(ElementDecorator.FOREIGN))
            );
            this.addDerivation(multivaluedAttribute);
        } else {

            Holder holder;

            // If it's compound, it'll have a static reference according to its source.
            // If it's not compound, it'll have a Final reference.
            if (attribute.hasAttributes()) {

                if (attribute.isOptional()) {
                    holder = new Replacer(new Common(), ElementDecorator.OPTIONAL);
                } else {
                    holder = new Replacer(new Common());
                }

            } else {
                holder = new Final();
            }

            Element element = new SingleElement(attribute.getIdentifier(), holder);

            if (attribute.isMain()) {

                // In case of a compound identification attribute, I will replace this identification attribute name
                // with the common attributes of the derivation of the compound attribute.
                this.identificationElements.add(element);
            } else {

                if (attribute.isAlternative()) {
                    element.addDecoration(ElementDecorator.ALTERNATIVE);
                }

                if (attribute.isOptional()) {
                    element.addDecoration(ElementDecorator.OPTIONAL);
                }

                this.commonElements.add(element);

            }
        }
    }

    void addDerivation(Derivation derivation) {
        this.derivations.add(derivation);
    }

    public abstract void generateDerivation();

    protected Derivation generateOwnDerivation() {

        Derivation derivation = new Derivation(this.name);

        for (Element element : this.commonElements) {
            derivation.addCommonElement(element);
        }

        for (Element element : this.identificationElements) {
            derivation.addIdentificationElement(element);
        }

        return derivation;
    }

    public List<Derivation> getDerivations() {
        return new ArrayList<>(derivations);
    }

    protected String getName() { return this.name; }
}
