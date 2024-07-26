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

    opens org.example.mtgspotscrapper.view to javafx.fxml;

    exports org.example.mtgspotscrapper;
    opens org.example.mtgspotscrapper.view.rightPanesImplementations to javafx.fxml;
    opens org.example.mtgspotscrapper.view.cardLogoAndNameImpl to javafx.fxml;
}