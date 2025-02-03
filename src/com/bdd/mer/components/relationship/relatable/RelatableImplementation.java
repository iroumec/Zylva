package com.bdd.mer.components.relationship.relatable;

import com.bdd.mer.components.relationship.Relationship;

import java.util.ArrayList;
import java.util.List;

// I found myself fighting with a problem where I needed to use multiple inheritance.
// This class appears due to I decided to use compositing over inheritance design in this problem to solve it.
public class RelatableImplementation implements Relatable {

    private final List<Relationship> relationships;

    public RelatableImplementation() {
        this.relationships = new ArrayList<>();
    }

    public void addRelationship(Relationship relationship) {

        if (!this.relationships.contains(relationship)) {
            this.relationships.add(relationship);
        }
    }

    public void removeRelationship(Relationship relationship) { this.relationships.remove(relationship); }

    public List<Relationship> getRelationships() { return new ArrayList<>(this.relationships); }

}
