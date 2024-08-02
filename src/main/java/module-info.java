module org.example.mtgspotscrapper {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.remote_driver;
    requires org.seleniumhq.selenium.support;
    requires java.sql;
    requires org.json;
    requires org.slf4j;
    requires dev.failsafe.core;
    requires javasdk;
    requires org.checkerframework.checker.qual;
    requires org.controlsfx.controls;
    requires org.jooq;
    requires jdk.security.auth;
    requires com.fasterxml.jackson.core;
//    requires org.apache.commons.csv;

    opens org.example.mtgspotscrapper.view to javafx.fxml;

    exports org.example.mtgspotscrapper;
    exports org.example.mtgspotscrapper.model.databaseClasses.tables.records;
    opens org.example.mtgspotscrapper.view.sidesManagers.rightPanesImplementations to javafx.fxml;
    opens org.example.mtgspotscrapper.view.cardLogoAndNameImpl to javafx.fxml;
}