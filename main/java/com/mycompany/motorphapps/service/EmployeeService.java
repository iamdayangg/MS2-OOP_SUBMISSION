package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.model.Role;

import java.util.List;

/**
 * All employee business logic and validation lives here.
 * Throws IllegalArgumentException for invalid input — GUI catches and displays the message.
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<String[]> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    /**
     * Validates all fields before saving.
     * Column order matches employee_data.csv:
     * [0]  Employee ID       [1]  Last Name        [2]  First Name
     * [3]  Birthday          [4]  Address          [5]  Phone
     * [6]  SSS               [7]  PhilHealth       [8]  TIN
     * [9]  Pag-IBIG          [10] Status           [11] Supervisor
     * [12] Position          [13] Basic Salary      [14] Rice Allow
     * [15] Phone Allow       [16] Clothing Allow    [17] Gross Semi-monthly
     * [18] Hourly Rate       [19] Gender
     */
    public void addEmployee(String[] data) {
        validate(data, false);
        employeeDAO.addEmployee(data);
    }

    public void updateEmployee(String[] data) {
        if (data == null || data.length == 0) return;
        validate(data, true);
        employeeDAO.updateEmployee(data[0], data);
    }

    public void deleteEmployee(String employeeId) {
        employeeDAO.deleteEmployee(employeeId);
    }

    public boolean employeeExists(String employeeId) {
        List<String[]> all = employeeDAO.getAllEmployees();
        for (String[] row : all) {
            if (row.length > 0 && row[0].equals(employeeId)) return true;
        }
        return false;
    }

    public boolean canManageEmployees(String roleStr) {
        return Role.fromString(roleStr).canEditEmployees();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validate(String[] d, boolean isUpdate) {
        // [0] Employee ID
        if (!isUpdate) InputValidator.requireEmployeeId(d[0]);

        // [1] Last Name, [2] First Name
        InputValidator.requireName(get(d, 1), "Last Name");
        InputValidator.requireName(get(d, 2), "First Name");

        // [3] Birthday
        InputValidator.requireBirthday(get(d, 3), "Birthday");

        // [4] Address
        InputValidator.requireNonBlank(get(d, 4), "Address");

        // [5] Phone
        InputValidator.requirePhone(get(d, 5));

        // [6] SSS
        InputValidator.requireSSS(get(d, 6));

        // [7] PhilHealth
        InputValidator.requirePhilHealth(get(d, 7));

        // [8] TIN
        InputValidator.requireTIN(get(d, 8));

        // [9] Pag-IBIG
        InputValidator.requirePagIbig(get(d, 9));

        // [10] Status — must be Regular or Probationary
        String status = InputValidator.requireNonBlank(get(d, 10), "Status");
        if (!status.equals("Regular") && !status.equals("Probationary"))
            throw new IllegalArgumentException("Status must be Regular or Probationary.");

        // [12] Position
        InputValidator.requireNonBlank(get(d, 12), "Position");

        // [13] Basic Salary
        InputValidator.requirePositiveDouble(get(d, 13), "Basic Salary");

        // [14] Rice Allowance
        InputValidator.requireNonNegativeDouble(get(d, 14), "Rice Allowance");

        // [15] Phone Allowance
        InputValidator.requireNonNegativeDouble(get(d, 15), "Phone Allowance");

        // [16] Clothing Allowance
        InputValidator.requireNonNegativeDouble(get(d, 16), "Clothing Allowance");

        // [17] Gross Semi-monthly
        InputValidator.requirePositiveDouble(get(d, 17), "Gross Semi-monthly");

        // [18] Hourly Rate
        InputValidator.requireHourlyRate(get(d, 18));
    }

    /** Safe array access — returns empty string if index out of range. */
    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length && arr[i] != null) ? arr[i].trim() : "";
    }
}
