package com.example.demo_vaccine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class UserDashboardPage extends DashboardPage {

    //UI elements
    private Button viewProfileButton;
    private Button viewMyVaccinationsButton;
    private Button viewInjectionScheduleButton;
    private Image image;


    public UserDashboardPage(VaccineManagementSystem app) {
        super(app, "User");
    }

    /**
     * Implement the addRoleSpecificButtons from the DashboardPage
     */
    @Override
    protected void addRoleSpecificButtons() {
        // Create buttons
        viewProfileButton = new Button("View My Profile");
        viewMyVaccinationsButton = new Button("View My Vaccination History");
        viewInjectionScheduleButton = new Button("View Injection Schedule");

        // Configure VBox layout
        pane.setSpacing(10); // Vertical spacing between buttons
        pane.setPadding(new Insets(10)); // Padding around VBox
        pane.setAlignment(Pos.CENTER); // Align buttons to the center

        // Load the image from resources
        image = new Image(getClass().getResource("health1.png").toExternalForm());
        ImageView imageView = new ImageView(image);

        // Set a color for the VBox
        pane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #bdfdf1;");

        // Add buttons to the VBox
        pane.getChildren().addAll(
                viewProfileButton,
                viewMyVaccinationsButton,
                viewInjectionScheduleButton,
                imageView // Add the imageView at the bottom
        );

        // Set button actions
        viewProfileButton.setOnAction(e -> app.showPatientProfilePage());
        viewMyVaccinationsButton.setOnAction(e -> app.showVaccinationHistoryPage());
        viewInjectionScheduleButton.setOnAction(e -> app.showUserInjectionSchedulePage());

    }
}