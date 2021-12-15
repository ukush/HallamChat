module com.nsd.hallamchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens com.nsd.hallamchat to javafx.fxml;
    exports com.nsd.hallamchat;
}