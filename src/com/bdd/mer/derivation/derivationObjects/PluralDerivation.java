package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.elements.ElementDecorator;
import com.bdd.mer.derivation.elements.SingleElement;
import com.bdd.mer.derivation.elements.containers.Holder;
import com.bdd.mer.derivation.elements.containers.Replacer;
import com.bdd.mer.derivation.elements.containers.sources.Common;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PluralDerivation extends DerivationObject {

    private Derivation mainDerivation;
    private final List<Member> members;

    public PluralDerivation(String name) {
        super(name);
        this.members = new ArrayList<>();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    @Override
    public void generateDerivation() {

        int numberOfMembers = this.getNumberOfMembers();
        this.mainDerivation = this.generateOwnDerivation();

        switch (numberOfMembers) {
            case 2: manageBinaryRelationship(); break;
            case 3: manageTernaryRelationship(); break;
            default: throw new RuntimeException("Invalid number of members for a plural derivation.");
        }

        // In case the relationship derivation was not removed.
        if (mainDerivation != null) {
            this.addDerivation(mainDerivation);
        }
    }

    private void manageTernaryRelationship() {

        int oneCount = 0;

        Member oneCardinalityMember = null;

        for (Member member : members) {

            if (member.maxCardinality.equals("1")) {

                oneCount++;

                if (oneCardinalityMember == null) {
                    oneCardinalityMember = member;
                }
            }
        }

        Holder mostUsedHolder = new Replacer(ElementDecorator.FOREIGN);

        switch (oneCount) {
            case 0: // N:N:N

                this.mainDerivation.addIdentificationElement(
                        new SingleElement(this.members.getFirst().name, mostUsedHolder)
                );
                this.mainDerivation.addIdentificationElement(
                        new SingleElement(this.members.get(1).name, mostUsedHolder)
                );
                this.mainDerivation.addIdentificationElement(
                        new SingleElement(this.members.getLast().name, mostUsedHolder)
                );

                break;
            case 1, 2, 3: // 1:N:N

                for (Member member : this.members) {

                    if (!member.equals(oneCardinalityMember)) {

                        this.mainDerivation.addIdentificationElement(
                                new SingleElement(member.name, mostUsedHolder)
                        );
                    } else {

                        this.mainDerivation.addCommonElement(
                                new SingleElement(member.name, new Replacer(ElementDecorator.FOREIGN))
                        );
                    }
                }
                break;
            // 1:1:N -- The difference here is that there are more possible combinations. But the logic is the same as case 1 if we only want one combination.
            //case 2: break;
            // 1:1:1 -- Again, more possible, combinations.
            //case 3: break;
            default: throw new RuntimeException("Invalid cardinality for ternary relationship.");
        }
    }

    private void manageBinaryRelationship() {

        Member firstMember = this.members.getFirst();
        Member secondMember = this.members.getLast();

        if (firstMember.name.equals(secondMember.name)) {
            manageUnaryRelationship(firstMember, secondMember);
            return;
        }

        if (firstMember.maxCardinality.equals("1") || secondMember.maxCardinality.equals("1")) {

            if (firstMember.maxCardinality.equals("1") && secondMember.maxCardinality.equals("1")) {
                derivate1_1Relationship(firstMember, secondMember);
            } else {
                derivate1_NRelationship(firstMember, secondMember);
            }
        } else {
            derivateN_NRelationship(firstMember, secondMember);
        }
    }

    private void manageUnaryRelationship(Member firstMember, Member secondMember) {

        if (firstMember.maxCardinality.equals("1") || secondMember.maxCardinality.equals("1")) {

            if (firstMember.maxCardinality.equals("1") && secondMember.maxCardinality.equals("1")) {
                // I couldn't find any rules for this kind of derivation.
                derivate1_1Relationship(firstMember, secondMember);
            } else {
                derivate1_NUnaryRelationship(firstMember, secondMember);
            }
        } else {
            derivateN_NRelationship(firstMember, secondMember);
        }

    }

    private void derivate1_NUnaryRelationship(Member firstMember, Member secondMember) {

        String minCardinality = (firstMember.maxCardinality.equals("1")) ? firstMember.minCardinality : secondMember.minCardinality;

        Derivation derivation = new Derivation(firstMember.name);

        if (minCardinality.equals("0")) {

            if (!mainDerivation.isEmpty()) {
                derivation.addCommonElement(new SingleElement(this.getName(),
                        new Replacer(new Common(), ElementDecorator.OPTIONAL)));
            }

            derivation.addCommonElement(
                    new SingleElement(secondMember.name,
                            new Replacer(ElementDecorator.FOREIGN, ElementDecorator.OPTIONAL, ElementDecorator.DUPLICATED))
            );

        } else { // It's equal to 1.

            if (!mainDerivation.isEmpty()) {
                derivation.addCommonElement(new SingleElement(this.getName(),
                        new Replacer(new Common())));
            }

            derivation.addIdentificationElement(
                    new SingleElement(secondMember.name,
                            new Replacer(ElementDecorator.FOREIGN, ElementDecorator.DUPLICATED))
            );
        }

        this.addDerivation(derivation);
    }

    private void derivate1_NRelationship(Member firstMember, Member secondMember) {

        Member oneSideMember = (firstMember.maxCardinality.equals("1")) ? firstMember : secondMember;
        Member nSideMember = (firstMember.maxCardinality.equals("1")) ? secondMember : firstMember;

        Derivation derivation = new Derivation(nSideMember.name);

        List<ElementDecorator> decorators = new ArrayList<>();

        // If the one side member has a cardinality of zero,
        // the relationship's attributes are also optional.
        if (oneSideMember.minCardinality.equals("0")) {
            decorators.add(ElementDecorator.OPTIONAL);
        }

        // If the main derivation has common attributes, the derivation will take them.
        if (!mainDerivation.isEmpty()) {
            derivation.addCommonElement(
                    new SingleElement(
                            this.getName(),
                            new Replacer(new Common(), decorators.toArray(new ElementDecorator[0]))
                    )
            );
        }

        decorators.add(ElementDecorator.FOREIGN);

        derivation.addCommonElement(
                new SingleElement(
                        oneSideMember.name,
                        new Replacer(decorators.toArray(new ElementDecorator[0]))
                )
        );

        this.addDerivation(derivation);
    }

    private void derivate1_1Relationship(Member firstMember, Member secondMember) {

        // The member who takes the identification attributes of the other member.
        Member mainMember, secondaryMember;
        Derivation derivation;
        List<ElementDecorator> decorators = new ArrayList<>();
        decorators.add(ElementDecorator.FOREIGN);
        decorators.add(ElementDecorator.ALTERNATIVE);

        // Case (0,1):(1,1).
        if (!firstMember.minCardinality.equals(secondMember.minCardinality)) {

            mainMember = (firstMember.minCardinality.equals("0")) ? firstMember : secondMember;

        } else { // Cases (0,1):(0,1) and (1,1):(1,1). Here the member chosen doesn't matter. There are two valid derivations.

            if (firstMember.minCardinality.equals("0")) { // Case (0,1):(0,1).
                decorators.add(ElementDecorator.OPTIONAL);
                decorators.remove(ElementDecorator.ALTERNATIVE);
            }

            Random selector = new Random();

            mainMember = selector.nextBoolean() ? firstMember : secondMember;
        }

        secondaryMember = (mainMember.equals(firstMember)) ? secondMember : firstMember;

        derivation = new Derivation(mainMember.name);

        derivation.addCommonElement(
                new SingleElement(secondaryMember.name,
                        new Replacer(decorators.toArray(new ElementDecorator[0])))
        );

        if (!mainDerivation.isEmpty()) {

            // The derivation member has all the common attributes of the relationship.
            derivation.addCommonElement(
                    new SingleElement(this.getName(), new Replacer(new Common()))
            );
        }

        this.addDerivation(derivation);
    }

    private void derivateN_NRelationship(Member firstMember, Member secondMember) {

        // The names of the members will be replaced with their identification attributes.
        this.mainDerivation.addIdentificationElement(
                new SingleElement(firstMember.name, new Replacer(ElementDecorator.FOREIGN))
        );

        List<ElementDecorator> decoratorsForSecondMember = new ArrayList<>();
        decoratorsForSecondMember.add(ElementDecorator.FOREIGN);

        // N:N unary relationship.
        if (firstMember.name.equals(secondMember.name)) {
            decoratorsForSecondMember.add(ElementDecorator.DUPLICATED);
        }

        this.mainDerivation.addIdentificationElement(
                new SingleElement(secondMember.name,
                        new Replacer(decoratorsForSecondMember.toArray(new ElementDecorator[0])))
        );
    }

    private int getNumberOfMembers() {
        return this.members.size();
    }

    public static class Member {

        private final String name;
        private final String minCardinality;
        private final String maxCardinality;

        public Member(@NotNull String name, @NotNull String minCardinality, @NotNull String maxCardinality) {
            this.name = name;
            this.minCardinality = adaptMinCardinality(minCardinality);
            this.maxCardinality = adaptMaxCardinality(maxCardinality);
        }

        /**
         * Minimum cardinalities, in the derivation format, can only take two values: 0 or 1.
         * If the minimum cardinality has a value greater than one, it'll be replaced by 1 and
         * the user will have to specify a business rule for that minimum.
         *
         * @param minCardinality Minimum cardinality.
         * @return The minimum cardinality adapted to the derivation rules.
         */
        private String adaptMinCardinality(@NotNull String minCardinality) {

            if (minCardinality.isEmpty()) {
                throw new IllegalArgumentException("Min cardinality cannot be empty.");
            }

            try {
                int minCardinalityParse = Integer.parseInt(minCardinality);

                if (minCardinalityParse < 0) {
                    throw new IllegalArgumentException("Min cardinality cannot be negative.");
                }

                if (minCardinalityParse > 1) {
                    return  "1";
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Min cardinality must be a number.");
            }

            return minCardinality;
        }

        /**
         * Minimum cardinalities, in the derivation format, can only take two values: 1 or N (another letter can be used).
         * If the maximum cardinality has a value greater than one and different from N, it'll be replaced by N and
         * the user will have to specify a business rule for that maximum.
         *
         * @param maxCardinality Maximum cardinality.
         * @return The maximum cardinality adapted to the derivation rules.
         */
        private String adaptMaxCardinality(@NotNull String maxCardinality) {

            if (maxCardinality.isEmpty()) {
                throw new IllegalArgumentException("Min cardinality cannot be empty.");
            }

            try { // It's a number.
                int maxCardinalityParse = Integer.parseInt(maxCardinality);

                if (maxCardinalityParse < 1) {
                    throw new IllegalArgumentException("Max cardinality cannot be less than 1.");
                }

                if (maxCardinalityParse > 1) {
                    return  "N";
                }
            } catch (NumberFormatException e) { // It's okay if it's a number.
                return maxCardinality;
            }

            return maxCardinality;
        }
    }
}
