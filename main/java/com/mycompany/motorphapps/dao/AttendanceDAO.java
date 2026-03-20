package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    private static final String FILE_NAME = "src/main/resources/attendance_log.csv";
    private static final String DELIMITER  = ",";

    public List<String[]> getAllAttendance() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(DELIMITER);
                String[] padded = new String[5];
                for (int i = 0; i < 5; i++) padded[i] = i < fields.length ? fields[i] : "";
                list.add(padded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveAll(List<String[]> logs) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String[] row : logs) {
                if (row.length < 5) continue;
                bw.write(String.join(DELIMITER, row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
