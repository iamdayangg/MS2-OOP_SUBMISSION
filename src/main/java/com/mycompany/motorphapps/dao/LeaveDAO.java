package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    private static final String FILE_NAME = "src/main/resources/leave_data.csv";
    private static final String DELIMITER  = ",";

    public List<String[]> getAllLeaves() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(DELIMITER);
                if (parts.length == 6) list.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addLeave(String empId, String type, String startDate, String endDate) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String leaveId = "L" + System.currentTimeMillis();
            writer.write(String.join(DELIMITER, leaveId, empId, type, startDate, endDate, "Pending"));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(String leaveId, String status) {
        List<String[]> records = getAllLeaves();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String[] row : records) {
                if (row[0].equals(leaveId)) row[5] = status;
                writer.write(String.join(DELIMITER, row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLeave(String leaveId) {
        List<String[]> records = getAllLeaves();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String[] row : records) {
                if (!row[0].equals(leaveId)) {
                    writer.write(String.join(DELIMITER, row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
