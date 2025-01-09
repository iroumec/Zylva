package com.bdd.mer.components.relationship.relatable;

import com.bdd.mer.components.relationship.Relationship;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// I found myself fighting with a problem where I needed to use multiple inheritance.
// This class appear due to I decided to use compositing over inheritance design in this problem to solve it.
public class RelatableImplementation implements Relatable {

    private final List<Relationship> relationships;

    public RelatableImplementation() {
        this.relationships = new ArrayList<>();
    }

    public void addRelationship(Relationship relationship) { this.relationships.add(relationship); }

    public void removeRelationship(Relationship relationship) { this.relationships.remove(relationship); }

    @Override
    public void drawLinesToRelationships(Graphics2D graphics2D, int x, int y) {

        for (Relationship relationship : relationships) {
            graphics2D.drawLine(relationship.getX(), relationship.getY(), x, y);
        }
    }

    public List<Relationship> getRelationships() { return new ArrayList<>(this.relationships); }

}
