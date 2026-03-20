package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.LeaveDAO;
import com.mycompany.motorphapps.model.LeaveRequest;
import com.mycompany.motorphapps.model.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * All leave business logic and validation lives here.
 * Throws IllegalArgumentException for invalid input.
 */
public class LeaveService {

    // Allowed leave types
    private static final String[] VALID_TYPES = {
        "Sick Leave", "Vacation Leave", "Emergency Leave",
        "Maternity Leave", "Paternity Leave", "Bereavement Leave"
    };

    private final LeaveDAO leaveDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveDAO();
    }

    public List<LeaveRequest> getAllLeaves() {
        List<String[]> rows = leaveDAO.getAllLeaves();
        List<LeaveRequest> result = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length >= 6)
                result.add(new LeaveRequest(row[0], row[1], row[2], row[3], row[4], row[5]));
        }
        return result;
    }

    public void requestLeave(String employeeId, String type, String startDate, String endDate) {
        // All validation in the service — not in the panel
        String cleanType  = InputValidator.requireNonBlank(type,      "Leave Type");
        LocalDate start   = InputValidator.requireDate(startDate,     "Start Date");
        LocalDate end     = InputValidator.requireDate(endDate,       "End Date");

        InputValidator.requireFutureOrToday(start, "Start Date");
        InputValidator.requireDateRange(start, end);

        // Validate leave type is one of the allowed values
        boolean typeValid = false;
        for (String t : VALID_TYPES) {
            if (t.equalsIgnoreCase(cleanType)) { typeValid = true; break; }
        }
        if (!typeValid)
            throw new IllegalArgumentException(
                "Leave Type must be one of: Sick, Vacation, Emergency, Maternity, Paternity, or Bereavement Leave.");

        // Max 30 consecutive days
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        if (days > 30)
            throw new IllegalArgumentException("Leave duration cannot exceed 30 days.");

        leaveDAO.addLeave(employeeId, cleanType, startDate.trim(), endDate.trim());
    }

    public void updateStatus(String leaveId, String status) {
        leaveDAO.updateStatus(leaveId, status);
    }

    public void deleteLeave(String leaveId) {
        leaveDAO.deleteLeave(leaveId);
    }

    public boolean canManageLeave(String roleStr) {
        return Role.fromString(roleStr).canManageLeave();
    }

    public boolean canViewAllLeaves(String roleStr) {
        return Role.fromString(roleStr).canManageLeave();
    }

    /** Expose valid leave types so the panel can populate a combo box. */
    public String[] getLeaveTypes() {
        return VALID_TYPES.clone();
    }
}
