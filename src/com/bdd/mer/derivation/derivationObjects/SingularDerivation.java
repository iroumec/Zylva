package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.derivation.Derivable;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.elements.containers.Replacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingularDerivation extends DerivationObject {

    // Useful for the derivation of hierarchies.
    private final Derivable derivable;

    public SingularDerivation(@NotNull String name) {
        this(name, null);
    }

    public SingularDerivation(@NotNull String name, @Nullable Derivable derivable) {
        super(name);
        this.derivable = derivable;
    }

    @Override
    public void generateDerivation() {

       this.addDerivation(super.generateOwnDerivation());

       if (derivable != null) {

           for (Derivation derivation : this.getDerivations()) {
               derivation.addIdentificationElement(
                       new SingleElement(derivable.getIdentifier(),
                               new Replacer(ElementDecorator.FOREIGN))
               );
           }
       }
    }

}
