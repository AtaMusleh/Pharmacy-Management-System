package com.example.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class ViewSupplierFunctionality extends VBox {

    private TableView<Supplier> suppliersTable;

    public ViewSupplierFunctionality(Menu menu) {
        initializeUI(menu);
    }

    private void initializeUI(Menu menu) {

        Label suppliersLabel = new Label("Suppliers Functionality");
        suppliersTable = createSuppliersTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Supplier");
        addButton.setOnAction(e -> handleAddSupplier());
        Button updateButton = new Button("Update Supplier");
        updateButton.setOnAction(e -> handleUpdateSupplier());
        Button deleteButton = new Button("Delete Supplier");
        deleteButton.setOnAction(e -> handleDeleteSupplier());
        Button searchButton = new Button("Search Supplier");
        searchButton.setOnAction(e -> {
            Supplier foundSupplier = handleSearchSupplier();

            if (foundSupplier != null) {
                ObservableList<Supplier> foundSupplierList = FXCollections.observableArrayList();
                foundSupplierList.add(foundSupplier);
                suppliersTable.setItems(foundSupplierList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(suppliersLabel, suppliersTable, buttonsLayout);
        fetchAndDisplaySuppliersData();
        // Set action for the "View Suppliers" button
    }

    private TableView<Supplier> createSuppliersTableView() {
        TableView<Supplier> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Supplier, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<Supplier, String> lastNameCol = new TableColumn<>("Last Name");
        TableColumn<Supplier, String> addressCol = new TableColumn<>("Address");
        TableColumn<Supplier, String> phoneNumberCol = new TableColumn<>("Phone Number");
        TableColumn<Supplier, String> supplierIDCol = new TableColumn<>("Supplier ID");

        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        supplierIDCol.setCellValueFactory(new PropertyValueFactory<>("supplierID"));

        tableView.getColumns().addAll(firstNameCol, lastNameCol, addressCol, phoneNumberCol, supplierIDCol);

        return tableView;
    }

    private void fetchAndDisplaySuppliersData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Supplier");

            ObservableList<Supplier> suppliersData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Supplier supplier = new Supplier(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("supplierID")
                );

                suppliersData.add(supplier);
            }

            suppliersTable.setItems(suppliersData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddSupplier() {
        // Create a dialog
        Alert addSupplierDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addSupplierDialog.setTitle("Add Supplier");
        addSupplierDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField addressField = new TextField();
        TextField phoneNumberField = new TextField();
        TextField supplierIDField = new TextField();

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneNumberField, 1, 3);
        grid.add(new Label("Supplier ID:"), 0, 4);
        grid.add(supplierIDField, 1, 4);

        addSupplierDialog.getDialogPane().setContent(grid);

        // Set the button types
        addSupplierDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addSupplierDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String address = addressField.getText();
                String phoneNumber = phoneNumberField.getText();
                String supplierID = supplierIDField.getText();

                // Implement logic to add the supplier with the provided information to the database
                addSupplierToDatabase(firstName, lastName, address, phoneNumber, supplierID);

                // Refresh the suppliers table after adding
                fetchAndDisplaySuppliersData();
            }
        });
    }

    private void addSupplierToDatabase(String firstName, String lastName, String address,
                                       String phoneNumber, String supplierID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Check if the supplier with the same supplierID already exists
            String checkQuery = String.format("SELECT * FROM Supplier WHERE supplierID = '%s'", supplierID);
            ResultSet resultSet = statement.executeQuery(checkQuery);

            if (resultSet.next()) {
                // Supplier with the same supplierID already exists, show an alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Add Supplier");
                alert.setHeaderText(null);
                alert.setContentText("Supplier with the same ID already exists.");
                alert.showAndWait();
            } else {
                // Insert the new supplier into the Supplier table
                String insertQuery = "INSERT INTO Supplier VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, address);
                    preparedStatement.setString(4, phoneNumber);
                    preparedStatement.setString(5, supplierID);

                    preparedStatement.executeUpdate();
                }

                System.out.println("Supplier added to the database.");

                // Alternatively, you can show a success message here
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteSupplier() {
        // Get the selected supplier from the table
        Supplier selectedSupplier = suppliersTable.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            // If no supplier is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Supplier Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a supplier to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Supplier");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete the selected supplier?");

        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, proceed with deletion
                deleteSupplierFromDatabase(selectedSupplier);

                // Refresh the suppliers table after deletion
                fetchAndDisplaySuppliersData();
            }
        });
    }

    private void deleteSupplierFromDatabase(Supplier supplier) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Delete the selected supplier from the Supplier table
            String deleteQuery = String.format("DELETE FROM Supplier WHERE supplierID = '%s'", supplier.getSupplierID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Supplier deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateSupplier() {
        // Get the selected supplier from the table
        Supplier selectedSupplier = suppliersTable.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            // If no supplier is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Supplier Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a supplier to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating supplier information
        Alert updateSupplierAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateSupplierAlert.setTitle("Update Supplier");
        updateSupplierAlert.setHeaderText(null);
        updateSupplierAlert.setContentText("Update the supplier information:");

        // Create labels and text fields for supplier information
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameTextField = new TextField(selectedSupplier.getFirstName());

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameTextField = new TextField(selectedSupplier.getLastName());

        Label addressLabel = new Label("Address:");
        TextField addressTextField = new TextField(selectedSupplier.getAddress());

        Label phoneNumberLabel = new Label("Phone Number:");
        TextField phoneNumberTextField = new TextField(selectedSupplier.getPhoneNumber());

        Label supplierIDLabel = new Label("Supplier ID:");
        TextField supplierIDTextField = new TextField(selectedSupplier.getSupplierID());
        //supplierIDTextField.setEditable(false);

        VBox updateSupplierLayout = new VBox(10,
                firstNameLabel, firstNameTextField,
                lastNameLabel, lastNameTextField,
                addressLabel, addressTextField,
                phoneNumberLabel, phoneNumberTextField,
                supplierIDLabel, supplierIDTextField);

        updateSupplierLayout.setAlignment(Pos.CENTER);

        updateSupplierAlert.getDialogPane().setContent(updateSupplierLayout);

        // Set the button types for the alert
        updateSupplierAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateSupplierAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the supplier information
                updateSupplierInDatabase(selectedSupplier,
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        addressTextField.getText(),
                        phoneNumberTextField.getText(),
                        supplierIDTextField.getText());

                // Refresh the suppliers table after updating
                fetchAndDisplaySuppliersData();
            }
        });
    }

    private void updateSupplierInDatabase(Supplier supplier, String newFirstName, String newLastName,
                                          String newAddress, String newPhoneNumber, String newSupplierID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected supplier in the Supplier table
            String updateQuery = "UPDATE Supplier SET firstName = ?, lastName = ?, " +
                    "address = ?, phoneNumber = ?, supplierID = ? WHERE supplierID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newFirstName);
                preparedStatement.setString(2, newLastName);
                preparedStatement.setString(3, newAddress);
                preparedStatement.setString(4, newPhoneNumber);
                preparedStatement.setString(5, newSupplierID);
                preparedStatement.setString(6, supplier.getSupplierID());

                preparedStatement.executeUpdate();
            }

            System.out.println("Supplier updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Supplier handleSearchSupplier() {
        // Create a dialog
        TextInputDialog searchSupplierDialog = new TextInputDialog();
        searchSupplierDialog.setTitle("Search Supplier");
        searchSupplierDialog.setHeaderText(null);
        searchSupplierDialog.setContentText("Enter Supplier ID:");

        // Show the dialog and wait for user input
        Optional<String> result = searchSupplierDialog.showAndWait();
        if (result.isPresent()) {
            String supplierID = result.get();

            // Search for the supplier in the database
            return searchSupplierInDatabase(supplierID);
        }

        return null;
    }

    private Supplier searchSupplierInDatabase(String supplierID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Search for the supplier in the Supplier table
            String searchQuery = String.format("SELECT * FROM Supplier WHERE supplierID = '%s'", supplierID);
            ResultSet resultSet = statement.executeQuery(searchQuery);

            if (resultSet.next()) {
                // If the supplier is found, create a Supplier object and return it
                return new Supplier(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("address"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("supplierID")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no supplier is found, return null
        return null;
    }
}
