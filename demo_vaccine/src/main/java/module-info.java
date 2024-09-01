module com.example.demo_vaccine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.demo_vaccine to javafx.fxml;
    exports com.example.demo_vaccine;
}