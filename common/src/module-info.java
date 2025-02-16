module common {
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires java.prefs;
    requires java.logging;

    exports com.iroumec.core;
    exports com.iroumec.structures;
    exports com.iroumec.userPreferences;
    exports com.iroumec.components.line.lineShape;
    exports com.iroumec.components.line.lineMultiplicity;
    exports com.iroumec.components;
}