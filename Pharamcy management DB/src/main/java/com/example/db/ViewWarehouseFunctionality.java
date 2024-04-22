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

public class ViewWarehouseFunctionality extends VBox {

    private TableView<Warehouse> warehouseTable;

//    public ViewWarehouseFunctionality() {
//        initializeUI();
//    }

    public ViewWarehouseFunctionality(Menu menu) {
        initializeUI(menu);


    }

    private void initializeUI(Menu menu) {
        Label warehouseLabel = new Label("Warehouse Functionality");
        warehouseTable = createWarehouseTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Warehouse");
        addButton.setOnAction(e -> handleAddWarehouse());
        Button updateButton = new Button("Update Warehouse");
        updateButton.setOnAction(e -> handleUpdateWarehouse());
        Button deleteButton = new Button("Delete Warehouse");
        deleteButton.setOnAction(e -> handleDeleteWarehouse());
        Button searchButton = new Button("Search Warehouse");
        searchButton.setOnAction(e -> {
            Warehouse foundWarehouse = handleSearchWarehouse();

            if (foundWarehouse != null) {
                ObservableList<Warehouse> foundWarehouseList = FXCollections.observableArrayList();
                foundWarehouseList.add(foundWarehouse);
                warehouseTable.setItems(foundWarehouseList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(warehouseLabel, warehouseTable, buttonsLayout);
        fetchAndDisplayWarehouseData();
        // Set action for the "View Warehouse" button
    }

    private TableView<Warehouse> createWarehouseTableView() {
        TableView<Warehouse> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Warehouse, String> addressCol = new TableColumn<>("Address");
        TableColumn<Warehouse, String> warehouseNameCol = new TableColumn<>("Warehouse Name");
        TableColumn<Warehouse, String> warehouseIDCol = new TableColumn<>("Warehouse ID");

        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        warehouseNameCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        warehouseIDCol.setCellValueFactory(new PropertyValueFactory<>("warehouseID"));

        tableView.getColumns().addAll(addressCol, warehouseNameCol, warehouseIDCol);

        return tableView;
    }

    private void fetchAndDisplayWarehouseData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Warehouse");

            ObservableList<Warehouse> warehouseData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Warehouse warehouse = new Warehouse(
                        resultSet.getString("address"),
                        resultSet.getString("warehouseName"),
                        resultSet.getString("warehouseID")
                );

                warehouseData.add(warehouse);
            }

            warehouseTable.setItems(warehouseData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddWarehouse() {
        // Create a dialog
        Alert addWarehouseDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addWarehouseDialog.setTitle("Add Warehouse");
        addWarehouseDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField addressField = new TextField();
        TextField warehouseNameField = new TextField();
        TextField warehouseIDField = new TextField();

        grid.add(new Label("Address:"), 0, 0);
        grid.add(addressField, 1, 0);
        grid.add(new Label("Warehouse Name:"), 0, 1);
        grid.add(warehouseNameField, 1, 1);
        grid.add(new Label("Warehouse ID:"), 0, 2);
        grid.add(warehouseIDField, 1, 2);

        addWarehouseDialog.getDialogPane().setContent(grid);

        // Set the button types
        addWarehouseDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addWarehouseDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String address = addressField.getText();
                String warehouseName = warehouseNameField.getText();
                String warehouseID = warehouseIDField.getText();

                // Implement logic to add the warehouse with the provided information to the database
                addWarehouseToDatabase(address, warehouseName, warehouseID);

                // Refresh the warehouse table after adding
                fetchAndDisplayWarehouseData();
            }
        });
    }

    private void addWarehouseToDatabase(String address, String warehouseName, String warehouseID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Check if the warehouse with the same warehouseID already exists
            String checkQuery = String.format("SELECT * FROM Warehouse WHERE warehouseID = '%s'", warehouseID);
            ResultSet resultSet = statement.executeQuery(checkQuery);

            if (resultSet.next()) {
                // Warehouse with the same warehouseID already exists, show an alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Add Warehouse");
                alert.setHeaderText(null);
                alert.setContentText("Warehouse with the same ID already exists.");
                alert.showAndWait();
            } else {
                // Insert the new warehouse into the Warehouse table
                String insertQuery = "INSERT INTO Warehouse VALUES (?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, address);
                    preparedStatement.setString(2, warehouseName);
                    preparedStatement.setString(3, warehouseID);

                    preparedStatement.executeUpdate();
                }

                System.out.println("Warehouse added to the database.");

                // Alternatively, you can show a success message here
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteWarehouse() {
        // Get the selected warehouse from the table
        Warehouse selectedWarehouse = warehouseTable.getSelectionModel().getSelectedItem();

        if (selectedWarehouse == null) {
            // If no warehouse is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Warehouse Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a warehouse to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Warehouse");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete the selected warehouse?");

        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, proceed with deletion
                deleteWarehouseFromDatabase(selectedWarehouse);

                // Refresh the warehouse table after deletion
                fetchAndDisplayWarehouseData();
            }
        });
    }

