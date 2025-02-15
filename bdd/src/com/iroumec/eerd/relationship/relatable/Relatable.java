package com.iroumec.eerd.relationship.relatable;

import com.iroumec.eerd.relationship.Relationship;

import java.io.Serializable;

public interface Relatable extends Serializable {

    void addRelationship(Relationship relationship);

    void removeRelationship(Relationship relationship);
}
