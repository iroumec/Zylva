package com.bdd.mer.components.relationship.relatable;

import com.bdd.mer.components.relationship.Relationship;

import java.io.Serializable;

public interface Relatable extends Serializable {

    void addRelationship(Relationship relationship);

    void removeRelationship(Relationship relationship);
}
