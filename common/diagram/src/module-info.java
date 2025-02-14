module diagram {
    requires java.logging;

    requires userPreferences;
    requires org.jetbrains.annotations;
    requires java.desktop;

    exports com.iroumec to gui;
    exports com.iroumec.components.line;
    exports com.iroumec.components.note;
    exports com.iroumec.components.line.guard;
    exports com.iroumec.components to gui;
}