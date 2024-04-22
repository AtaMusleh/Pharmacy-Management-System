package com.example.db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class ViewStoresFunctionality extends VBox {

    private TableView<Store> storesTable;

    public ViewStoresFunctionality(Menu menu) {
        initializeUI(menu);
    }

    private void initializeUI(Menu menu) {

        Label storesLabel = new Label("Stores Functionality");
        storesTable = createStoresTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Store");
        addButton.setOnAction(e-> handleAddStore());
        Button updateButton = new Button("Update Store");
        updateButton.setOnAction(e->handleUpdateStore());
        Button deleteButton = new Button("Delete Store");
        deleteButton.setOnAction(e->handleDeleteStore());
        Button searchButton = new Button("Search Store");
        searchButton.setOnAction(e -> {
            Store foundStore = handleSearchStore();

            if (foundStore != null) {
                ObservableList<Store> foundStoreList = FXCollections.observableArrayList();
                foundStoreList.add(foundStore);
                storesTable.setItems(foundStoreList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(storesLabel, storesTable, buttonsLayout);
        fetchAndDisplayStoresData();
    }

    private TableView<Store> createStoresTableView() {
        TableView<Store> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Store, String> storeNameCol = new TableColumn<>("Store Name");
        TableColumn<Store, String> storeIDCol = new TableColumn<>("Store ID");
        TableColumn<Store, String> addressCol = new TableColumn<>("Address");
        TableColumn<Store, String> phoneNumberCol = new TableColumn<>("Phone Number");

        storeNameCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        storeIDCol.setCellValueFactory(new PropertyValueFactory<>("storeID"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        tableView.getColumns().addAll(storeNameCol, storeIDCol, addressCol, phoneNumberCol);

        return tableView;
    }

    private void fetchAndDisplayStoresData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Store");

            ObservableList<Store> storesData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Store store = new Store(
                        resultSet.getString("storeName"),
                        resultSet.getString("storeID"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber")
                );

                storesData.add(store);
            }

            storesTable.setItems(storesData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleAddStore() {
        // Create a dialog
        Alert addStoreDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addStoreDialog.setTitle("Add Store");
        addStoreDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField storeNameField = new TextField();
        TextField storeIDField = new TextField();
        TextField addressField = new TextField();
        TextField phoneNumberField = new TextField();

        grid.add(new Label("Store Name:"), 0, 0);
        grid.add(storeNameField, 1, 0);
        grid.add(new Label("Store ID:"), 0, 1);
        grid.add(storeIDField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneNumberField, 1, 3);

        addStoreDialog.getDialogPane().setContent(grid);

        // Set the button types
        addStoreDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addStoreDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String storeName = storeNameField.getText();
                String storeID = storeIDField.getText();
                String address = addressField.getText();
                String phoneNumber = phoneNumberField.getText();

                // Implement logic to add the store with the provided information to the database
                addStoreToDatabase(storeName, storeID, address, phoneNumber);

                // Refresh the stores table after adding
                fetchAndDisplayStoresData();
            }
        });
    }

private void addStoreToDatabase(String storeName, String storeID, String address, String phoneNumber) {
    try (Connection connection = DatabaseConnection.getConnection();
         Statement statement = connection.createStatement()) {

        // Check if the store with the same storeID already exists
        String checkQuery = String.format("SELECT * FROM Store WHERE storeID = '%s'", storeID);
        ResultSet resultSet = statement.executeQuery(checkQuery);

        if (resultSet.next()) {
            // Store with the same storeID already exists, show an alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add Store");
            alert.setHeaderText(null);
            alert.setContentText("Store with the same ID already exists.");
            alert.showAndWait();
        } else {
            // Insert the new store into the Store table
            String insertQuery = String.format("INSERT INTO Store VALUES ('%s', '%s', '%s', '%s')",
                    storeName, storeID, address, phoneNumber);
            statement.executeUpdate(insertQuery);

            System.out.println("Store added to the database.");

        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void handleDeleteStore() {
        // Get the selected store from the table
        Store selectedStore = storesTable.getSelectionModel().getSelectedItem();

        if (selectedStore == null) {
            // If no store is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Store Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a store to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Store");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete the selected store?");

        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, proceed with deletion
                deleteStoreFromDatabase(selectedStore);

                // Refresh the stores table after deletion
                fetchAndDisplayStoresData();
            }
        });
    }

    private void deleteStoreFromDatabase(Store store) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Delete the selected store from the Store table
            String deleteQuery = String.format("DELETE FROM Store WHERE storeID = '%s'", store.getStoreID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Store deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateStore() {
        // Get the selected store from the table
        Store selectedStore = storesTable.getSelectionModel().getSelectedItem();

        if (selectedStore == null) {
            // If no store is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Store Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a store to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating store information
        Alert updateStoreAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateStoreAlert.setTitle("Update Store");
        updateStoreAlert.setHeaderText(null);
        updateStoreAlert.setContentText("Update the store information:");

        // Create labels and text fields for store information
        Label storeIDLabel = new Label("Store ID:");
        TextField storeIDTextField = new TextField(selectedStore.getStoreID());
        //storeIDTextField.setEditable(false);

        Label storeNameLabel = new Label("Store Name:");
        TextField storeNameTextField = new TextField(selectedStore.getStoreName());

        Label addressLabel = new Label("Address:");
        TextField addressTextField = new TextField(selectedStore.getAddress());

        Label phoneNumberLabel = new Label("Phone Number:");
        TextField phoneNumberTextField = new TextField(selectedStore.getPhoneNumber());

        VBox updateStoreLayout = new VBox(10,
                storeIDLabel, storeIDTextField,
                storeNameLabel, storeNameTextField,
                addressLabel, addressTextField,
                phoneNumberLabel, phoneNumberTextField);

        updateStoreLayout.setAlignment(Pos.CENTER);

        updateStoreAlert.getDialogPane().setContent(updateStoreLayout);

        // Set the button types for the alert
        updateStoreAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateStoreAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the store information
                updateStoreInDatabase(selectedStore,
                        storeNameTextField.getText(),
                        addressTextField.getText(),
                        phoneNumberTextField.getText());

                // Refresh the stores table after updating
                fetchAndDisplayStoresData();
            }
        });
    }

    private void updateStoreInDatabase(Store store, String newStoreName, String newAddress, String newPhoneNumber) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected store in the Store table
            String updateQuery = String.format("UPDATE Store SET storeName = '%s', address = '%s', phoneNumber = '%s' " +
                            "WHERE storeID = '%s'",
                    newStoreName, newAddress, newPhoneNumber, store.getStoreID());
            statement.executeUpdate(updateQuery);

            System.out.println("Store updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void handleSearchStore1() {
        Store foundStore = handleSearchStore();

        if (foundStore != null) {
            ObservableList<Store> foundStoreList = FXCollections.observableArrayList();
            foundStoreList.add(foundStore);
            storesTable.setItems(foundStoreList);
        }
    }

    private Store handleSearchStore() {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Store");
        searchDialog.setHeaderText(null);
        searchDialog.setContentText("Enter Store ID:");

        Optional<String> result = searchDialog.showAndWait();

        return result.map(this::searchStoreInDatabase).orElse(null);
    }

    private Store searchStoreInDatabase(String storeID) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM Store WHERE storeID = ?")) {

            preparedStatement.setString(1, storeID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Store(
                        resultSet.getString("storeName"),
                        resultSet.getString("storeID"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if store is not found
        return null;
    }
}
