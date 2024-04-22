package com.example.db;
import java.util.Objects;

public class Warehouse {
    private String address;
    private String warehouseName;
    private String warehouseID;

    public Warehouse(String address, String warehouseName, String warehouseID) {
        this.address = address;
        this.warehouseName = warehouseName;
        this.warehouseID = warehouseID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "Warehouse{" +
                "address='" + address + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", warehouseID='" + warehouseID + '\'' +
                '}';
    }
}

