package com.iroumec.bdd.derivation.exporters;

import com.iroumec.bdd.derivation.Constraint;
import com.iroumec.bdd.derivation.Derivation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class DerivationExporter {

    public static void exportToHTML(Collection<Derivation> derivations, Collection<Constraint> constraints){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("derivation.html"));
            writer.write(HTMLFormater.formatToHTML(derivations, constraints));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
