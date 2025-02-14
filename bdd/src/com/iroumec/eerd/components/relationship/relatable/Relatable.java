package com.iroumec.eerd.components.relationship.relatable;

import com.iroumec.eerd.components.relationship.Relationship;

import java.io.Serializable;

public interface Relatable extends Serializable {

    void addRelationship(Relationship relationship);

    void removeRelationship(Relationship relationship);
}
