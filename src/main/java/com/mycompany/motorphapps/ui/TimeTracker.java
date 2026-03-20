package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.AttendanceService;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
/**
 *
 * @author DAYANG GWAPA
 */
public class TimeTracker extends JPanel {

    private JLabel statusLabel;
    private JCheckBox timeFormatToggle;
    private LocalTime timeIn, timeOut;
    private final String employeeID;
    private final String employeeName;
    private BufferedImage backgroundImage;

    private AttendanceService attendanceService;

    public TimeTracker(String employeeID) {
        this.employeeID = employeeID;
        this.attendanceService = new AttendanceService();
        this.employeeName = attendanceService.getEmployeeName(employeeID);

        try {
            backgroundImage = ImageIO.read(
                    getClass().getResource("/images/MotorPH Timetracker Design.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Time Tracker - " + employeeName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.setOpaque(false);

        JButton timeInButton = new JButton("Time In");
        JButton timeOutButton = new JButton("Time Out");

        timeFormatToggle = new JCheckBox("Use 12-hour format");
        statusLabel = new JLabel("Status: Not logged in");
        statusLabel.setForeground(Color.WHITE);

        timeInButton.addActionListener(e -> handleTimeIn());
        timeOutButton.addActionListener(e -> handleTimeOut());

        centerPanel.add(timeInButton);
        centerPanel.add(timeOutButton);
        centerPanel.add(timeFormatToggle);
        centerPanel.add(statusLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void handleTimeIn() {
        timeIn = LocalTime.now().withSecond(0).withNano(0);
        String timeStr = formatTime(timeIn);

        attendanceService.recordTimeIn(employeeID, timeIn);

        JOptionPane.showMessageDialog(this, "Time In: " + timeStr);
        statusLabel.setText("Time In at " + timeStr);
    }

    private void handleTimeOut() {
        if (timeIn == null) {
            JOptionPane.showMessageDialog(this, "Please Time In first.");
            return;
        }

        timeOut = LocalTime.now().withSecond(0).withNano(0);
        String timeStr = formatTime(timeOut);

        attendanceService.recordTimeOut(employeeID, timeOut);

        JOptionPane.showMessageDialog(this, "Time Out: " + timeStr);
        statusLabel.setText(statusLabel.getText() + " | Time Out at " + timeStr);
    }

    private String formatTime(LocalTime time) {
        return timeFormatToggle.isSelected()
                ? time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                : time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}