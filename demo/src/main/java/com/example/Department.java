package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Department implements Serializable {

    private final String name;
    private final List<Employee> employees;

    // constructor
    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    // getters
    public String getName() {
        return name;
    }

    // employee management
    public void addEmployee(Employee employee) { // add employee to the department
        employees.add(employee);
    }

    public void removeEmployee(String employeeId) { // remove employee with the given id
        employees.removeIf(emp -> emp.getId().equals(employeeId));
    }

    // payroll calculation
    public double calculateTotalPayroll() {
        return employees.stream()
                .mapToDouble(Employee::getGrossPay)
                .sum(); // sum the gross pay of all employees
    }
}