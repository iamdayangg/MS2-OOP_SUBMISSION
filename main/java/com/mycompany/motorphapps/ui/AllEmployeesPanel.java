package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import com.mycompany.motorphapps.service.EmployeeService;

public class AllEmployeesPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JEditorPane detailPane;
    private JTextField searchField;
    private EmployeeService employeeService;

    private final String[] fullColumns = {
        "Employee #","Last Name","First Name","Birthday","Address","Phone Number",
        "SSS #","Philhealth #","TIN #","Pag-ibig #","Status","Position",
        "Immediate Supervisor","Basic Salary","Rice Subsidy","Phone Allowance",
        "Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"
    };

    public AllEmployeesPanel(String role){

        employeeService = new EmployeeService();

        setLayout(new BorderLayout(10,10));
        setOpaque(false);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10,10));
        add(mainPanel,BorderLayout.CENTER);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon icon = null;
        try{
            icon = new ImageIcon(getClass().getResource("/images/employees.png"));
        }catch(Exception e){
            System.out.println("employees.png not found");
        }

        JLabel iconLabel = new JLabel();
        if(icon!=null){
            Image img = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        }

        JLabel title = new JLabel("Employee Directory");
        title.setFont(new Font("Segoe UI",Font.BOLD,22));
        title.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        titlePanel.setOpaque(false);
        titlePanel.add(iconLabel);
        titlePanel.add(title);

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI",Font.PLAIN,14));

        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);

        topPanel.add(titlePanel,BorderLayout.WEST);
        topPanel.add(searchPanel,BorderLayout.EAST);

        mainPanel.add(topPanel,BorderLayout.NORTH);


        model = new DefaultTableModel(
                new Object[]{"Employee #","Name","Status"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setSelectionBackground(new Color(255,120,200));
        table.setSelectionForeground(Color.WHITE);

        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setRowMargin(4);
        table.setIntercellSpacing(new Dimension(10,6));

        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loadEmployeeData();

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){

            public Component getTableCellRendererComponent(
                    JTable table,Object value,boolean isSelected,
                    boolean hasFocus,int row,int column){

                Component c = super.getTableCellRendererComponent(
                        table,value,isSelected,hasFocus,row,column);

                if(!isSelected){
                    if(row%2==0)
                        c.setBackground(new Color(230,240,255));
                    else
                        c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setBackground(new Color(80,130,255));
        header.setForeground(Color.WHITE);

        table.getSelectionModel().addListSelectionListener(e->{

            if(!e.getValueIsAdjusting()){

                int row = table.getSelectedRow();

                if(row>=0){
                    row = table.convertRowIndexToModel(row);
                    showEmployeeDetails(getFullRowData(row));
                }
            }
        });


        detailPane = new JEditorPane();
        detailPane.setContentType("text/html");
        detailPane.setEditable(false);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.getVerticalScrollBar().setBlockIncrement(50);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane detailScroll = new JScrollPane(detailPane);
        detailScroll.getVerticalScrollBar().setUnitIncrement(16);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tableScroll,
                detailScroll
        );

        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.4);

        mainPanel.add(splitPane,BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        if(employeeService.canManageEmployees(role)){

            GradientButton addBtn = new GradientButton("Add Employee");
            GradientButton editBtn = new GradientButton("Edit");
            GradientButton deleteBtn = new GradientButton("Delete");

            addBtn.addActionListener(e->openEmployeeDialog(null));
            editBtn.addActionListener(e->editSelected());
            deleteBtn.addActionListener(e->deleteSelected());

            buttonPanel.add(addBtn);
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
        }

        mainPanel.add(buttonPanel,BorderLayout.SOUTH);


        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
        });
    }


    private void loadEmployeeData(){

        model.setRowCount(0);

        List<String[]> employees = employeeService.getAllEmployees();

        for(String[] row:employees){

            if(row.length>=3){

                String id = row[0];
                String name = row[2]+" "+row[1];
                String status = row.length>10 ? row[10] : "";

                model.addRow(new Object[]{id,name,status});
            }
        }
    }


    private void filter(){

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);

        String text = searchField.getText();

        sorter.setRowFilter(
                text.length()==0 ? null :
                        RowFilter.regexFilter("(?i)"+text));

        table.setRowSorter(sorter);
    }


    private String[] getFullRowData(int row){

        String empId = model.getValueAt(row,0).toString();

        List<String[]> employees = employeeService.getAllEmployees();

        for(String[] r:employees){
            if(r[0].equals(empId))
                return r;
        }

        return new String[fullColumns.length];
    }


    private void showEmployeeDetails(String[] data){

        StringBuilder html =
                new StringBuilder("<html><body style='font-family:Segoe UI;'>");

        html.append("<h2 style='color:#4A6CFF;'>Employee Profile</h2><table>");

        for(int i=0;i<fullColumns.length;i++){

            String val = i<data.length ? data[i] : "";

            html.append("<tr><td><b>")
                    .append(fullColumns[i])
                    .append("</b></td><td>")
                    .append(val)
                    .append("</td></tr>");
        }

        html.append("</table></body></html>");

        detailPane.setText(html.toString());
        detailPane.setCaretPosition(0);
    }

    private void editSelected(){

        int row = table.getSelectedRow();

        if(row<0){
            showModernMessage("Message","Select employee first.");
            return;
        }

        row = table.convertRowIndexToModel(row);

        String[] data = getFullRowData(row);

        openEmployeeDialog(data);
    }


    private void deleteSelected(){

        int row = table.getSelectedRow();

        if(row<0){
            showModernMessage("Message","Select employee first.");
            return;
        }

        row = table.convertRowIndexToModel(row);

        String empID = model.getValueAt(row,0).toString();

        employeeService.deleteEmployee(empID);

        loadEmployeeData();
        detailPane.setText("");

        showModernMessage("Success","Employee record deleted.");
    }


    private void openEmployeeDialog(String[] data){

        boolean isEditing = (data != null);

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle(isEditing ? "Edit Employee" : "Add New Employee");
        dialog.setSize(660, 700);
        dialog.setLocationRelativeTo(this);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel header = new JLabel(isEditing ? "Edit Employee" : "Add New Employee", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 8, 0, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        boolean[] required = {
            true,  // 0  Employee #
            true,  // 1  Last Name
            true,  // 2  First Name
            true,  // 3  Birthday
            true,  // 4  Address
            true,  // 5  Phone Number
            true,  // 6  SSS #
            true,  // 7  PhilHealth #
            true,  // 8  TIN #
            true,  // 9  Pag-ibig #
            true,  // 10 Status
            true,  // 11 Position
            false, // 12 Immediate Supervisor (optional)
            true,  // 13 Basic Salary
            true,  // 14 Rice Subsidy
            true,  // 15 Phone Allowance
            true,  // 16 Clothing Allowance
            true,  // 17 Gross Semi-monthly Rate
            true   // 18 Hourly Rate
        };

        String[] hints = {
            "5 digits, e.g. 10001",
            "e.g. Dela Cruz",
            "e.g. Juan",
            "MM/DD/YYYY",
            "Full address",
            "09XXXXXXXXX",
            "##-#######-#",
            "####-####-####",
            "###-###-###",
            "####-####-####",
            "Regular or Probationary",
            "e.g. Software Engineer",
            "e.g. Garcia, Manuel III",
            "Numeric, e.g. 30000",
            "Numeric, e.g. 1500",
            "Numeric, e.g. 2000",
            "Numeric, e.g. 1000",
            "Numeric, e.g. 15000",
            "Numeric, e.g. 357.14"
        };

        JTextField[] fields    = new JTextField[fullColumns.length];
        JLabel[]     errLabels = new JLabel[fullColumns.length];

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Regular","Probationary","Contractual"});

        int row = 0;
        for(int i = 0; i < fullColumns.length; i++){

 
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            JLabel lbl = new JLabel(fullColumns[i] + (required[i] ? " *" : ""));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(required[i] ? new Color(30,30,30) : new Color(100,100,100));
            formPanel.add(lbl, gbc);

            // ── Input ──
            gbc.gridx = 1; gbc.weightx = 0.7;
            String val = (data != null && i < data.length) ? data[i] : "";

            if(i == 10){

                statusCombo.setSelectedItem(val.isEmpty() ? "Regular" : val);
                statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                formPanel.add(statusCombo, gbc);
            } else {
                fields[i] = new JTextField(val, 20);
                fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                fields[i].setToolTipText(hints[i]);


                if(val.isEmpty()){
                    fields[i].setForeground(Color.GRAY);
                    fields[i].setText(hints[i]);
                    final int fi = i;
                    fields[i].addFocusListener(new java.awt.event.FocusAdapter(){
                        public void focusGained(java.awt.event.FocusEvent e){
                            if(fields[fi].getText().equals(hints[fi])){
                                fields[fi].setText("");
                                fields[fi].setForeground(Color.BLACK);
                            }
                        }
                        public void focusLost(java.awt.event.FocusEvent e){
                            if(fields[fi].getText().trim().isEmpty()){
                                fields[fi].setForeground(Color.GRAY);
                                fields[fi].setText(hints[fi]);
                            }
                        }
                    });
                } else {
                    fields[i].setForeground(Color.BLACK);
                }

                if(i == 0 && isEditing){
                    fields[i].setEditable(false);
                    fields[i].setBackground(new Color(230,230,230));
                    fields[i].setForeground(new Color(80,80,80));
                    fields[i].setToolTipText("Employee number cannot be changed.");
                }

                formPanel.add(fields[i], gbc);
            }

            row++;

            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
            errLabels[i] = new JLabel(" ");
            errLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 11));
            errLabels[i].setForeground(new Color(200,0,0));
            errLabels[i].setBorder(BorderFactory.createEmptyBorder(0,2,4,0));
            formPanel.add(errLabels[i], gbc);
            row++;
        }

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scroll, BorderLayout.CENTER);

        GradientButton saveBtn   = new GradientButton(isEditing ? "Update" : "Save");
        GradientButton cancelBtn = new GradientButton("Cancel");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);

        saveBtn.addActionListener(e -> {

            for(JLabel el : errLabels) el.setText(" ");

            boolean valid = true;
            String[] emp  = new String[fullColumns.length];

            for(int i = 0; i < fullColumns.length; i++){

                if(i == 10){

                    emp[i] = statusCombo.getSelectedItem().toString();
                } else {
                    String val2 = fields[i].getText().trim();
       
                    emp[i] = val2.equals(hints[i]) ? "" : val2;
                }

                if(required[i] && emp[i].isEmpty()){
                    errLabels[i].setText("Required");
                    valid = false;
                }
            }

            if(!valid){
                showModernMessage("Incomplete Form", "Please fill in all required (*) fields.");
                return;
            }

 
            if(!isEditing && employeeService.employeeExists(emp[0])){
                errLabels[0].setText("Employee number already exists.");
                fields[0].requestFocus();
                return;
            }

            try{
                if(!isEditing)
                    employeeService.addEmployee(emp);
                else
                    employeeService.updateEmployee(emp);

                loadEmployeeData();
                dialog.dispose();
                showModernMessage("Success", isEditing ? "Employee updated successfully." : "Employee added successfully.");

            } catch(IllegalArgumentException ex){

                String msg = ex.getMessage();
                if(msg.contains("Birthday"))        { errLabels[3].setText(msg);  }
                else if(msg.contains("Phone"))      { errLabels[5].setText(msg);  }
                else if(msg.contains("SSS"))        { errLabels[6].setText(msg);  }
                else if(msg.contains("PhilHealth")) { errLabels[7].setText(msg);  }
                else if(msg.contains("TIN"))        { errLabels[8].setText(msg);  }
                else if(msg.contains("Pag-IBIG"))   { errLabels[9].setText(msg);  }
                else if(msg.contains("Basic Salary"))        { errLabels[13].setText(msg); }
                else if(msg.contains("Rice"))       { errLabels[14].setText(msg); }
                else if(msg.contains("Phone Allow"))         { errLabels[15].setText(msg); }
                else if(msg.contains("Clothing"))   { errLabels[16].setText(msg); }
                else if(msg.contains("Gross"))      { errLabels[17].setText(msg); }
                else if(msg.contains("Hourly"))     { errLabels[18].setText(msg); }
                else { showModernMessage("Validation Error", msg); }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }


    private void showModernMessage(String title,String message){

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),true);

        dialog.setSize(360,200);
        dialog.setLocationRelativeTo(this);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel(title,JLabel.CENTER);
        header.setFont(new Font("Segoe UI",Font.BOLD,18));
        header.setForeground(Color.WHITE);

        JLabel msg = new JLabel(message,JLabel.CENTER);
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


    class GradientPanel extends JPanel{

        protected void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0,0,new Color(70,120,255),
                    getWidth(),getHeight(),
                    new Color(255,120,200));

            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }

    class GradientButton extends JButton{

        public GradientButton(String text){

            super(text);

            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI",Font.BOLD,14));
            setBorder(BorderFactory.createEmptyBorder(8,18,8,18));
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g){

            Graphics2D g2 = (Graphics2D)g.create();

            GradientPaint gp = new GradientPaint(
                    0,0,new Color(70,120,255),
                    0,getHeight(),
                    new Color(255,120,200));

            g2.setPaint(gp);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);

            g2.dispose();

            super.paintComponent(g);
        }
    }
}