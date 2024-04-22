package com.example.db;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Application {
    Stage stage = new Stage();
    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // Initialize the database connection
        try {
        	//Edit it with your database
        	connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database Connection Error", "Unable to connect to the database.");
            return;
        }

        Image logo = new Image("/logo.png");
        ImageView logoV = new ImageView(logo);
        Label top = new Label(" Al-Issa pharmacy");
        top.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        top.setTextFill(Color.GREEN);

        VBox topV = new VBox(10, top, logoV);
        topV.setAlignment(Pos.CENTER);

        Image userImage = new Image("/user.png");
        ImageView userV = new ImageView(userImage);
        Circle clip = new Circle(userImage.getWidth() / 2, userImage.getHeight() / 2,
                Math.min(userImage.getWidth(), userImage.getHeight()) / 2);

        userV.setClip(clip);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(userV);

        Label username = new Label("Username: ");
        Label password = new Label("Password: ");
        TextField un = new TextField(null);
        un.setPromptText("Username");
        un.setStyle("-fx-pref-width: 200; -fx-max-width: 200;");
        PasswordField pw = new PasswordField();
        pw.setPromptText("Password");
        pw.setStyle("-fx-pref-width: 200; -fx-max-width: 200;");   // Set preferred width for PasswordField

        VBox vv = new VBox(10, un, pw);
        vv.setPrefWidth(20);
        vv.setAlignment(Pos.CENTER);
        Button login = new Button("Login");
        login.setStyle("-fx-pref-width: 200;");
        VBox v1 = new VBox(10, userV, vv, login);
        v1.setAlignment(Pos.CENTER);
        login.setOnAction(e -> handleLogin(un.getText(), pw.getText()));

        BorderPane bp = new BorderPane();
        bp.setTop(topV);
        bp.setCenter(v1);

        Scene s = new Scene(bp, 500, 500);
        stage.setResizable(false);
        stage.setScene(s);
        stage.show();
    }


//private void handleLogin(String enteredUsername, String enteredPassword) {
//    try {
//        // Check the credentials against the employee table
//        String sql = "SELECT * FROM employee WHERE FirstName = ? AND EmployeeID = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setString(1, enteredUsername);
//            preparedStatement.setString(2, enteredPassword);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            if (resultSet.next()) {
//                // Successful login
//                String username = resultSet.getString("FirstName"); // Get the actual username from the result set
//                Menu m = new Menu(username); // Pass the username to the Menu instance
//                stage.close();
//                m.start(stage);
//            } else {
//                showAlert("Error!", "Invalid credentials", "Please enter valid username and password.");
//            }
//        }
//    } catch (SQLException e) {
//        e.printStackTrace();
//        showAlert("Error", "Database Error", "Error while querying the database.");
//    }
private void handleLogin(String enteredUsername, String enteredPassword) {
    try {
        // Check if the entered credentials are the default credentials
        if (enteredUsername.equals("root") && enteredPassword.equals("123455")) {
            // Successful login
            Menu m = new Menu(enteredUsername); // Pass the entered username to the Menu instance
            stage.close();
            m.start(stage);
            return;
        }

        // Check the credentials against the employee table
        String sql = "SELECT * FROM employee WHERE FirstName = ? AND EmployeeID = ? AND position = 'Manager'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, enteredUsername);
            preparedStatement.setString(2, enteredPassword);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Successful login
                String username = resultSet.getString("FirstName"); // Get the actual username from the result set
                Menu m = new Menu(username); // Pass the username to the Menu instance
                stage.close();
                m.start(stage);
            } else {
                showAlert("Error!", "Invalid credentials", "Please enter valid username and password.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Database Error", "Error while querying the database.");
    }
}




    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
