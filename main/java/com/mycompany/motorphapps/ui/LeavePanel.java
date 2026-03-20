/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.LeaveRequest;
import com.mycompany.motorphapps.service.LeaveService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author DAYANG GWAPA
 */
public class LeavePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private LeaveService leaveService;

    private String role;
    private String employeeId;

    private Color blue = new Color(70,130,180);
    private Color pink = new Color(255,105,180);

    public LeavePanel(String role,String employeeId){

        this.role = role;
        this.employeeId = employeeId;

        leaveService = new LeaveService();

        setLayout(new BorderLayout(20,20));
        setBackground(Color.WHITE);

        add(createHeader(),BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Leave ID","Employee ID","Type","Start Date","End Date","Status"},0
        ){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        add(scroll,BorderLayout.CENTER);

        if(leaveService.canManageLeave(role)){
            add(createAdminButtons(),BorderLayout.SOUTH);
        }else{
            add(createEmployeeButtons(),BorderLayout.SOUTH);
        }

        loadLeaves();
    }

private JPanel createHeader(){

    JPanel header = new JPanel(){
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

    header.setPreferredSize(new Dimension(100,60));
    header.setLayout(new FlowLayout(FlowLayout.LEFT));

    JLabel title = new JLabel("Leave Management");
    title.setFont(new Font("Segoe UI",Font.BOLD,20));
    title.setForeground(Color.WHITE);

    ImageIcon icon = new ImageIcon(getClass().getResource("/images/leave.png"));
    Image img = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
    JLabel iconLabel = new JLabel(new ImageIcon(img));

    header.add(Box.createHorizontalStrut(15));
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
            public Component getTableCellRendererComponent(
                    JTable table,Object value,boolean isSelected,
                    boolean hasFocus,int row,int column){

                Component c = super.getTableCellRendererComponent(
                        table,value,isSelected,hasFocus,row,column);

                if(!isSelected){
                    c.setBackground(row%2==0 ? new Color(245,250,255) : Color.WHITE);
                }

                setHorizontalAlignment(JLabel.CENTER);

                return c;
            }
        });
    }


    private GradientButton createButton(String text){
        GradientButton btn = new GradientButton(text);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI",Font.BOLD,13));
        return btn;
    }

    private JPanel createAdminButtons(){

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        GradientButton approveBtn = createButton("Approve");
        GradientButton rejectBtn = createButton("Reject");
        GradientButton deleteBtn = createButton("Delete");

        approveBtn.addActionListener(e -> updateStatus("Approved"));
        rejectBtn.addActionListener(e -> updateStatus("Rejected"));
        deleteBtn.addActionListener(e -> deleteLeave());

        panel.add(approveBtn);
        panel.add(rejectBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private JPanel createEmployeeButtons(){

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        GradientButton requestBtn = createButton("Request Leave");

        requestBtn.addActionListener(e -> requestLeave());

        panel.add(requestBtn);

        return panel;
    }


    private void requestLeave(){

        JDialog dialog = new JDialog(
                (Frame)SwingUtilities.getWindowAncestor(this),true);

        dialog.setSize(420,320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout()){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,blue,
                        getWidth(),getHeight(),pink);

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Request Leave",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,20));
        title.setForeground(Color.WHITE);

        // Combo box populated from service — no hardcoded types in the GUI
        JComboBox<String> typeCombo = new JComboBox<>(leaveService.getLeaveTypes());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setBackground(Color.WHITE);

        JTextField start = new JTextField("YYYY-MM-DD");
        JTextField end   = new JTextField("YYYY-MM-DD");
        styleField(start);
        styleField(end);
        start.setForeground(Color.WHITE);
        end.setForeground(Color.WHITE);

        start.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (start.getText().equals("YYYY-MM-DD")) { start.setText(""); start.setForeground(Color.BLACK); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (start.getText().trim().isEmpty()) { start.setText("YYYY-MM-DD"); start.setForeground(Color.BLACK); }
            }
        });
        end.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (end.getText().equals("YYYY-MM-DD")) { end.setText(""); end.setForeground(Color.BLACK); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (end.getText().trim().isEmpty()) { end.setText("YYYY-MM-DD"); end.setForeground(Color.BLACK); }
            }
        });

        JLabel typeLbl  = createLabel("Leave Type:");
        JLabel startLbl = createLabel("Start Date:");
        JLabel endLbl   = createLabel("End Date:");

        GradientButton submit = new GradientButton("Submit");
        GradientButton cancel = new GradientButton("Cancel");

        submit.addActionListener(e -> {
            try {
                // Pass raw values — service does all validation
                leaveService.requestLeave(
                        employeeId,
                        (String) typeCombo.getSelectedItem(),
                        start.getText().equals("YYYY-MM-DD") ? "" : start.getText(),
                        end.getText().equals("YYYY-MM-DD")   ? "" : end.getText()
                );
                dialog.dispose();
                loadLeaves();
                showModernMessage("Success", "Leave request submitted.");
            } catch (IllegalArgumentException ex) {
                showModernMessage("Invalid Input", ex.getMessage());
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title,gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        panel.add(typeLbl,gbc);
        gbc.gridx = 1;
        panel.add(typeCombo,gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(startLbl,gbc);
        gbc.gridx = 1;
        panel.add(start,gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(endLbl,gbc);
        gbc.gridx = 1;
        panel.add(end,gbc);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(submit);
        btnPanel.add(cancel);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(btnPanel,gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


private void updateStatus(String status){

    int row = table.getSelectedRow();

    if(row < 0){
        showModernMessage("Message","Select a leave request.");
        return;
    }

    String leaveId = model.getValueAt(row,0).toString();

    int confirm = showModernConfirm(
            status + " Leave",
            "Are you sure you want to " + status.toLowerCase() + " this leave request?"
    );

    if(confirm == 1){

        leaveService.updateStatus(leaveId,status);

        loadLeaves();

        showModernMessage("Success","Leave request " + status.toLowerCase() + ".");
    }
}


private void deleteLeave(){

    int row = table.getSelectedRow();

    if(row < 0){
        showModernMessage("Message","Select a record first.");
        return;
    }

    int confirm = showModernConfirm(
            "Delete Leave",
            "Delete this leave record?"
    );

    if(confirm == 1){

        String leaveId = model.getValueAt(row,0).toString();

        leaveService.deleteLeave(leaveId);

        loadLeaves();

        showModernMessage("Deleted","Leave record deleted.");
    }
}

    private void loadLeaves(){

        model.setRowCount(0);

        List<LeaveRequest> records = leaveService.getAllLeaves();

        for(LeaveRequest leave : records){

            if(leaveService.canViewAllLeaves(role)
                    || leave.getEmployeeId().equals(employeeId)){

                model.addRow(new Object[]{
                        leave.getId(),
                        leave.getEmployeeId(),
                        leave.getLeaveType(),
                        leave.getStartDate(),
                        leave.getEndDate(),
                        leave.getStatus()
                });
            }
        }
    }
    
    private void showModernMessage(String title,String message){

    JDialog dialog = new JDialog(
            (Frame)SwingUtilities.getWindowAncestor(this),true);

    dialog.setSize(360,200);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new BorderLayout()){
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0,0,blue,
                    getWidth(),getHeight(),pink);

            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    };

    JLabel header = new JLabel(title,JLabel.CENTER);
    header.setFont(new Font("Segoe UI",Font.BOLD,18));
    header.setForeground(Color.WHITE);

    JLabel msg = new JLabel(message,JLabel.CENTER);
    msg.setFont(new Font("Segoe UI",Font.PLAIN,14));
    msg.setForeground(Color.WHITE);

    GradientButton ok = new GradientButton("OK");

    ok.addActionListener(e -> dialog.dispose());

    JPanel btn = new JPanel();
    btn.setOpaque(false);
    btn.add(ok);

    panel.add(header,BorderLayout.NORTH);
    panel.add(msg,BorderLayout.CENTER);
    panel.add(btn,BorderLayout.SOUTH);

    dialog.setContentPane(panel);
    dialog.setVisible(true);
}
    
    
    private int showModernConfirm(String title,String message){

    final int[] result = {0};

    JDialog dialog = new JDialog(
            (Frame)SwingUtilities.getWindowAncestor(this),true);

    dialog.setSize(380,220);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new BorderLayout()){
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0,0,blue,
                    getWidth(),getHeight(),pink);

            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    };

    JLabel header = new JLabel(title,JLabel.CENTER);
    header.setFont(new Font("Segoe UI",Font.BOLD,18));
    header.setForeground(Color.WHITE);

    JLabel msg = new JLabel(
            "<html><div style='text-align:center;'>"+message+"</div></html>",
            JLabel.CENTER);
    msg.setForeground(Color.WHITE);

    GradientButton yes = new GradientButton("OK");
    GradientButton no = new GradientButton("Cancel");

    yes.addActionListener(e -> {
        result[0] = 1;
        dialog.dispose();
    });

    no.addActionListener(e -> dialog.dispose());

    JPanel btnPanel = new JPanel();
    btnPanel.setOpaque(false);
    btnPanel.add(yes);
    btnPanel.add(no);

    panel.add(header,BorderLayout.NORTH);
    panel.add(msg,BorderLayout.CENTER);
    panel.add(btnPanel,BorderLayout.SOUTH);

    dialog.setContentPane(panel);
    dialog.setVisible(true);

    return result[0];
}
    private JLabel createLabel(String text){

    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI",Font.BOLD,13));
    lbl.setForeground(Color.WHITE);

    return lbl;
}

private void styleField(JTextField field){

    field.setFont(new Font("Segoe UI",Font.PLAIN,13));
    field.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
}


    class GradientButton extends JButton{

        public GradientButton(String text){
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8,18,8,18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        protected void paintComponent(Graphics g){

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0,0,blue,
                    getWidth(),getHeight(),pink
            );

            g2.setPaint(gp);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);

            super.paintComponent(g2);

            g2.dispose();
        }
    }
}