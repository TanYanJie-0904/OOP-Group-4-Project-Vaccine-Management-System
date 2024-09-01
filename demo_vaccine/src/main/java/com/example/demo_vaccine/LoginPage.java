package com.example.demo_vaccine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage {
    private VaccineManagementSystem app;
    private GridPane pane;

    //UI elements
    private Label userLabel;
    private TextField userField;
    private Label passLabel;
    private PasswordField passField;
    private Button loginButton;
    private Button registerButton;
    private HBox buttonBox;



    public LoginPage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();

        // Configure GridPane layout
        pane.setPadding(new Insets(20, 20, 20, 20)); // Padding around the grid
        pane.setVgap(10); // Vertical gap between elements
        pane.setHgap(10); // Horizontal gap between elements
        pane.setAlignment(Pos.CENTER); // Center the grid

        // Create UI elements
        userLabel = new Label("Username:");
        userField = new TextField();
        passLabel = new Label("Password:");
        passField = new PasswordField();
        loginButton = new Button("Login");
        registerButton = new Button("Register");

        // Organize buttons horizontally using HBox
        buttonBox = new HBox(10); // Horizontal box with 10px spacing
        buttonBox.setAlignment(Pos.CENTER); // Center align buttons
        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Add elements to the GridPane
        pane.add(userLabel, 0, 0);
        pane.add(userField, 1, 0);
        pane.add(passLabel, 0, 1);
        pane.add(passField, 1, 1);
        pane.add(buttonBox, 1, 2); // Add button box to the grid

        // Handle login button action
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            try (Connection connection = DatabaseUtil.getConnection()) {
                // First, get the user's role based on the username
                String roleQuery = "SELECT role FROM users WHERE username = ?";
                try (PreparedStatement roleStatement = connection.prepareStatement(roleQuery)) {
                    roleStatement.setString(1, username);
                    try (ResultSet roleResultSet = roleStatement.executeQuery()) {
                        if (roleResultSet.next()) {
                            String role = roleResultSet.getString("role");
                            String sql;
                            // Construct the SQL query based on the retrieved role
                            if ("user".equalsIgnoreCase(role)) {
                                sql = "SELECT u.id, u.role, p.name, p.gender " +
                                        "FROM users u " +
                                        "JOIN patients p ON u.id = p.user_id " +
                                        "WHERE u.username = ? AND u.password = ?";
                            } else {
                                sql = "SELECT u.id, u.role " +
                                        "FROM users u " +
                                        "WHERE u.username = ? AND u.password = ?";
                            }
                            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                                statement.setString(1, username);
                                statement.setString(2, password);
                                try (ResultSet resultSet = statement.executeQuery()) {
                                    if (resultSet.next()) {
                                        int userId = resultSet.getInt("id");
                                        String name = null;
                                        String gender = null;
                                        if ("user".equalsIgnoreCase(role)) {
                                            name = resultSet.getString("name");
                                            gender = resultSet.getString("gender");
                                        }
                                        app.setName(name);
                                        app.setGender(gender);
                                        app.setUser(userId, role); // Set user ID and role in VaccineManagementSystem
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Log in Failed", "Invalid login credentials.");
                                    }
                                }
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Log in Failed", "Invalid username.");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });



        // Handle register button action
        registerButton.setOnAction(e -> app.showRegistrationPage());
    }


    /**
     * @param alertType the type of the alert such as information, error...
     * @param title the title of the message box
     * @param message the message to be delivered to the users
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GridPane getPane() {
        return pane;
    }
}
