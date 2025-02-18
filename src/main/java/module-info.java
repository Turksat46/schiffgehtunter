module com.turksat46.schiffgehtunter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.media;
    requires com.google.gson;


    opens com.turksat46.schiffgehtunter to javafx.fxml;
    opens com.turksat46.schiffgehtunter.netzwerk to javafx.fxml;
    opens com.turksat46.schiffgehtunter.other to com.google.gson;
    exports com.turksat46.schiffgehtunter;
    exports com.turksat46.schiffgehtunter.netzwerk;
    exports com.turksat46.schiffgehtunter.other;
}