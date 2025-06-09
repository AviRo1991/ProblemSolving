package com.employee;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAnalyser {

    public static final String FILENAME = "employees.csv";
    public static void main(String[] args) {
        try {

            Map<String, Employee> employeeMap;
            employeeMap = loadEmployees();
            buildHierarchy(employeeMap);

            System.out.println("Managers violating salary rules:");
            System.out.println(checkSalaryRules(employeeMap));

            System.out.println("\nEmployees with too long reporting lines:");
            System.out.println(checkReportingDepths(employeeMap));

        } catch (IOException e) {
            System.out.println("Failed to read the file: " + e.getMessage());
        }
    }

    private static Map<String, Employee> loadEmployees() throws IOException {

        Map<String, Employee> employeeMap = new LinkedHashMap<>();
        InputStream is = EmployeeAnalyser.class.getClassLoader().getResourceAsStream(FILENAME);
        if (is == null) throw new FileNotFoundException(FILENAME + " not found.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Employee emp = new Employee(
                        parts[0], parts[1], parts[2],
                        Double.parseDouble(parts[3]),
                        parts[4].isEmpty() ? null : parts[4]);
                employeeMap.put(emp.id, emp);
            }
        }

        return employeeMap;
    }

    private static void buildHierarchy(Map<String, Employee> employeeMap) {
        for (Employee emp : employeeMap.values()) {
            if (emp.managerId != null) {
                Employee mgr = employeeMap.get(emp.managerId);
                if (mgr != null) {
                    mgr.subordinates.add(emp);
                }
            }
        }
    }

    public static List<String> checkSalaryRules(Map<String, Employee> employeeMap) {

        List<String> salaryViolations = new ArrayList<>();

        for (Employee emp : employeeMap.values()) {
            if (emp.subordinates.isEmpty()) continue;

            double avgSubSalary = emp.subordinates.stream().mapToDouble(s -> s.salary).average().orElse(0);
            double minExpected = avgSubSalary * 1.2;
            double maxAllowed = avgSubSalary * 1.5;

            if (emp.salary < minExpected) {
                salaryViolations.add(emp.getFullName() + " earns too little. Short by: " + (minExpected - emp.salary));
            } else if (emp.salary > maxAllowed) {
                salaryViolations.add(emp.getFullName() + " earns too much. Over by: " + (emp.salary - maxAllowed));
            }
        }

        return salaryViolations;
    }

    public static List<String> checkReportingDepths(Map<String, Employee> employeeMap) {

        List<String> depthViolations = new ArrayList<>();

        for (Employee emp : employeeMap.values()) {
            int depth = getDepth(emp, employeeMap);
            if (depth > 4) {
                depthViolations.add(emp.getFullName() + " has " + depth + " managers above (limit: 4). Over by: " + (depth - 4));
            }
        }

        return depthViolations;
    }

    private static int getDepth(Employee emp, Map<String, Employee> employeeMap) {
        int count = 0;
        while (emp.managerId != null) {
            emp = employeeMap.get(emp.managerId);
            count++;
        }
        return count;
    }

}
