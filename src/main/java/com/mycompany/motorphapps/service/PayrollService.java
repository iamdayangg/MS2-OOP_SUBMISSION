package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.model.PeriodAttendanceSummary;
import com.mycompany.motorphapps.service.AttendanceService;
import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.dao.PayrollDAO;
import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.model.Role;
import com.mycompany.motorphapps.payroll.DeductionCalculator;
import com.mycompany.motorphapps.payroll.PayrollCalculator;
import java.util.List;

/**
 * All payroll business logic and validation lives here.
 * Throws IllegalArgumentException for invalid input.
 */
public class PayrollService {

    private final PayrollDAO       payrollDAO;
    private final EmployeeDAO      employeeDAO;
    private final AttendanceService attendanceService;

    public PayrollService() {
        this.payrollDAO       = new PayrollDAO();
        this.employeeDAO      = new EmployeeDAO();
        this.attendanceService = new AttendanceService();
    }

    public List<String[]> getAllPayroll() {
        return payrollDAO.readAllPayroll();
    }

    /**
     * Accepts raw String inputs from the GUI.
     * All parsing and validation happens here — the panel passes nothing but strings.
     */
    public Payslip processPayroll(String employeeId, String period,
                                  String rawHours, String rawRate,
                                  PayrollCalculator calculator) {

        // Validate in order: ID → period → hours → rate
        String cleanId = InputValidator.requireEmployeeId(employeeId);

        if (!employeeExists(cleanId))
            throw new IllegalArgumentException("Employee ID " + cleanId + " does not exist.");

        InputValidator.requireNonBlank(period, "Payroll Period");
        if (period.equals("Select Period"))
            throw new IllegalArgumentException("Please select a payroll period.");

        double hours = InputValidator.requireHoursWorked(rawHours);
        double rate  = InputValidator.requireHourlyRate(rawRate);

        double gross = calculator.calculateGross(hours, rate);
        if (gross <= 0)
            throw new IllegalArgumentException("Calculated gross pay must be greater than 0.");

        DeductionCalculator deductions = new DeductionCalculator();
        deductions.computeDeductions(gross);

        double tax       = deductions.getTax();
        double sss       = deductions.getSss();
        double philHealth = deductions.getPhilHealth();
        double pagibig   = deductions.getPagibig();
        double totalDed  = deductions.getTotalDeductions();

        payrollDAO.savePayslip(cleanId, period, gross, tax, sss, philHealth, pagibig, gross - totalDed);

        Payslip payslip = new Payslip(cleanId, period);
        payslip.compute(gross, totalDed);
        return payslip;
    }


