module common {
    requires java.desktop;
    requires org.jetbrains.annotations;
    requires java.prefs;
    requires java.logging;

    exports com.iroumec.gui;
    exports com.iroumec.structures;
    exports com.iroumec.components;
    exports com.iroumec.executables;
    exports com.iroumec.userPreferences;
    exports com.iroumec.components.basicComponents;
    exports com.iroumec.components.basicComponents.line.lineShape;
    exports com.iroumec.components.basicComponents.line.lineMultiplicity;
}