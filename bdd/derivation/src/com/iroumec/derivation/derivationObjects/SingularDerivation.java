package com.iroumec.derivation.derivationObjects;

import com.iroumec.derivation.Derivable;
import com.iroumec.derivation.Derivation;
import com.iroumec.derivation.elements.ElementDecorator;
import com.iroumec.derivation.elements.SingleElement;
import com.iroumec.derivation.elements.containers.Replacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SingularDerivation extends DerivationObject {

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
