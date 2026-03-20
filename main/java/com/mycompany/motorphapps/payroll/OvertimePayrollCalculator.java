/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;
/**
 *
 * @author DAYANG GWAPA
 */
public class OvertimePayrollCalculator implements PayrollCalculator {

    @Override
    public double calculateGross(double hoursWorked, double ratePerHour) {

        double regularHours = Math.min(hoursWorked, 8);
        double overtimeHours = Math.max(hoursWorked - 8, 0);

        double regularPay = regularHours * ratePerHour;
        double overtimePay = overtimeHours * ratePerHour * 1.5;

        return regularPay + overtimePay;
    }

    @Override
    public double calculateTax(double grossSalary) {
        return grossSalary * 0.10; 
    }

    @Override
    public double calculateSSS(double grossSalary) {
        return grossSalary * 0.045; 
    }

    @Override
    public double calculatePhilHealth(double grossSalary) {
        return grossSalary * 0.03; 
    }

    @Override
    public double calculatePagibig(double grossSalary) {
        return grossSalary * 0.02;
    }
}