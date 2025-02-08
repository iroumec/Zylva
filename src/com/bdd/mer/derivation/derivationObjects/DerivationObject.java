package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.DerivationFormater;
import com.bdd.mer.derivation.ReferencialIntegrityConstraint;

import java.util.ArrayList;
import java.util.List;

public abstract class DerivationObject {

    private final String name;
    private final List<String> attributes;
    private final List<Derivation> derivations;
    private final List<ReferencialIntegrityConstraint> referencialIntegrityConstraints;

    public DerivationObject(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.derivations = new ArrayList<>();
        this.referencialIntegrityConstraints = new ArrayList<>();
    }

    public void addAttribute(String attribute) {
        this.attributes.add(attribute);
    }

    public void addAttribute(Attribute attribute) {

        this.addAttribute(this.parseAttribute(attribute));
    }

    void addDerivation(Derivation derivation) {
        this.derivations.add(derivation);
    }

    void addReferencialIntegrityConstraint(ReferencialIntegrityConstraint referencialIntegrityConstraint) {
        this.referencialIntegrityConstraints.add(referencialIntegrityConstraint);
    }

    public void generateDerivation() {

        Derivation derivation = new Derivation(this.name);

        for (String attribute : this.attributes) {
            derivation.addAttribute(attribute);
        }

        this.derivations.add(derivation);
    }

    public List<Derivation> getDerivations() {
        return new ArrayList<>(derivations);
    }

    public List<ReferencialIntegrityConstraint> getReferencialIntegrityConstraints() {
        return new ArrayList<>(referencialIntegrityConstraints);
    }

    protected String parseAttribute(Attribute attribute) {

        String parsedAttribute = attribute.getIdentifier();

        if (attribute.isMain()) {
            parsedAttribute = DerivationFormater.MAIN_ATTRIBUTE + parsedAttribute;
        }

        if (attribute.isAlternative()) {
            parsedAttribute = DerivationFormater.ALTERNATIVE_ATTRIBUTE + parsedAttribute;
        }

        if (attribute.isMultivalued()) {
            parsedAttribute = DerivationFormater.MULTIVALUED_ATTRIBUTE + parsedAttribute;
        }

        if (attribute.isOptional()) {
            parsedAttribute = DerivationFormater.OPTIONAL_ATTRIBUTE + parsedAttribute;
        }

        return parsedAttribute;
    }

    protected String getName() { return this.name; }
}
