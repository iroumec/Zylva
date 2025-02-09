package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.derivation.AttributeDecorator;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.*;
import com.bdd.mer.derivation.elements.singleElements.Final;
import com.bdd.mer.derivation.elements.singleElements.replacers.Reference;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Common;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Identifier;
import com.bdd.mer.derivation.elements.singleElements.replacers.types.Type;

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
        this.commonElements.add(new Final(attribute));
    }

    public void addAttribute(Attribute attribute) {

        // There is no distinction if the attribute is multivalued optional or not.
        // If it's multivalued, it can only be common.
        if (attribute.isMultivalued()) {
            Derivation multivaluedAttribute = new Derivation(attribute.getIdentifier());
            multivaluedAttribute.addIdentificationElement(new Final(attribute.getIdentifier()));
            multivaluedAttribute.addIdentificationElement(new Reference(this.getName(), new Identifier()));
            this.addDerivation(multivaluedAttribute);
        } else {

            if (attribute.isMain()) {

                this.identificationElements.add(new Reference(attribute.getIdentifier(), new Identifier()));
            } else {

                Element element;
                Type type = new Common(attribute.isOptional());

                if (attribute.isAlternative()) {

                    element = new Reference(attribute.getIdentifier(), type);
                    element.addDecoration(AttributeDecorator.ALTERNATIVE_ATTRIBUTE);
                } else {

                    element = new Reference(attribute.getIdentifier(), type);
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
