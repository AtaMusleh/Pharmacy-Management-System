package com.example.db;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Menu extends Application {
    Stage stage;
    VBox sidePanel;
    Label userLabel;
    public Menu() {
        // Default constructor with no arguments
    }
    public Menu(String username) {

        userLabel = new Label("Logged in as: " + username);
        userLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black; -fx-font-weight: bold");
    }
    private void showLoginScreen() {
        if (stage != null) {
            stage.close(); // Close the current menu stage
        }

        Login login = new Login();
        try {
            login.start(new Stage());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        initializeUI();
    }

    void initializeUI() {
        if (userLabel == null) {
            showLoginScreen();
            return;
        }
        Label top = new Label("Main Menu");
        top.setStyle("-fx-font-size: 30; -fx-font-weight: bold");

        // Buttons for the side panel
        Button storesButton = createButton("Stores");
        Button emp = createButton("Employees");
        Button product = createButton("Products");
        Button customers = createButton("Customers");
        Button supplier = createButton("Suppliers");
        Button warehouse = createButton("Warehouses");


        storesButton.setOnAction(e -> showFunctionality(new ViewStoresFunctionality(this)));
        emp.setOnAction(e -> showFunctionality(new ViewEmployeesFunctionality(this)));
        product.setOnAction(e -> showFunctionality(new ViewProductFunctionality(this)));
        customers.setOnAction(e -> showFunctionality(new ViewCustomerFunctionality(this)));
        supplier.setOnAction(e -> showFunctionality(new ViewSupplierFunctionality(this)));
        warehouse.setOnAction(e -> showFunctionality(new ViewWarehouseFunctionality(this)));
        Button logoutButton = createButton("Logout");
        logoutButton.setOnAction(e -> {

            Login login = new Login();
            try {
                login.start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });

        sidePanel = new VBox(20, userLabel, storesButton, emp, product, customers, supplier, warehouse, logoutButton);
        sidePanel.setAlignment(Pos.CENTER);

        VBox placeholder = new VBox(new Label("Select a functionality from the side panel."));
        placeholder.setAlignment(Pos.CENTER);


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(top);
        borderPane.setAlignment(top, Pos.CENTER);
        borderPane.setLeft(sidePanel);

        placeholder.setAlignment(Pos.CENTER);

        borderPane.setCenter(placeholder);

       // borderPane.setStyle("-fx-background-color: #dcf67e;");
       borderPane.setStyle("-fx-background-color: linear-gradient(to bottom, #a8c4a8, #ffffff);");
        Scene scene = new Scene(borderPane, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void showFunctionality(VBox functionality) {
        // Update the center of the BorderPane with the selected functionality
        BorderPane.setAlignment(functionality, Pos.CENTER);
        BorderPane.setMargin(functionality, new Insets(20, 20, 20, 20));
        BorderPane.setMargin(sidePanel, new Insets(20, 0, 20, 0));

        // Use the instance of Menu to access non-static methods
        initializeUI(); // Reset the UI
        BorderPane borderPane = (BorderPane) stage.getScene().getRoot();
        borderPane.setCenter(functionality);
    }



    public static void main(String[] args) {
        launch(args);
    }
    private Button createButton(String text) {
        Button button = new Button(text);
        ScaleTransition scaleTransition1 = new ScaleTransition(Duration.millis(200), button);
        scaleTransition1.setFromX(0.5);
        scaleTransition1.setFromY(0.5);
        scaleTransition1.setToX(1);
        scaleTransition1.setToY(1);
        button.setOnMouseEntered(event -> {
            // Pause the transition if it's running and play it again
            if (scaleTransition1.getStatus() == ScaleTransition.Status.RUNNING) {
                scaleTransition1.pause();
            }
            scaleTransition1.play();
        });


        button.setOnMouseExited(event -> {
            // Pause the transition if it's running and play it again
            if (scaleTransition1.getStatus() == ScaleTransition.Status.RUNNING) {
                scaleTransition1.pause();
            }
            scaleTransition1.play();
        });
        button.setStyle("-fx-font-size: 18; -fx-background-color: transparent; -fx-font-weight: bold;-fx-text-fill: black");
        return button;
    }
}
