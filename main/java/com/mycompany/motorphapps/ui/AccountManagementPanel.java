/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.AccountService;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author DAYANG GWAPA
 */
public class AccountManagementPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private AccountService accountService;

    public AccountManagementPanel() {

        accountService = new AccountService();

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        add(mainPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("  Account Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Employee ID", "Role"}, 0) {

            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(255, 120, 200));
        table.setSelectionForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {

                    if (row % 2 == 0)
                        c.setBackground(new Color(230, 240, 255));
                    else
                        c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(80, 130, 255));
        header.setForeground(Color.WHITE);

        loadAccounts();

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);


        GradientButton addBtn    = new GradientButton("Add Account");
        GradientButton editBtn   = new GradientButton("Edit Account");
        GradientButton deleteBtn = new GradientButton("Delete Account");

        addBtn.addActionListener(e    -> openAccountDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filter(); }
            public void removeUpdate(DocumentEvent e)  { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }


    private void loadAccounts() {

        model.setRowCount(0);

        List<String[]> accounts = accountService.getAllAccounts();

        for (String[] row : accounts) {

            if (row.length >= 3) {
                model.addRow(new Object[]{row[0], row[2]});
            }
        }
    }


    private void filter() {

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);

        String text = searchField.getText();

        sorter.setRowFilter(
                text.isEmpty() ? null :
                        RowFilter.regexFilter("(?i)" + text));

        table.setRowSorter(sorter);
    }


    private void editSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            showModernMessage("Message", "Select an account first.");
            return;
        }

        row = table.convertRowIndexToModel(row);

        String empId = model.getValueAt(row, 0).toString();

        List<String[]> accounts = accountService.getAllAccounts();

        for (String[] acc : accounts) {

            if (acc[0].equals(empId)) {
                openAccountDialog(acc);
                return;
            }
        }
    }


    private void deleteSelected() {

        int row = table.getSelectedRow();

        if (row < 0) {
            showModernMessage("Message", "Select an account first.");
            return;
        }

        row = table.convertRowIndexToModel(row);

        String empId = model.getValueAt(row, 0).toString();

        accountService.deleteAccount(empId);

        loadAccounts();

        showModernMessage("Success", "Account deleted.");
    }


    private void openAccountDialog(String[] data) {

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), true);

        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel header = new JLabel(
                data == null ? "Add New Account" : "Edit Account",
                JLabel.CENTER);

        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(empIdLabel, gbc);

        gbc.gridx = 1;
        JTextField empIdField = new JTextField(15);
        empIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (data != null) {
            empIdField.setText(data[0]);
            empIdField.setEditable(false);
            empIdField.setBackground(new Color(230, 230, 230));
        }

        formPanel.add(empIdField, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JLabel empIdError = new JLabel(" ");
        empIdError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        empIdError.setForeground(Color.RED);
        formPanel.add(empIdError, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (data != null) passField.setText(data[1]);

        formPanel.add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JLabel passError = new JLabel(" ");
        passError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passError.setForeground(Color.RED);
        formPanel.add(passError, gbc);


        gbc.gridx = 0; gbc.gridy = 4;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        String[] roles = {"employee", "admin", "HR", "FINANCE", "IT"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        if (data != null) roleBox.setSelectedItem(data[2]);

        formPanel.add(roleBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        GradientButton saveBtn   = new GradientButton("Save");
        GradientButton cancelBtn = new GradientButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        saveBtn.addActionListener(e -> {

            String empId = empIdField.getText().trim();
            String pass  = new String(passField.getPassword()).trim();
            String role  = roleBox.getSelectedItem().toString();

            empIdError.setText(" ");
            passError.setText(" ");


            if (empId.isEmpty()) {
                empIdError.setText("Employee ID is required");
                return;
            }


            try {

                if (data == null) {
                    accountService.addAccount(empId, pass, role);
                } else {
                    accountService.updateAccount(empId, pass, role);
                }

                loadAccounts();
                dialog.dispose();
                showModernMessage("Success", "Account saved successfully.");

            } catch (IllegalArgumentException ex) {
                showModernMessage("Error", ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }


    private void showModernMessage(String title, String message) {

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), true);

        dialog.setSize(360, 200);
        dialog.setLocationRelativeTo(this);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title, JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);

        JLabel msg = new JLabel(message, JLabel.CENTER);
        msg.setForeground(Color.WHITE);

        GradientButton ok = new GradientButton("OK");
        ok.addActionListener(e -> dialog.dispose());

        JPanel btn = new JPanel();
        btn.setOpaque(false);
        btn.add(ok);

        panel.add(header, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


    class GradientPanel extends JPanel {

        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(70, 120, 255),
                    getWidth(), getHeight(),
                    new Color(255, 120, 200));

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }


    class GradientButton extends JButton {

        public GradientButton(String text) {

            super(text);

            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(70, 120, 255),
                    0, getHeight(),
                    new Color(255, 120, 200));

            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}








