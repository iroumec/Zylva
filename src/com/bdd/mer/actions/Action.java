package com.bdd.mer.actions;

import com.bdd.mer.frame.LanguageManager;

public enum Action {

    DELETE("option.delete"),
    RENAME("option.rename"),
    ADD_ENTITY("option.addEntity"),
    ADD_RELATIONSHIP("option.addRelationship"),
    ADD_DEPENDENCY("option.addDependency"),
    ADD_ATTRIBUTE("option.addAttribute"),
    ADD_COMPLEX_ATTRIBUTE("option.addAttribute"),
    ADD_ASSOCIATION("option.addAssociation"),
    SWAP_OPTIONALITY("option.swapOptionality"),
    SWAP_MULTIVALUED("option.swapMultivalued"),
    SWAP_EXCLUSIVITY("option.swapExclusivity"),
    CHANGE_CARDINALITY("option.changeValues"),
    ADD_NOTE("option.addNote"),
    CHANGE_TEXT("option.changeText");

    private final String key;

    Action(String key) {
        this.key = key;
    }

    public String getText() {
        return LanguageManager.getMessage(key);
    }

}
