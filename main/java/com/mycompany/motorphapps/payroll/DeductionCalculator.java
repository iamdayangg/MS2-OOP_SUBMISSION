/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;
/**
 *
 * @author DAYANG GWAPA
 */
public class DeductionCalculator {

    private double tax;
    private double sss;
    private double philHealth;
    private double pagibig;

    public void computeDeductions(double gross) {
        tax        = gross * 0.10;   // 10% tax
        sss        = gross * 0.045;  // 4.5% SSS
        philHealth = gross * 0.03;   // 3% PhilHealth
        pagibig    = gross * 0.02;   // 2% Pag-IBIG
    }

    public double getTax() { return tax; }
    public double getSss() { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagibig() { return pagibig; }

    public double getTotalDeductions() {
        return tax + sss + philHealth + pagibig;
    }
}