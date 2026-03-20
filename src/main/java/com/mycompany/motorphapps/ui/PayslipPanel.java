package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.service.EmployeeService;
import com.mycompany.motorphapps.service.PayrollService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Payslip Viewer Panel
 * @author DAYANG GWAPA
 */
public class PayslipPanel extends JPanel {

    private JComboBox<String> employeeCombo;
    private JComboBox<PeriodItem> periodCombo;
    private JEditorPane area;

    private PayrollService payrollService;
    private EmployeeService employeeService;

    private Color blue = new Color(70,130,180);
    private Color pink = new Color(255,105,180);

    public PayslipPanel(String employeeID, String role) {
        payrollService = new PayrollService();
        employeeService = new EmployeeService();

        setLayout(new BorderLayout(20,20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        add(createHeader(), BorderLayout.NORTH);
        add(createViewer(), BorderLayout.CENTER);
        add(createControlCard(employeeID, role), BorderLayout.SOUTH);
    }

    public boolean printPayslip() {
        try {
            return area.print();
        } catch (Exception ex) {
            return false;
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0,blue,getWidth(),0,pink);
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        header.setLayout(new FlowLayout(FlowLayout.LEFT));
        header.setPreferredSize(new Dimension(100,60));

        JLabel title = new JLabel("Payslip Viewer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        header.add(Box.createHorizontalStrut(10));
        header.add(title);
        return header;
    }

    private JScrollPane createViewer() {
        area = new JEditorPane();
        area.setContentType("text/html");
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        return scroll;
    }

    private JPanel createControlCard(String employeeID, String role) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10,10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        int col = 0;

        periodCombo = new JComboBox<>();
        styleCombo(periodCombo);

        if(payrollService.canViewAllPayroll(role)) {
            employeeCombo = new JComboBox<>();
            styleCombo(employeeCombo);
            loadEmployees();

            gbc.gridx = col++;
            gbc.gridy = 0;
            top.add(new JLabel("Employee:"), gbc);

            gbc.gridx = col++;
            top.add(employeeCombo, gbc);

            if(employeeCombo.getItemCount() > 0){
                String firstEmpId = ((String) employeeCombo.getItemAt(0)).split(" - ")[0];
                loadPeriods(firstEmpId);
            }

            employeeCombo.addActionListener(e -> {
                String selected = (String) employeeCombo.getSelectedItem();
                if(selected != null){
                    String empId = selected.split(" - ")[0];
                    loadPeriods(empId);
                    clearViewer();
                }
            });
        } else {
            loadPeriods(employeeID);
        }

        gbc.gridx = col++;
        top.add(new JLabel("Period:"), gbc);

        gbc.gridx = col++;
        top.add(periodCombo, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,5));
        buttons.setBackground(Color.WHITE);

        GradientButton viewBtn  = new GradientButton("View Payslip");
        GradientButton printBtn = new GradientButton("Print Payslip");

        buttons.add(viewBtn);
        buttons.add(printBtn);

        card.add(top, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        viewBtn.addActionListener(e -> {
            String empId = employeeID;

            if(payrollService.canViewAllPayroll(role)){
                if(employeeCombo == null || employeeCombo.getSelectedItem() == null){
                    showModernMessage("No Employee","Please select an employee.");
                    return;
                }
                empId = ((String) employeeCombo.getSelectedItem()).split(" - ")[0];
            }

            PeriodItem selectedPeriod = (PeriodItem) periodCombo.getSelectedItem();
            if(selectedPeriod == null || selectedPeriod.placeholder){
                showModernMessage("Select Period","Please choose a payroll period.");
                return;
            }

            displayPayslip(empId, selectedPeriod.rawValue);
        });

        printBtn.addActionListener(e -> {
            boolean printed = printPayslip();
            showModernMessage("Print", printed ? "Payslip printed successfully." : "Printing cancelled.");
        });

        return card;
    }

    private void displayPayslip(String empId, String rawPeriod) {
        Payslip payslip = payrollService.buildPayslipDetail(empId, rawPeriod);

        if (payslip == null) {
            area.setText(
                "<html><body style='font-family:Segoe UI;text-align:center;padding-top:40px;'>"
                + "<h3>No Payslip Available</h3>"
                + "<p>This employee does not have a payroll record for the selected period.</p>"
                + "</body></html>"
            );
            return;
        }

        String html = "<html><body style='font-family:Segoe UI; padding:18px;'>"
                + "<div style='border:1px solid #d9d9d9; padding:20px; border-radius:8px;'>"
                + "<h2 style='margin-top:0; color:#2d5ea8;'>Employee Payslip</h2>"
                + "<hr/>"
                + "<table style='width:100%; border-collapse:collapse;'>"
                + "<tr><td style='padding:6px 0;'><b>Employee ID:</b></td><td>" + payslip.getEmployeeId() + "</td></tr>"
                + "<tr><td style='padding:6px 0;'><b>Name:</b></td><td>" + payslip.getEmployeeName() + "</td></tr>"
                + "<tr><td style='padding:6px 0;'><b>Period:</b></td><td>" + payslip.getDisplayPeriod() + "</td></tr>"
                + "</table>"
                + "<br/>"
                + "<table style='width:100%; border-collapse:collapse;' border='1' cellpadding='8'>"
                + "<tr style='background:#f3f7fc;'><th align='left' colspan='2'>Earnings</th></tr>"
                + "<tr><td>Basic Salary</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getBasicSalary()) + "</td></tr>"
                + "<tr><td>Overtime Pay</td><td align='right'><b style='color:#c85a00;'>&#8369;" + String.format("%.2f", payslip.getOvertimePay()) + "</b></td></tr>"
                + "<tr><td>Rice Subsidy</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getRiceAllowance()) + "</td></tr>"
                + "<tr><td>Phone Allowance</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getPhoneAllowance()) + "</td></tr>"
                + "<tr><td>Clothing Allowance</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getClothingAllowance()) + "</td></tr>"
                + "<tr><td><b>Total Allowances</b></td><td align='right'><b>&#8369;" + String.format("%.2f", payslip.getTotalAllowances()) + "</b></td></tr>"
                + "<tr style='background:#f3f7fc;'><td><b>Gross Salary</b></td><td align='right'><b>&#8369;" + String.format("%.2f", payslip.getGross()) + "</b></td></tr>"
                + "<tr style='background:#f3f7fc;'><th align='left' colspan='2'>Deductions</th></tr>"
                + "<tr><td>Tax</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getTax()) + "</td></tr>"
                + "<tr><td>SSS</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getSss()) + "</td></tr>"
                + "<tr><td>PhilHealth</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getPhilHealth()) + "</td></tr>"
                + "<tr><td>Pag-IBIG</td><td align='right'>&#8369;" + String.format("%.2f", payslip.getPagibig()) + "</td></tr>"
                + "<tr style='background:#fff4f8; font-weight:bold;'><td>Net Pay</td><td align='right'>&#8369;"
                + String.format("%.2f", payslip.getNet()) + "</td></tr>"
                + "</table>"
                + "</div></body></html>";

        area.setText(html);
        area.setCaretPosition(0);
    }

    private void clearViewer() {
        area.setText(
            "<html><body style='font-family:Segoe UI;text-align:center;padding-top:40px;color:#666;'>"
            + "<h3>Select a Payroll Period</h3>"
            + "<p>Choose an employee and period, then click View Payslip.</p>"
            + "</body></html>"
        );
    }

    private void styleCombo(JComboBox<?> combo){
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(230, 32));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
    }

    private void loadEmployees(){
        if(employeeCombo == null) return;

        employeeCombo.removeAllItems();
        List<String[]> list = employeeService.getAllEmployees();

        for(String[] emp : list){
            if(emp.length >= 3){
                employeeCombo.addItem(emp[0] + " - " + emp[2] + " " + emp[1]);
            }
        }
    }

    private void loadPeriods(String empId){
        periodCombo.removeAllItems();
        periodCombo.addItem(PeriodItem.placeholder());

        List<String[]> payrollList = payrollService.getAllPayroll();
        Set<String> periodValues = new LinkedHashSet<>();

        for(String[] row : payrollList){
            if(row.length < 2) continue;
            if(empId != null && row[0].equals(empId)){
                periodValues.add(row[1]);
            }
        }

        List<String> sortedPeriods = new ArrayList<>(periodValues);
        sortedPeriods.sort(Comparator.comparing(this::periodSortKey).reversed());

        for(String raw : sortedPeriods){
            periodCombo.addItem(new PeriodItem(raw, formatPeriodForDisplay(raw), false));
        }

        periodCombo.setSelectedIndex(0);
    }

    private LocalDate periodSortKey(String raw){
        String cleaned = raw == null ? "" : raw.trim();
        if(cleaned.contains(" to ")){
            try{
                String start = cleaned.split("\\s+to\\s+")[0].trim();
                return LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }catch(Exception ignored){
            }
        }
        return LocalDate.MIN;
    }

    private String formatPeriodForDisplay(String rawPeriod){
        if(rawPeriod == null || rawPeriod.trim().isEmpty()){
            return "";
        }

        String cleaned = rawPeriod.trim();

        if(cleaned.contains(" to ")){
            try{
                String[] parts = cleaned.split("\\s+to\\s+");
                LocalDate start = LocalDate.parse(parts[0].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate end = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                DateTimeFormatter nice = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                return start.format(nice) + " - " + end.format(nice);
            }catch(DateTimeParseException ignored){
                return cleaned;
            }
        }

        return cleaned;
    }

    private void showModernMessage(String title, String message){
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),true);
        dialog.setSize(360,180);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title, JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);

        JLabel msg = new JLabel("<html><center>"+message+"</center></html>", JLabel.CENTER);
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

    class GradientButton extends JButton {
        public GradientButton(String text){
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10,22,10,22));
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0,0,blue,getWidth(),getHeight(),pink);
            g2.setPaint(gp);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0,0,blue,getWidth(),getHeight(),pink);
            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }

    private static class PeriodItem {
        private final String rawValue;
        private final String displayValue;
        private final boolean placeholder;

        private PeriodItem(String rawValue, String displayValue, boolean placeholder) {
            this.rawValue = rawValue;
            this.displayValue = displayValue;
            this.placeholder = placeholder;
        }

        static PeriodItem placeholder() {
            return new PeriodItem("", "Select Period", true);
        }

        @Override
        public String toString() {
            return displayValue;
        }
    }
}