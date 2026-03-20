/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
/**
 *
 * @author DAYANG GWAPA
 */
public class UIStyle {
    
    public static void stylePanel(JPanel panel){
    panel.setBackground(BACKGROUND);
}

    public static Color PRIMARY = new Color(25,85,155);
    public static Color ACCENT = new Color(52,120,200);
    public static Color BACKGROUND = new Color(245,247,250);

    public static JButton createButton(String text){

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8,18,8,18));

        return btn;
    }

    public static JButton createDangerButton(String text){

        JButton btn = createButton(text);
        btn.setBackground(new Color(200,60,60));

        return btn;
    }

    public static void styleTable(JTable table){

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(230,230,230));
        table.setSelectionBackground(new Color(200,220,240));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0,32));
    }

}