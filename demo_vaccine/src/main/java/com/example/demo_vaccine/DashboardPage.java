package com.example.demo_vaccine;

import com.example.demo_vaccine.VaccineManagementSystem;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class DashboardPage {
    protected VaccineManagementSystem app;
    protected VBox pane;
    private Text title;
    private Button logoutButton;

    public DashboardPage(VaccineManagementSystem app, String role) {
        this.app = app;
        pane = new VBox();


        if("admin".equalsIgnoreCase(app.getUserRole())){
            title = new Text("Welcome " + role );
        }else {
            title = new Text("Welcome " + role + " " + app.getName());
        }
        pane.getChildren().add(title);



        addRoleSpecificButtons(); // Abstract method for role-specific buttons

        logoutButton = new Button("Log out");
        pane.getChildren().add(logoutButton);
        // Handle logout button action
        logoutButton.setOnAction(e -> {
            app.showLoginPage(); // Ensure this properly transitions to the login screen
        });
    }

    /**
     * Method to be implemented by subclasses
     */
    protected abstract void addRoleSpecificButtons();

    public VBox getPane() {
        return pane;
    }
}
