package com.bdd.mer.derivation.derivationObjects;

public class SingularDerivation extends DerivationObject {

    public SingularDerivation(String name) {
        super(name);
    }

    @Override
    public void generateDerivation() {

       this.addDerivation(super.generateOwnDerivation());
    }

}
