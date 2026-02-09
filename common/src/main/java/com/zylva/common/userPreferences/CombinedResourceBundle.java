package com.zylva.common.userPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CombinedResourceBundle extends ResourceBundle {

    /**
     * Array of {@code ResourceBundle}s that participe in the combination.
     */
    private final ResourceBundle[] bundles;

    /**
     * Constructs a {@code CombinedResourceBundle}.
     *
     * @param bundles {@code ResourceBundle}s conforming it.
     */
    public CombinedResourceBundle(ResourceBundle... bundles) {
        this.bundles = bundles;
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected Object handleGetObject(@NotNull String key) {
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return bundle.getObject(key);
            }
        }
        return null;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public @NotNull Enumeration<String> getKeys() {
        Set<String> keys = new HashSet<>();
        for (ResourceBundle bundle : bundles) {
            Enumeration<String> bundleKeys = bundle.getKeys();
            while (bundleKeys.hasMoreElements()) {
                keys.add(bundleKeys.nextElement());
            }
        }
        return Collections.enumeration(keys);
    }
}
