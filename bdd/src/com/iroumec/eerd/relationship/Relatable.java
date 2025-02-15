package com.iroumec.eerd.relationship;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public interface Relatable extends Serializable {

    List<Rectangle> getAssociationBounds();
}
