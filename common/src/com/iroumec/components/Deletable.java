package com.iroumec.components;

public interface Deletable {

    /**
     * Cleans all the references to the component.
     * <p></p>
     * At the moment of implementing this method, it must be taken into consideration that the component attached
     * is no longer available in the diagram. So, all references to it must be eliminated.
     * <p></p>
     * If the component cannot exist after deleting all the references to the attached component,
     * the method {@code notifyRemovingOf()} has been bad implemented. This method shouldn't efectuate any
     * deletion for the correct working of the application.
     * <p></p>
     * By default, due to not all deletable objects might have the need of cleaning references, it's left empty
     * on purpose. Another reason why it could be left empty is that the class doesn't have any important reference
     * that would not produce its elimination in the notifyRemovingOf() method (and so, this method wouldn't be called).
     *
     * @param component Component no longer available in the diagram.
     */
    default void cleanReferencesTo(Component component) {}

    /**
     * The component notified handle the removing of the component attached in case of being related to it.
     * <p></p>
     * Despite the component notified depends in existence on the component attached in the notification,
     * it's not guaranteed that it will be removed until all related components have been analyzed. For that reason,
     * the component should not implement any change in its values at the moment of implementing this method.
     * <p></p>
     * By default, due to not all deletable objects might have the need of notifying its removing, it's left empty
     * on purpose.
     *
     * @param component Component attached. If everything goes okay, it will be removed from the diagram.
     */
    default void notifyRemovingOf(Component component) {}

    /**
     * @return A boolean indicating if the component can be deleted or not. By default, due to not all deletable objects
     * might have problems with being deleted, it returns {@code TRUE}.
     */
    default boolean canBeDeleted() { return true; }
}
