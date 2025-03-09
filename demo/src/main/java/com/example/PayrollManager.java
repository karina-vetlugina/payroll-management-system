package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PayrollManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Payroll> payrolls;

    // constructor
    public PayrollManager() {
        payrolls = new ArrayList<>();
    }

    public Payroll processPayroll(Employee employee, double taxRate) { // calculate and store payroll for an employee
        double grossPay = employee.getGrossPay();
        double taxDeducted = grossPay * taxRate;
        double netPay = grossPay - taxDeducted;

        Payroll payroll = new Payroll(employee.getId(), grossPay, taxDeducted, netPay);
        payrolls.add(payroll);
        employee.setPayrollData(taxRate, grossPay, taxDeducted, netPay);

        return payroll;
    }

    public void clearPayrolls() {
        payrolls.clear();
    }

    // saving data
    public void saveData(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Data successfully saved to payroll.txt");
        } catch (IOException e) {
            System.err.println("Failed to save data to payroll.txt");
            throw e;
        }
    }

    // loading data
    public static PayrollManager loadData(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            System.out.println("Data successfully loaded from payroll.txt");
            return (PayrollManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load data from payroll.txt");
            throw e;
        }
    }
}