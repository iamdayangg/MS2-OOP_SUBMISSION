package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.model.Admin;
import com.mycompany.motorphapps.model.Employee;
import com.mycompany.motorphapps.model.Finance;
import com.mycompany.motorphapps.model.HR;
import com.mycompany.motorphapps.model.ITStaff;
import com.mycompany.motorphapps.model.Person;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Authenticator {

    private static final String FILE_NAME = "src/main/resources/credentials.csv";
    private static final String DELIMITER = ",";

    public Person login(String id, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(DELIMITER);
                if (data.length < 3) {
                    continue;
                }

                String empId = data[0].trim();
                String pass = data[1].trim();
                String role = data[2].trim();

                if (empId.equals(id) && pass.equals(password)) {
                    String[] nameParts = getEmployeeName(empId);
                    String firstName = nameParts[0];
                    String lastName = nameParts[1];

                    switch (role.toUpperCase()) {
                        case "ADMIN":
                            return new Admin(empId, firstName, lastName);

                        case "HR":
                            return new HR(empId, firstName, lastName);

                        case "FINANCE":
                            return new Finance(empId, firstName, lastName);

                        case "IT":
                            return new ITStaff(empId, firstName, lastName);

                        case "USER":
                        case "EMPLOYEE":
                        default:
                            return new Employee(empId, firstName, lastName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String[] getEmployeeName(String empId) {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        List<String[]> employees = employeeDAO.getAllEmployees();

        for (String[] row : employees) {
            if (row.length < 3) {
                continue;
            }

            String rowEmpId = row[0].trim();

            if (rowEmpId.equalsIgnoreCase(empId)) {
                String lastName = row[1].trim();
                String firstName = row[2].trim();

                if (lastName.equalsIgnoreCase("last name") || firstName.equalsIgnoreCase("first name")) {
                    continue;
                }

                return new String[]{firstName, lastName};
            }
        }

        return new String[]{"", ""};
    }
}