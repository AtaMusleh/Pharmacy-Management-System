package com.example.db;
public class Store {
    private String storeName;
    private String storeID;
    private String address;
    private String phoneNumber;

    public Store(String storeName, String storeID, String address, String phoneNumber) {
        this.storeName = storeName;
        this.storeID = storeID;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Add getters and setters for each property

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