    private void deleteWarehouseFromDatabase(Warehouse warehouse) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Delete the selected warehouse from the Warehouse table
            String deleteQuery = String.format("DELETE FROM Warehouse WHERE warehouseID = '%s'", warehouse.getWarehouseID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Warehouse deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateWarehouse() {
        // Get the selected warehouse from the table
        Warehouse selectedWarehouse = warehouseTable.getSelectionModel().getSelectedItem();

        if (selectedWarehouse == null) {
            // If no warehouse is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Warehouse Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a warehouse to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating warehouse information
        Alert updateWarehouseAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateWarehouseAlert.setTitle("Update Warehouse");
        updateWarehouseAlert.setHeaderText(null);
        updateWarehouseAlert.setContentText("Update the warehouse information:");

        // Create labels and text fields for warehouse information
        Label addressLabel = new Label("Address:");
        TextField addressTextField = new TextField(selectedWarehouse.getAddress());

        Label warehouseNameLabel = new Label("Warehouse Name:");
        TextField warehouseNameTextField = new TextField(selectedWarehouse.getWarehouseName());

        Label warehouseIDLabel = new Label("Warehouse ID:");
        TextField warehouseIDTextField = new TextField(selectedWarehouse.getWarehouseID());
        //warehouseIDTextField.setEditable(false);

        VBox updateWarehouseLayout = new VBox(10,
                addressLabel, addressTextField,
                warehouseNameLabel, warehouseNameTextField,
                warehouseIDLabel, warehouseIDTextField);

        updateWarehouseLayout.setAlignment(Pos.CENTER);

        updateWarehouseAlert.getDialogPane().setContent(updateWarehouseLayout);

        // Set the button types for the alert
        updateWarehouseAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateWarehouseAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the warehouse information
                updateWarehouseInDatabase(selectedWarehouse,
                        addressTextField.getText(),
                        warehouseNameTextField.getText(),
                        warehouseIDTextField.getText());

                // Refresh the warehouse table after updating
                fetchAndDisplayWarehouseData();
            }
        });
    }

    private void updateWarehouseInDatabase(Warehouse warehouse, String newAddress, String newWarehouseName,
                                           String newWarehouseID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected warehouse in the Warehouse table
            String updateQuery = "UPDATE Warehouse SET address = ?, warehouseName = ?, " +
                    "warehouseID = ? WHERE warehouseID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newAddress);
                preparedStatement.setString(2, newWarehouseName);
                preparedStatement.setString(3, newWarehouseID);
                preparedStatement.setString(4, warehouse.getWarehouseID());

                preparedStatement.executeUpdate();
            }

            System.out.println("Warehouse updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Warehouse handleSearchWarehouse() {
        // Create a dialog
        TextInputDialog searchWarehouseDialog = new TextInputDialog();
        searchWarehouseDialog.setTitle("Search Warehouse");
        searchWarehouseDialog.setHeaderText(null);
        searchWarehouseDialog.setContentText("Enter Warehouse ID:");

        // Show the dialog and wait for user input
        Optional<String> result = searchWarehouseDialog.showAndWait();
        if (result.isPresent()) {
            String warehouseID = result.get();

            // Search for the warehouse in the database
            return searchWarehouseInDatabase(warehouseID);
        }

        return null;
    }

    private Warehouse searchWarehouseInDatabase(String warehouseID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Search for the warehouse in the Warehouse table
            String searchQuery = String.format("SELECT * FROM Warehouse WHERE warehouseID = '%s'", warehouseID);
            ResultSet resultSet = statement.executeQuery(searchQuery);

            if (resultSet.next()) {
                // If the warehouse is found, create a Warehouse object and return it
                return new Warehouse(
                        resultSet.getString("address"),
                        resultSet.getString("warehouseName"),
                        resultSet.getString("warehouseID")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no warehouse is found, return null
        return null;
    }
}
