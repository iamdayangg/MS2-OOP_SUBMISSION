package com.mycompany.motorphapps.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.mycompany.motorphapps.model.Person;
import com.mycompany.motorphapps.service.AuthService;

public class Dashboard extends JFrame {

    private Person loggedInUser;
    private String employeeID;
    private String role;

    private JPanel contentPanel;

    private Color blue = new Color(24, 82, 157);
    private Color pink = new Color(232, 151, 176);
    private Color lightPink = new Color(247, 214, 224);

    public Dashboard(Person user) {
        this.loggedInUser = user;
        this.employeeID = user.getEmployeeId();
        this.role = user.getRole();

        setTitle("MotorPH Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildHeader();
        buildSidebar();

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        showHome();

        setVisible(true);
    }

    private void buildHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(
                        0, 0, blue,
                        getWidth(), 0, pink
                );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("MotorPH Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    private void buildSidebar() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        menuPanel.setBackground(lightPink);

        GradientButton homeBtn = new GradientButton("Home");
        GradientButton employeesBtn = new GradientButton("Employees");
        GradientButton accountsBtn = new GradientButton("Accounts");
        GradientButton attendanceBtn = new GradientButton("Attendance");
        GradientButton leaveBtn = new GradientButton("Leave");
        GradientButton payrollBtn = new GradientButton("Payroll");
        GradientButton payslipBtn = new GradientButton("My Payslip");
        GradientButton profileBtn = new GradientButton("My Profile");
        GradientButton logoutBtn = new GradientButton("Logout");

        AuthService authService = new AuthService();
        List<String> allowedMenus = authService.getAllowedMenus(role);

        menuPanel.add(homeBtn);

        if (allowedMenus.contains("employees")) menuPanel.add(employeesBtn);
        if (allowedMenus.contains("accounts")) menuPanel.add(accountsBtn);
        if (allowedMenus.contains("attendance")) menuPanel.add(attendanceBtn);
        if (allowedMenus.contains("leave")) menuPanel.add(leaveBtn);
        if (allowedMenus.contains("payroll")) menuPanel.add(payrollBtn);
        if (allowedMenus.contains("payslip")) menuPanel.add(payslipBtn);

        menuPanel.add(profileBtn);
        menuPanel.add(logoutBtn);

        add(menuPanel, BorderLayout.WEST);

        homeBtn.addActionListener(e -> showHome());

        employeesBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new AllEmployeesPanel(role), BorderLayout.CENTER);
            refresh();
        });

        accountsBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new AccountManagementPanel(), BorderLayout.CENTER);
            refresh();
        });

        attendanceBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new AttendancePanel(employeeID, role), BorderLayout.CENTER);
            refresh();
        });

        leaveBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new LeavePanel(role, employeeID), BorderLayout.CENTER);
            refresh();
        });

        payrollBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new PayrollPanel(employeeID, role), BorderLayout.CENTER);
            refresh();
        });

        payslipBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new PayslipPanel(employeeID, role), BorderLayout.CENTER);
            refresh();
        });

        profileBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new EmployeeInfoPanel(employeeID, role), BorderLayout.CENTER);
            refresh();
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
    }

    private void showHome() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(142, 180, 230),
                        getWidth(), getHeight(), new Color(240, 160, 190)
                );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        homePanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(560, 340));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/system.jpg"));
        Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome to MotorPH System");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(blue);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Employee Payroll & Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("User: " + loggedInUser.getDisplayName(true));
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(blue);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel employeeIdLabel = new JLabel("Employee ID: " + employeeID);
        employeeIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        employeeIdLabel.setForeground(new Color(110, 110, 110));
        employeeIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Role: " + role.toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        roleLabel.setForeground(pink);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(welcome);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(employeeIdLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(roleLabel);
        card.add(Box.createVerticalGlue());

        homePanel.add(card);
        contentPanel.add(homePanel, BorderLayout.CENTER);

        refresh();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, blue,
                    getWidth(), getHeight(), pink
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            super.paintComponent(g2);
            g2.dispose();
        }
    }
}