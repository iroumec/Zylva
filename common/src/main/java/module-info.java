module common {
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires java.prefs;
    requires java.logging;

    exports com.zylva.common.core;
    exports com.zylva.common.structures;
    exports com.zylva.common.userPreferences;
    exports com.zylva.common.components.line.lineShape;
    exports com.zylva.common.components.line.lineMultiplicity;
    exports com.zylva.common.components;
}