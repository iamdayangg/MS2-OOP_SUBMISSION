package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    private static final String FILE_NAME = "src/main/resources/payroll_data.csv";
    private static final String DELIMITER  = ",";

    public void savePayslip(String employeeId, String period,
                            double gross, double tax, double sss,
                            double philHealth, double pagibig, double net) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.join(DELIMITER,
                    employeeId, period,
                    String.valueOf(gross), String.valueOf(tax), String.valueOf(sss),
                    String.valueOf(philHealth), String.valueOf(pagibig), String.valueOf(net)));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> readAllPayroll() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header row
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(DELIMITER);
                    for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                    list.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deletePayroll(String employeeId, String period) {
        List<String[]> all = readAllPayroll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String[] row : all) {
                if (!(row[0].equals(employeeId) && row[1].equals(period))) {
                    writer.write(String.join(DELIMITER, row));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
