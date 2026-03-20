package com.mycompany.motorphapps.model;

/**
 * Factory — picks the correct AttendanceSummary subclass for a given role.
 * This is the single place that decides which OOP subclass to use.
 *
 * @author DAYANG GWAPA
 */
public class AttendanceSummaryFactory {

    public static AttendanceSummary forRole(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        switch (r) {
            case "ADMIN":
            case "FINANCE":
                return new FinanceAdminSummary();
            case "HR":
            case "IT":
                return new ManagerSummary();
            default:
                return new EmployeeSummary();
        }
    }
}
