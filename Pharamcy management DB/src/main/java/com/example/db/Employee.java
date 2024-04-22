package com.example.db;

import java.time.LocalDate;

// Employee.java
public class Employee {
    private String position;
    private double salary;
    private LocalDate hireDate;
    private String firstName;
    private String lastName;
    private String employeeID;
    private LocalDate empBirthDate;
    private String storeID;

    public Employee(String position, double salary, LocalDate hireDate, String firstName, String lastName,
                    String employeeID, LocalDate empBirthDate, String storeID) {
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeID = employeeID;
        this.empBirthDate = empBirthDate;
        this.storeID = storeID;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setEmpBirthDate(LocalDate empBirthDate) {
        this.empBirthDate = empBirthDate;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public LocalDate getEmpBirthDate() {
        return empBirthDate;
    }

    public String getStoreID() {
        return storeID;
    }
// Add getters and setters as needed

    @Override
    public String toString() {
        return "Employee{" +
                "position='" + position + '\'' +
                ", salary=" + salary +
                ", hireDate=" + hireDate +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", empBirthDate=" + empBirthDate +
                ", storeID='" + storeID + '\'' +
                '}';
    }
}
