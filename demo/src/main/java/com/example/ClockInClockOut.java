package com.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClockInClockOut implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String employeeId;
    private final String action; // clocked in or clocked out
    private final LocalDateTime timestamp; // time of the action

    // constructor
    public ClockInClockOut(String employeeId, String action) {
        this.employeeId = employeeId;
        this.action = action;
        this.timestamp = LocalDateTime.now(); // automatically set the current timestamp
    }

    @Override
    public String toString() {
        return "Action: " + action + ", Employee ID: " + employeeId + ", Time: " + timestamp;
    }
}