package com.example;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient SimpleStringProperty id;
    private transient SimpleStringProperty fullName;
    private transient SimpleStringProperty department;
    private transient SimpleDoubleProperty hourlyRate;
    private transient SimpleBooleanProperty isClockedIn;
    private transient SimpleDoubleProperty taxRate;
    private transient SimpleDoubleProperty grossPay;
    private transient SimpleDoubleProperty taxDeducted;
    private transient SimpleDoubleProperty netPay;
    private transient SimpleDoubleProperty hoursWorked;

    private final List<ClockInClockOut> timeRecords;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String password;

    // constructor
    public Employee(String id, String fullName, String department, double hourlyRate, double grossPay) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.department = new SimpleStringProperty(department);
        this.hourlyRate = new SimpleDoubleProperty(hourlyRate);
        this.isClockedIn = new SimpleBooleanProperty(false); // default is clocked out
        this.grossPay = new SimpleDoubleProperty(grossPay);
        this.taxRate = new SimpleDoubleProperty();
        this.taxDeducted = new SimpleDoubleProperty();
        this.netPay = new SimpleDoubleProperty();
        this.hoursWorked = new SimpleDoubleProperty();
        this.timeRecords = new ArrayList<>();
    }

    // getters and setters
    public SimpleStringProperty idProperty() {
        return id;
    }

    public SimpleStringProperty fullNameProperty() {
        return fullName;
    }

    public SimpleStringProperty departmentProperty() {
        return department;
    }

    public String getId() {
        return id.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getDepartment() {
        return department.get();
    }

    public double getHourlyRate() {
        return hourlyRate.get();
    }

    public boolean isClockedIn() {
        return isClockedIn.get();
    }

    public double getGrossPay() {
        return grossPay.get();
    }

    public double getTaxDeducted() {
        return taxDeducted.get();
    }

    public double getNetPay() {
        return netPay.get();
    }

    public double getHoursWorked() {
        return hoursWorked.get();
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate.set(hourlyRate);
    }

    public void addTimeRecord(ClockInClockOut record) {
        this.timeRecords.add(record);
    }

    public void setPayrollData(double taxRate, double grossPay, double taxDeducted, double netPay) {
        this.taxRate.set(taxRate);
        this.grossPay.set(grossPay);
        this.taxDeducted.set(taxDeducted);
        this.netPay.set(netPay);
    }

    public void addToGrossPay(double amount) {
        this.grossPay.set(this.grossPay.get() + amount);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // clock in and clock out logic
    public void clockIn() {
        this.clockInTime = LocalDateTime.now();
        this.isClockedIn.set(true);
    }

    public void clockOut() {
        if (!this.isClockedIn.get()) {
            throw new IllegalStateException("Employee is not clocked in. Cannot clock out.");
        }
        if (this.clockInTime == null) {
            throw new IllegalStateException("Clock-in time is not set. Cannot clock out.");
        }

        this.clockOutTime = LocalDateTime.now();
        long secondsWorked = Duration.between(clockInTime, clockOutTime).getSeconds();
        this.hoursWorked.set(secondsWorked / 3600.0); // convert seconds to decimal hours

        double regularHours = calculateRegularHours();
        double overtimeHours = calculateOvertimeHours();

        double regularPay = regularHours * this.hourlyRate.get();
        double overtimePay = overtimeHours * this.hourlyRate.get() * 1.5;
        double totalPay = regularPay + overtimePay;

        this.grossPay.set(this.grossPay.get() + totalPay);

        this.clockInTime = null; // reset for next clock in
        this.isClockedIn.set(false);
    }

    // payroll calculation
    public double calculateRegularHours() {
        return Math.min(hoursWorked.get(), 8); // regular hours: 8
    }

    public double calculateOvertimeHours() {
        return Math.max(0, hoursWorked.get() - 8); // overtime hours: 8+
    }

    // writing (serialization) methods
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(id.get());
        out.writeUTF(fullName.get());
        out.writeUTF(department.get());
        out.writeDouble(hourlyRate.get());
        out.writeBoolean(isClockedIn.get());
        out.writeDouble(taxRate.get());
        out.writeDouble(grossPay.get());
        out.writeDouble(taxDeducted.get());
        out.writeDouble(netPay.get());
        out.writeDouble(hoursWorked.get());
    }

    // reading methods
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.id = new SimpleStringProperty(in.readUTF());
        this.fullName = new SimpleStringProperty(in.readUTF());
        this.department = new SimpleStringProperty(in.readUTF());
        this.hourlyRate = new SimpleDoubleProperty(in.readDouble());
        this.isClockedIn = new SimpleBooleanProperty(in.readBoolean());
        this.taxRate = new SimpleDoubleProperty(in.readDouble());
        this.grossPay = new SimpleDoubleProperty(in.readDouble());
        this.taxDeducted = new SimpleDoubleProperty(in.readDouble());
        this.netPay = new SimpleDoubleProperty(in.readDouble());
        this.hoursWorked = new SimpleDoubleProperty(in.readDouble());
    }
}