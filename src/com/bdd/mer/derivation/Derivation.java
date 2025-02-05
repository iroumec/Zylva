package com.bdd.mer.derivation;

import java.util.ArrayList;
import java.util.List;

class Derivation {

    private final String name;
    private final List<String> commonAttributes;
    private final List<String> identificationAttributes;

    Derivation(String name) {
        this.name = name;
        this.commonAttributes = new ArrayList<>();
        this.identificationAttributes = new ArrayList<>();
    }

    void addAttribute(String attribute) {


    }

    void removeAttribute(String attribute) {

    }

    private void addIdentificationAttribute(String attribute) {
        this.identificationAttributes.add(attribute);
    }

    private void addCommonAttribute(String attribute) {
        this.commonAttributes.add(attribute);
    }

    void copyIdentificationAttributes(Derivation derivation) {
        derivation.identificationAttributes.addAll(this.identificationAttributes);
    }

    void copyIdentificationAttributesAsOptional(Derivation derivation) {

        for (String attribute : this.identificationAttributes) {
            derivation.addCommonAttribute("*" + attribute);
        }
    }

    void moveAttributesTo(Derivation derivation) {

        for (String attribute : this.identificationAttributes) {
            derivation.addIdentificationAttribute(attribute);
        }

        for (String attribute : this.commonAttributes) {
            derivation.addCommonAttribute(attribute);
        }

    }

    boolean hasAttributes() {
        return !this.commonAttributes.isEmpty() || !this.identificationAttributes.isEmpty();
    }

    @Override
    public String toString() {
        return this.name +
                "(" + this.commonAttributes.toString() + "," + this.identificationAttributes.toString() + ")";
    }
}
