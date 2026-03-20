package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.EmployeeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
/**
 *
 * @author DAYANG GWAPA
 */
public class EmployeeInfoPanel extends JPanel {

    private BufferedImage profileImage;
    private JLabel nameLabel;
    private JTextArea infoArea;
    private EmployeeService employeeService;

    public EmployeeInfoPanel(String employeeID, String role) {
        employeeService = new EmployeeService();

        String gender = getEmployeeGender(employeeID);
        if ("admin".equalsIgnoreCase(role)) {
            profileImage = loadImage("/images/adminProfile.jpg");
        } else if ("Male".equalsIgnoreCase(gender)) {
            profileImage = loadImage("/images/user_male.png");
        } else {
            profileImage = loadImage("/images/user.png");
        }

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createProfileCard(employeeID), BorderLayout.CENTER);
    }

    private JPanel createProfileCard(String employeeID) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 120, 255),
                        getWidth(), getHeight(), new Color(255, 105, 180)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        card.add(createProfileHeader(employeeID), BorderLayout.NORTH);
        card.add(createInfoPanel(employeeID), BorderLayout.CENTER);

        return card;
    }

    private JPanel createProfileHeader(String employeeID) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        header.setOpaque(false);

        JLabel picLabel = new JLabel();
        if (profileImage != null) {
            Image img = profileImage.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            picLabel.setIcon(new ImageIcon(img));
        }

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);

        header.add(picLabel);
        header.add(nameLabel);

        loadEmployeeName(employeeID);

        return header;
    }

    private JScrollPane createInfoPanel(String employeeID) {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoArea.setForeground(Color.WHITE);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBorder(new EmptyBorder(15, 5, 5, 5));

        loadEmployeeInfo(employeeID);

        JScrollPane scroll = new JScrollPane(infoArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        return scroll;
    }

    private void loadEmployeeName(String employeeID) {
        List<String[]> employees = employeeService.getAllEmployees();
        for (String[] emp : employees) {
            if (emp.length > 2 && emp[0].trim().equals(employeeID.trim())) {
                String name = emp[2].trim() + " " + emp[1].trim(); 
                nameLabel.setText(name);
                return;
            }
        }
        nameLabel.setText("Name not found");
    }

    private void loadEmployeeInfo(String employeeID) {
        List<String[]> employees = employeeService.getAllEmployees();
        for (String[] emp : employees) {
            if (emp.length > 0 && emp[0].trim().equals(employeeID.trim())) {

                String[] data = new String[20]; 
                for (int i = 0; i < data.length; i++) {
                    data[i] = i < emp.length ? emp[i].trim() : "";
                }

                infoArea.setText(
                        "Employee ID: " + data[0] + "\n" +
                        "Last Name: " + data[1] + "\n" +
                        "First Name: " + data[2] + "\n" +
                        "Birthday: " + data[3] + "\n" +
                        "Address: " + data[4] + "\n" +
                        "Phone: " + data[5] + "\n\n" +

                        "SSS: " + data[6] + "\n" +
                        "PhilHealth: " + data[7] + "\n" +
                        "TIN: " + data[8] + "\n" +
                        "Pag-IBIG: " + data[9] + "\n\n" +

                        "Status: " + data[10] + "\n" +
                        "Position: " + data[11] + "\n" +
                        "Supervisor: " + data[12] + "\n\n" +

                        "Basic Salary: " + data[13] + "\n" +
                        "Rice Subsidy: " + data[14] + "\n" +
                        "Phone Allowance: " + data[15] + "\n" +
                        "Clothing Allowance: " + data[16] + "\n\n" +

                        "Gross Salary: " + data[17] + "\n" +
                        "Hourly Rate: " + data[18] + "\n" +
                        "Gender: " + (data[19].isEmpty() ? (data[0].equals("10000") ? "Female" : "Male") : data[19])
                );
                return;
            }
        }
        infoArea.setText("Employee profile not found.");
    }

    private String getEmployeeGender(String employeeID) {
        List<String[]> employees = employeeService.getAllEmployees();
        for (String[] emp : employees) {
            if (emp.length > 0 && emp[0].trim().equals(employeeID.trim())) {
                if (emp.length > 19 && !emp[19].isEmpty()) return emp[19];
                return emp[0].equals("10000") ? "Female" : "Male"; 
            }
        }
        return "Female";
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResource(path));
        } catch (Exception e) {
            System.out.println("Image not found: " + path);
            return null;
        }
    }
}