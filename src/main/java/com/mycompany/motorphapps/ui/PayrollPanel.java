package com.mycompany.motorphapps.ui;
import com.mycompany.motorphapps.payroll.OvertimePayrollCalculator;
import com.mycompany.motorphapps.payroll.PayrollCalculator;
import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.service.EmployeeService;
import com.mycompany.motorphapps.service.PayrollService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DAYANG GWAPA
 */
public class PayrollPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private PayrollService payrollService;

    private Color blue = new Color(70,130,180);
    private Color pink = new Color(255,105,180);

    public PayrollPanel(String employeeID, String role){

        payrollService = new PayrollService();

        setLayout(new BorderLayout(20,20));
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Employee ID","Employee Name","Period","Gross","Tax","SSS","PhilHealth","Pagibig","Net"},0){
            @Override
            public boolean isCellEditable(int r,int c){ return false; }
        };

        table = new JTable(model);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        add(scroll, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBackground(Color.WHITE);
        eastPanel.add(createPayslipButtons(employeeID, role), BorderLayout.NORTH);

        add(eastPanel, BorderLayout.EAST);

        if(payrollService.canProcessPayroll(role)){
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setBackground(Color.WHITE);

            southPanel.add(createCalculationPanel(), BorderLayout.CENTER);
            southPanel.add(createDeleteButton(), BorderLayout.SOUTH);

            add(southPanel, BorderLayout.SOUTH);
        }

        loadPayrollData(employeeID, role);
    }

    private JPanel createHeader(){

        JPanel header = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,blue,
                        getWidth(),0,pink
                );

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        header.setLayout(new FlowLayout(FlowLayout.LEFT));
        header.setPreferredSize(new Dimension(100,60));

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/payroll.png"));
        Image img = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);

        JLabel iconLabel = new JLabel(new ImageIcon(img));

        JLabel title = new JLabel("Payroll Management");
        title.setFont(new Font("Segoe UI",Font.BOLD,20));
        title.setForeground(Color.WHITE);

        header.add(Box.createHorizontalStrut(10));
        header.add(iconLabel);
        header.add(Box.createHorizontalStrut(10));
        header.add(title);

        return header;
    }

    private void styleTable(){

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));
        table.setSelectionBackground(pink);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230,230,230));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setBackground(blue);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100,40));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for(int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,Object value,boolean isSelected,
                    boolean hasFocus,int row,int column){

                Component c = super.getTableCellRendererComponent(
                        table,value,isSelected,hasFocus,row,column);

                if(!isSelected){
                    c.setBackground(row%2==0?new Color(245,250,255):Color.WHITE);
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }

    private JPanel createPayslipButtons(String employeeID,String role){

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10,10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(20,20,20,20)
        ));

        JLabel title = new JLabel("View Payslip");
        title.setFont(new Font("Segoe UI",Font.BOLD,18));
        title.setForeground(blue);

        JLabel desc = new JLabel(
                "<html><body style='width:200px'>Open and print employee payslip records.</body></html>"
        );
        desc.setFont(new Font("Segoe UI",Font.PLAIN,13));
        desc.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(desc);

        GradientButton viewBtn = new GradientButton("View Payslip");

        viewBtn.addActionListener(e -> {
            JFrame frame = new JFrame("Payslip Viewer");
            frame.setSize(640,560);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            PayslipPanel payslipPanel = new PayslipPanel(employeeID,role);
            frame.add(payslipPanel,BorderLayout.CENTER);

            frame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewBtn);

        card.add(textPanel,BorderLayout.NORTH);
        card.add(buttonPanel,BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCalculationPanel(){

        JPanel panel = new JPanel(new GridLayout(5,3,8,8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                "Payroll Calculator"));
        panel.setBackground(Color.WHITE);

        JTextField idField = new JTextField();
        JLabel idError = new JLabel("");
        idError.setForeground(Color.RED);

        JTextField hoursField = new JTextField();
        JLabel hoursError = new JLabel("");
        hoursError.setForeground(Color.RED);

        JTextField rateField = new JTextField();
        JLabel rateError = new JLabel("");
        rateError.setForeground(Color.RED);

        JComboBox<String> periodCombo = new JComboBox<>();
        stylePeriodCombo(periodCombo);
        loadPeriodOptions(periodCombo);

        JLabel periodHint = new JLabel("Select payroll cutoff dates");
        periodHint.setForeground(new Color(100,100,100));

        makeNumericOnly(idField,idError);
        makeNumericOnly(hoursField,hoursError);
        makeNumericOnly(rateField,rateError);

        GradientButton calculateBtn = new GradientButton("Calculate & Save");

        panel.add(new JLabel("Employee ID:"));
        panel.add(idField);
        panel.add(idError);

        panel.add(new JLabel("Hours Worked:"));
        panel.add(hoursField);
        panel.add(hoursError);

        panel.add(new JLabel("Rate / Hour:"));
        panel.add(rateField);
        panel.add(rateError);

        panel.add(new JLabel("Period:"));
        panel.add(periodCombo);
        panel.add(periodHint);

        panel.add(new JLabel());
        panel.add(calculateBtn);
        panel.add(new JLabel());

        calculateBtn.addActionListener(e -> {
            try {
                // Panel only collects raw strings — zero logic, zero parsing
                String employeeId     = idField.getText().trim();
                String hoursText      = hoursField.getText().trim();
                String rateText       = rateField.getText().trim();
                String selectedPeriod = periodCombo.getSelectedItem() == null
                                        ? "" : periodCombo.getSelectedItem().toString();

                PayrollCalculator calculator = new OvertimePayrollCalculator();

                // Service receives raw strings and handles ALL validation
                Payslip payslip = payrollService.processPayroll(
                        employeeId,
                        selectedPeriod,
                        hoursText,
                        rateText,
                        calculator
                );

                showModernMessage("Payroll Saved", "Net Salary: " + String.format("%.2f", payslip.getNet()));
                loadPayrollData(null, "admin");
                idField.setText("");
                hoursField.setText("");
                rateField.setText("");
                periodCombo.setSelectedIndex(0);

            } catch (IllegalArgumentException ex) {
                showModernMessage("Validation Error", ex.getMessage());
            } catch (Exception ex) {
                showModernMessage("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        return panel;
    }

    private void stylePeriodCombo(JComboBox<String> combo){
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
    }

    private void loadPeriodOptions(JComboBox<String> periodCombo){
        periodCombo.removeAllItems();
        periodCombo.addItem("Select Period");

        int currentYear = java.time.LocalDate.now().getYear();

        List<String> periods = generateSemiMonthlyPeriods(currentYear);

        for(String period : periods){
            periodCombo.addItem(period);
        }
    }

    private List<String> generateSemiMonthlyPeriods(int year){
        List<String> periods = new ArrayList<>();

        for(int month = 1; month <= 12; month++){
            YearMonth yearMonth = YearMonth.of(year, month);
            int lastDay = yearMonth.lengthOfMonth();

            String monthText = String.format("%02d", month);

            String firstCutoff = year + "-" + monthText + "-01 to " + year + "-" + monthText + "-15";
            String secondCutoff = year + "-" + monthText + "-16 to " + year + "-" + monthText + "-" + String.format("%02d", lastDay);

            periods.add(firstCutoff);
            periods.add(secondCutoff);
        }

        return periods;
    }

    private JPanel createDeleteButton(){

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        GradientButton deleteBtn = new GradientButton("Delete Selected Payroll");

        deleteBtn.addActionListener(e -> {

            int row = table.getSelectedRow();

            if(row<0){
                showModernMessage("Warning","Please select a payroll to delete.");
                return;
            }

            boolean confirm = showModernConfirm(
                    "Confirm Delete",
                    "Are you sure you want to delete this payroll?"
            );

            if(confirm){
                String empId = (String)model.getValueAt(row,0);
                String period = (String)model.getValueAt(row,2);

                payrollService.deletePayroll(empId,period);
                loadPayrollData(null,"admin");

                showModernMessage("Success","Payroll deleted.");
            }
        });

        panel.add(deleteBtn);
        return panel;
    }

    private void makeNumericOnly(JTextField field,JLabel error){

        field.getDocument().addDocumentListener(new DocumentListener(){

            private void validate(){
                if(!field.getText().matches("[0-9.]*")){
                    error.setText("Numbers only");
                }else{
                    error.setText("");
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e){validate();}
            @Override
            public void removeUpdate(DocumentEvent e){validate();}
            @Override
            public void changedUpdate(DocumentEvent e){validate();}
        });
    }

    private void loadPayrollData(String employeeID,String role){

        model.setRowCount(0);

        List<String[]> records = payrollService.getAllPayroll();
        EmployeeService empService = new EmployeeService();
        List<String[]> employees = empService.getAllEmployees();

        for(String[] row : records){

            if(payrollService.canViewAllPayroll(role) ||
               (employeeID!=null && row[0].equals(employeeID))){

                String[] paddedRow = new String[8];
                for(int i=0;i<paddedRow.length;i++){
                    paddedRow[i] = i<row.length ? row[i] : "";
                }

                String name = "";
                for(String[] emp : employees){
                    if(emp[0].equals(paddedRow[0])){
                        name = emp[2] + " " + emp[1];
                        break;
                    }
                }

                model.addRow(new Object[]{
                        paddedRow[0],
                        name,
                        paddedRow[1],
                        paddedRow[2],
                        paddedRow[3],
                        paddedRow[4],
                        paddedRow[5],
                        paddedRow[6],
                        paddedRow[7]
                });
            }
        }
    }

    private void showModernMessage(String title,String message){

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),true);
        dialog.setSize(360,180);
        dialog.setLocationRelativeTo(this);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title,JLabel.CENTER);
        header.setFont(new Font("Segoe UI",Font.BOLD,18));
        header.setForeground(Color.WHITE);

        JLabel msg = new JLabel("<html><center>"+message+"</center></html>",JLabel.CENTER);
        msg.setForeground(Color.WHITE);

        GradientButton ok = new GradientButton("OK");
        ok.addActionListener(e->dialog.dispose());

        JPanel btn = new JPanel();
        btn.setOpaque(false);
        btn.add(ok);

        panel.add(header,BorderLayout.NORTH);
        panel.add(msg,BorderLayout.CENTER);
        panel.add(btn,BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private boolean showModernConfirm(String title,String message){

        final boolean[] result={false};

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),true);
        dialog.setSize(360,180);
        dialog.setLocationRelativeTo(this);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title,JLabel.CENTER);
        header.setFont(new Font("Segoe UI",Font.BOLD,18));
        header.setForeground(Color.WHITE);

        JLabel msg = new JLabel("<html><center>"+message+"</center></html>",JLabel.CENTER);
        msg.setForeground(Color.WHITE);

        GradientButton ok = new GradientButton("OK");
        GradientButton cancel = new GradientButton("Cancel");

        ok.addActionListener(e->{result[0]=true;dialog.dispose();});
        cancel.addActionListener(e->dialog.dispose());

        JPanel btn = new JPanel();
        btn.setOpaque(false);
        btn.add(ok);
        btn.add(cancel);

        panel.add(header,BorderLayout.NORTH);
        panel.add(msg,BorderLayout.CENTER);
        panel.add(btn,BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);

        return result[0];
    }

    class GradientButton extends JButton{

        public GradientButton(String text){
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI",Font.BOLD,13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10,22,10,22));
        }

        @Override
        protected void paintComponent(Graphics g){

            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0,0,blue,
                    getWidth(),getHeight(),pink
            );

            g2.setPaint(gp);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class GradientPanel extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            GradientPaint gp=new GradientPaint(0,0,blue,getWidth(),getHeight(),pink);
            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }
}