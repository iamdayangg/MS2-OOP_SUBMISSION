package com.mycompany.motorphapps.dao;

import java.io.*;
import java.util.*;

public class CredentialDAO {

    private static final String FILE = "src/main/resources/credentials.csv";
    private static final String DELIMITER = ",";

    public boolean validateLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(DELIMITER);
                if (data.length >= 2 && data[0].trim().equals(username)
                        && data[1].trim().equals(password)) {
                    return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    public void register(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
            bw.write(username + DELIMITER + password);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
