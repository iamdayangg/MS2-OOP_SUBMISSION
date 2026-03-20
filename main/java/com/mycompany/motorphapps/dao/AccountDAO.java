package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    private static final String FILE_NAME = "src/main/resources/credentials.csv";
    private static final String DELIMITER  = ",";

    public List<String[]> getAllAccounts() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 3) {
                    for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                    list.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addAccount(String empId, String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(empId + DELIMITER + password + DELIMITER + role);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(String empId, String newPassword, String newRole) {
        List<String[]> all = getAllAccounts();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (String[] row : all) {
                String line = row[0].equals(empId)
                        ? empId + DELIMITER + newPassword + DELIMITER + newRole
                        : String.join(DELIMITER, row);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(String empId) {
        List<String[]> all = getAllAccounts();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (String[] row : all) {
                if (!row[0].equals(empId)) {
                    writer.write(String.join(DELIMITER, row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
