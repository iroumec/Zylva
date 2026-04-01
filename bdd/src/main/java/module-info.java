module bdd {
    requires java.logging;
    requires java.sql;
    requires org.jetbrains.annotations;

    requires transitive java.desktop;

    requires transitive common;

    exports com.zylva.bdd;
}