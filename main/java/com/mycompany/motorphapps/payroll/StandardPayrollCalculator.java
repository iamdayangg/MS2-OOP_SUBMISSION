/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;
/**
 *
 * @author DAYANG GWAPA
 */
public class StandardPayrollCalculator implements PayrollCalculator {

    @Override
    public double calculateGross(double basicSalary, double allowance) {
        return basicSalary + allowance;
    }

    @Override
    public double calculateTax(double gross) {
        return gross * 0.10; 
    }

    @Override
    public double calculateSSS(double gross) {
        return gross * 0.045;
    }

    @Override
    public double calculatePhilHealth(double gross) {
        return gross * 0.03;
    }

    @Override
    public double calculatePagibig(double gross) {
        return gross * 0.02;
    }
}