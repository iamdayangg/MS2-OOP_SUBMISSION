package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class EmployeeDAO {

    private static final String FILE_PATH = "src/main/resources/employee_data.csv";
    private static final String DELIMITER  = ",";

    /** Read all rows from the CSV file. */
    public List<String[]> getAllEmployees() {
        List<String[]> employees = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return employees;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER);
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                employees.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

   
    public void addEmployee(String[] data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(String.join(DELIMITER, data));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public void updateEmployee(String empId, String[] newData) {
        List<String[]> all = getAllEmployees();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (String[] row : all) {
                String line = row[0].equals(empId)
                        ? String.join(DELIMITER, newData)
                        : String.join(DELIMITER, row);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void deleteEmployee(String empId) {
        List<String[]> all = getAllEmployees();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (String[] row : all) {
                if (!row[0].equals(empId)) {
                    bw.write(String.join(DELIMITER, row));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
