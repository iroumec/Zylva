package com.bdd.mer.derivation.derivationObjects;

import com.bdd.mer.components.attribute.Attribute;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.derivation.Derivation;
import com.bdd.mer.derivation.DerivationFormater;
import com.bdd.mer.derivation.ReferencialIntegrityConstraint;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;

public class PluralDerivation extends DerivationObject {

    private final Derivation mainDerivation;
    private final List<Member> members;

    public PluralDerivation(String name) {
        super(name);
        this.members = new ArrayList<>();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    @Override
    public void addAttribute(Attribute attribute) {

        mainDerivation.addAttribute(parseAttribute(attribute));
    }

    @Override
    public void generateDerivation() {

        for (Member member : members) {
            member.name = member.name.toLowerCase();
        }
    }

    private void manageRelationship(String relationshipName, String cardinalities) {

        int numberOfMembers = this.getNumberOfMembers();

        switch (numberOfMembers) {
            case 2: manageBinaryRelationship(this.members.getFirst(), this.members.getLast()); break;
            case 3: manageTernaryRelationship(); break;
            default: throw new RuntimeException("Invalid number of members for a plural derivation.");
        }
    }

    private void manageTernaryRelationship() {

        Map<String, List<Integer>> cardinalityMap = new HashMap<>();

        int oneCount = 0;

        for (int i = 0; i < names.size(); i++) {

            String maxCardinality = maxCardinalities.get(i);

            if (maxCardinality.equals("1")) {
                oneCount++;
            }

            List<Integer> cardinality = cardinalityMap.getOrDefault(names.get(i), new ArrayList<>());
            cardinality.add(i);
            cardinalityMap.put(maxCardinality, cardinality);
        }

        List<ReferencialIntegrityConstraint> constraints = new ArrayList<>();
        Derivation relationshipDerivation = derivations.get(relationshipName);
        Derivation firstDerivation = derivations.get(names.getFirst());
        Derivation secondDerivation = derivations.get(names.get(1));
        Derivation thirdDerivation = derivations.get(names.getLast());
        List<Derivation> derivationList = Arrays.asList(firstDerivation, secondDerivation, thirdDerivation);

        switch (oneCount) {
            case 0: // N:N:N

                for (Derivation derivation : derivationList) {
                    constraints.add(
                            derivation.copyIdentificationAttributesAs(
                                    relationshipDerivation, DerivationFormater.FOREIGN_ATTRIBUTE));
                }

                break;
            case 1, 2, 3: // 1:N:N
                Derivation maxCardinalityEqualsToOneDerivation
                        = derivations.get(names.get(cardinalityMap.get("1").getFirst()));

                for (Derivation derivation : derivationList) {
                    if (!derivation.equals(maxCardinalityEqualsToOneDerivation)) {
                        constraints.add(derivation
                                .copyIdentificationAttributesAs(relationshipDerivation, DerivationFormater.FOREIGN_ATTRIBUTE));
                    } else {
                        constraints.add(derivation
                                .copyIdentificationAttributesAs(
                                        relationshipDerivation,
                                        DerivationFormater.FOREIGN_ATTRIBUTE,
                                        Derivation.AttributeType.COMMON
                                ));
                    }
                }
                break;
            // 1:1:N -- The difference here is that there are more possible combinations. But the logic is the same as case 1 if we only want one combination.
            //case 2: break;
            // 1:1:1 -- Again, more possible, combinations.
            //case 3: break;
            default: throw new RuntimeException("Invalid cardinality for ternary relationship.");
        }

        referentialIntegrityConstraints.addAll(constraints);
    }

    private static void manageBinaryRelationship(Member firstMember, Member secondMember) {

        if (firstMember.name.equals(lastMember.name)) {
            manageUnaryRelationship(firstMember, secondMember);
        }

        if (names.getFirst().equals(names.getLast())) {
            manageUnaryRelationship(relationshipName,names, minCardinalities, maxCardinalities);
            return;
        }

        if (maxCardinalities.getFirst().equals("1") || maxCardinalities.getLast().equals("1")) {

            if (maxCardinalities.getFirst().equals("1") && maxCardinalities.getLast().equals("1")) {
                derivate1_1Relationship(relationshipName, names, minCardinalities);
            } else {
                derivate1_NRelationship(relationshipName, names, minCardinalities, maxCardinalities);
            }
        } else {
            derivateN_NRelationship(relationshipName, names);
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

        Derivation firstMemberDerivation

        Derivation derivation = derivations.get(name);
        Derivation relationshipDerivation = derivations.get(relationshipName);
        relationshipDerivation.moveCommonAttributesTo(derivation);

        if (minCardinality.equals("0")) {
            referentialIntegrityConstraints.add(
                    derivation.copyIdentificationAttributesAs(
                            derivation,
                            DerivationFormater.OPTIONAL_ATTRIBUTE
                    ));
        } else { // It's equal to 1.
            referentialIntegrityConstraints.add(derivation.copyIdentificationAttributesAs(derivation));
        }
    }

    private static void derivate1_NRelationship(String relationshipName,
                                                List<String> names,
                                                List<String> minCardinalities,
                                                List<String> maxCardinalities) {
        String oneSideName = "", nSideName = "";
        String oneSideMinCardinality = "";

        for (int i = 0; i < names.size(); i++) {

            if (maxCardinalities.get(i).equals("1")) {
                oneSideName = names.get(i);
                oneSideMinCardinality = minCardinalities.get(i);
            } else {
                nSideName = names.get(i);
            }
        }

        Derivation nSideDerivation = derivations.get(nSideName);
        Derivation oneSideDerivation = derivations.get(oneSideName);
        Derivation relationshipDerivation = new Derivation(relationshipName);

        relationshipDerivation.moveAttributesTo(nSideDerivation);

        ReferencialIntegrityConstraint constraint;

        if (oneSideMinCardinality.equals("0")) {
            constraint = oneSideDerivation
                    .copyIdentificationAttributesAs(
                            nSideDerivation,
                            DerivationFormater.OPTIONAL_ATTRIBUTE
                    );
        } else {
            constraint = oneSideDerivation
                    .copyIdentificationAttributesAs(
                            nSideDerivation,
                            DerivationFormater.FOREIGN_ATTRIBUTE,
                            Derivation.AttributeType.COMMON
                    );
        }

        if (constraint != null) {
            referentialIntegrityConstraints.add(constraint);
        }

        derivations.remove(relationshipName);
    }

    private void derivate1_1Relationship(Member firstMember, Member secondMember) {

        if (firstMember.minCardinality.equals("0") || secondMember.minCardinality.equals("0")) {

            if (firstMember.minCardinality.equals("0") && secondMember.minCardinality.equals("0")) { // Case (0,1):(0,1)

                // The order could be different.
                Derivation derivation = new Derivation(firstMember.name);

                // The order could be different.
                this.mainDerivation.moveAttributesTo(derivation);
                this.addReferencialIntegrityConstraint(

                );
                referentialIntegrityConstraints.add(
                        lastDerivation.copyIdentificationAttributesAs(
                                firstDerivation,
                                DerivationFormater.FOREIGN_ATTRIBUTE + DerivationFormater.OPTIONAL_ATTRIBUTE
                        ));
            } else { // Case (0,1):(1,1)

                if (minCardinalities.getFirst().equals("0")) {

                    relationshipDerivation.moveAttributesTo(firstDerivation);
                    referentialIntegrityConstraints.add(lastDerivation.copyIdentificationAttributesAsAlternativeForeign(firstDerivation));
                } else {

                    relationshipDerivation.moveAttributesTo(lastDerivation);
                    referentialIntegrityConstraints.add(firstDerivation.copyIdentificationAttributesAsAlternativeForeign(lastDerivation));
                }
            }
        } else { // Case (1,1):(1,1)

            // In the case of (1,1):(1,1), the order could be different.

            relationshipDerivation.moveAttributesTo(firstDerivation);
            referentialIntegrityConstraints.add(lastDerivation.copyIdentificationAttributesAsAlternativeForeign(firstDerivation));
        }

        derivations.remove(relationshipName);
    }

    private void derivateN_NRelationship(Member firstMember, Member secondMember) {

        Derivation derivation = new Derivation(this.getName());

        // The names of the members will be replaced with their identification attributes.
        String prefix = DerivationFormater.MAIN_ATTRIBUTE + DerivationFormater.FOREIGN_ATTRIBUTE;
        derivation.addAttribute(prefix + firstMember.name);
        derivation.addAttribute(prefix + secondMember.name);

        this.addDerivation(derivation);
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
