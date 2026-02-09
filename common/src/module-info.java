module common {
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires java.prefs;
    requires java.logging;

    exports com.zylva.core;
    exports com.zylva.structures;
    exports com.zylva.userPreferences;
    exports com.zylva.components.line.lineShape;
    exports com.zylva.components.line.lineMultiplicity;
    exports com.zylva.components;
}