    /**
     * Builds a fully detailed Payslip for display in PayslipPanel.
     *
     * Sources:
     *  - Basic salary, allowances  → employee_data.csv (col 13-16, 18)
     *  - Overtime hours            → attendance_log.csv (real hours worked in the period)
     *  - Deductions                → payroll_data.csv (saved tax/SSS/PhilHealth/PagIBIG)
     *
     * Uses overload 4: compute(basicSalary, overtimePay, rice, phone, clothing, deductions)
     */
    /**
     * Single entry point for PayslipPanel.
     * Looks up payroll + employee rows, computes everything, and returns
     * a fully populated Payslip. The panel only calls getters — zero logic there.
     */
    public Payslip buildPayslipDetail(String employeeId, String period) {

        // --- Look up payroll row ---
        String[] payrollRow = null;
        for (String[] row : payrollDAO.readAllPayroll()) {
            if (row.length >= 2 && row[0].equals(employeeId) && row[1].equals(period)) {
                payrollRow = row;
                break;
            }
        }
        if (payrollRow == null) return null; // no payroll record — panel shows "No Payslip"

        // --- Look up employee row ---
        String[] employeeRow = null;
        for (String[] emp : employeeDAO.getAllEmployees()) {
            if (emp.length >= 3 && emp[0].equals(employeeId)) {
                employeeRow = emp;
                break;
            }
        }

        // --- Sanitise payroll row (fill missing slots with "0") ---
        String[] row = new String[8];
        for (int i = 0; i < row.length; i++) {
            row[i] = (i < payrollRow.length
                    && payrollRow[i] != null
                    && !payrollRow[i].isEmpty())
                    ? payrollRow[i] : "0";
        }

        // --- Read deductions from payroll_data.csv ---
        double tax     = safeDouble(row, 3);
        double sss     = safeDouble(row, 4);
        double phil    = safeDouble(row, 5);
        double pagibig = safeDouble(row, 6);
        double totalDeductions = tax + sss + phil + pagibig;

        // --- Read employee details from employee_data.csv (col indices) ---
        double basicSalary       = safeDouble(employeeRow, 13); // Basic Salary
        double hourlyRate        = safeDouble(employeeRow, 18); // Hourly Rate
        double riceAllowance     = safeDouble(employeeRow, 14); // Rice Subsidy
        double phoneAllowance    = safeDouble(employeeRow, 15); // Phone Allowance
        double clothingAllowance = safeDouble(employeeRow, 16); // Clothing Allowance

        // --- Build employee display name ---
        String employeeName = (employeeRow != null && employeeRow.length >= 3)
                ? employeeRow[2] + " " + employeeRow[1] : "";

        // --- Format period for display ---
        String displayPeriod = formatPeriodForDisplay(period);

        // --- Get overtime from attendance log ---
        PeriodAttendanceSummary attendance = attendanceService.getPeriodSummary(employeeId, period);
        double overtimePay = attendance.computeOvertimePay(hourlyRate);

        // --- Build Payslip ---
        Payslip payslip = new Payslip(employeeId, period);
        payslip.compute(
                basicSalary,
                overtimePay,
                riceAllowance,
                phoneAllowance,
                clothingAllowance,
                totalDeductions
        );

        // Deductions (individual breakdown for display)
        payslip.setTax(tax);
        payslip.setSss(sss);
        payslip.setPhilHealth(phil);
        payslip.setPagibig(pagibig);

        // Attendance metadata
        payslip.setRegularHours(attendance.getRegularHours());
        payslip.setOvertimeHours(attendance.getOvertimeHours());
        payslip.setDaysWorked(attendance.getDaysWorked());
        payslip.setOvertimeSummary(attendance.getOvertimeSummary());
        payslip.setHourlyRate(hourlyRate);

        // Display metadata
        payslip.setEmployeeName(employeeName);
        payslip.setDisplayPeriod(displayPeriod);

        return payslip;
    }

    private String formatPeriodForDisplay(String rawPeriod) {
        if (rawPeriod == null || rawPeriod.trim().isEmpty()) return "";
        String cleaned = rawPeriod.trim();
        if (cleaned.contains(" to ")) {
            try {
                String[] parts = cleaned.split("\\s+to\\s+");
                java.time.LocalDate start = java.time.LocalDate.parse(parts[0].trim(),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                java.time.LocalDate end = java.time.LocalDate.parse(parts[1].trim(),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                java.time.format.DateTimeFormatter nice =
                        java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy");
                return start.format(nice) + " - " + end.format(nice);
            } catch (Exception ignored) { }
        }
        return cleaned;
    }

    private double safeDouble(String[] arr, int i) {
        try {
            return (arr != null && i < arr.length) ? Double.parseDouble(arr[i].trim()) : 0.0;
        } catch (Exception e) { return 0.0; }
    }

    public void deletePayroll(String employeeId, String period) {
        InputValidator.requireNonBlank(employeeId, "Employee ID");
        InputValidator.requireNonBlank(period, "Period");
        payrollDAO.deletePayroll(employeeId, period);
    }

    public boolean canProcessPayroll(String roleStr) {
        return Role.fromString(roleStr).canProcessPayroll();
    }

    public boolean canViewAllPayroll(String roleStr) {
        return Role.fromString(roleStr).canProcessPayroll();
    }

    private boolean employeeExists(String employeeId) {
        List<String[]> all = employeeDAO.getAllEmployees();
        for (String[] row : all) {
            if (row.length > 0 && row[0].trim().equals(employeeId)) return true;
        }
        return false;
    }
}
