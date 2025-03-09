package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Employee> employees;
    private final List<Department> departments;

    // constructor
    public EmployeeManager() {
        employees = new ArrayList<>();
        departments = new ArrayList<>();
    }

    // employee management
    public void addEmployee(Employee employee) {
        employees.add(employee);
        String departmentName = employee.getDepartment();
        Department department = getDepartmentByName(departmentName);

        if (department == null) {
            department = new Department(departmentName);
            addDepartment(department);
        }
        department.addEmployee(employee);
    }

    public void deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);

        if (employee != null) {
            employees.remove(employee);
            Department department = getDepartmentByName(employee.getDepartment());

            if (department != null) {
                department.removeEmployee(id);
            }
        }
    }

    public Employee getEmployeeById(String id) { // finds and returns the employee with the given id, if not found returns null
        return employees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Employee> getAllEmployees() {
        return employees;
    }

    // department management
    public void addDepartment(Department department) {
        departments.add(department);
    }

    public Department getDepartmentByName(String name) { // finds and returns the department with the given name, if not found returns null
        return departments.stream()
                .filter(dept -> dept.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Department> getAllDepartments() {
        return departments;
    }

    // saving data
    public void saveData(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Data successfully saved to employees.txt");
        } catch (IOException e) {
            System.err.println("Failed to save data to employees.txt");
            throw e;
        }
    }

    // loading data
    public static EmployeeManager loadData(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            System.out.println("Data successfully loaded from employees.txt");
            return (EmployeeManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load data from employees.txt");
            throw e;
        }
    }
}