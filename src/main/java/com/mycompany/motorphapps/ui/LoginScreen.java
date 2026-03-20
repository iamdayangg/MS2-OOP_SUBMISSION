package com.mycompany.motorphapps.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.mycompany.motorphapps.service.AuthService;
import com.mycompany.motorphapps.dao.Authenticator;
import com.mycompany.motorphapps.model.Person;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private BufferedImage bgImage;
    private JPanel backgroundPanel;

    private final Color blue = new Color(70, 130, 180);
    private final Color pink = new Color(255, 105, 180);

    public LoginScreen() {
        setTitle("MotorPH Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);

        loadBackgroundImage();

        backgroundPanel = createBackgroundPanel();
        setContentPane(backgroundPanel);

        buildUI();

        setVisible(true);
    }

    private void loadBackgroundImage() {
        try {
            bgImage = ImageIO.read(
                getClass().getResource("/images/MotorPH Login Design.jpg")
            );
        } catch (Exception e) {
            System.out.println("Background image not found.");
        }
    }

    private JPanel createBackgroundPanel() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
    }

    private void buildUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 280));
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MotorPH Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 60, 120));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(52, 120, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(200, 35));
        loginButton.addActionListener(e -> authenticate());

        formPanel.add(loginButton, gbc);

        backgroundPanel.add(formPanel);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showModernMessage("Login Error", "Please enter both username and password.");
            return;
        }

        AuthService authService = new AuthService();
Person user = authService.login(username, password);

        if (user != null) {
            dispose();
            new Dashboard(user);
        } else {
            showModernMessage("Login Failed", "Invalid username or password.");
        }
    }

    private void showModernMessage(String title, String message) {
        JDialog dialog = new JDialog(this, true);
        dialog.setTitle(title);
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title, JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JLabel msg = new JLabel("<html><center>" + message + "</center></html>", JLabel.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        GradientButton ok = new GradientButton("OK");
        ok.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(ok);

        panel.add(header, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint gp = new GradientPaint(
                0, 0, blue,
                getWidth(), getHeight(), pink
            );

            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            GradientPaint gp = new GradientPaint(
                0, 0, blue,
                getWidth(), getHeight(), pink
            );

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}