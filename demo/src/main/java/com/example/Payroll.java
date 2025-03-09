package com.example;

import java.io.Serializable;

public class Payroll implements Serializable{

    private final String employeeId;
    private final double grossPay;
    private final double taxDeducted;
    private final double netPay;

    // constructor
    public Payroll(String employeeId, double grossPay, double taxDeducted, double netPay) {
        this.employeeId = employeeId;
        this.grossPay = grossPay;
        this.taxDeducted = taxDeducted;
        this.netPay = netPay;
    }

    // getters
    public double getGrossPay() {
        return grossPay;
    }

    public double getTaxDeducted() {
        return taxDeducted;
    }

    public double getNetPay() {
        return netPay;
    }

    @Override
    public String toString() {
        return "Payroll: " +
                "Employee ID='" + employeeId + '\'' +
                ", Gross Pay=" + grossPay +
                ", Tax Deducted=" + taxDeducted +
                ", Net Pay=" + netPay;
    }
}