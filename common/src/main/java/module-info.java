module common {
    requires java.prefs;
    requires java.logging;
    requires org.jetbrains.annotations;

    requires transitive java.desktop;
    requires org.jspecify;

    exports com.zylva.common.core;
    exports com.zylva.common.structures;
    exports com.zylva.common.components;
    exports com.zylva.common.userPreferences;
    exports com.zylva.common.components.line.lineShape;
    exports com.zylva.common.components.line.lineMultiplicity;
}