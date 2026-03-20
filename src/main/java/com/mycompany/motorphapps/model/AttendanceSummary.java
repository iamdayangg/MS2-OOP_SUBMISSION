package com.mycompany.motorphapps.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Abstract base class for attendance summary calculation.
 * Demonstrates: ABSTRACTION — defines the contract all summaries must follow.
 *
 * @author DAYANG GWAPA
 */
public abstract class AttendanceSummary {

    protected static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── Abstract methods — subclasses MUST override these ──────────────────
    /**
     * Returns the label shown in the summary section header.
     */
    public abstract String getSummaryLabel();

    /**
     * Returns true if this role is allowed to see the summary panel.
     */
    public abstract boolean canViewSummary(String role);

    /**
     * Returns true if this role is allowed to calculate all-employee totals.
     */
    public abstract boolean canCalculateAll(String role);

    // ── Overloaded computeHours methods — OVERLOADING ──────────────────────

    /**
     * Overload 1: compute hours from two LocalTime objects directly.
     */
    public double computeHours(LocalTime timeIn, LocalTime timeOut) {
        if (timeIn == null || timeOut == null) return 0.0;
        long minutes = java.time.Duration.between(timeIn, timeOut).toMinutes();
        return minutes < 0 ? 0.0 : minutes / 60.0;
    }

    /**
     * Overload 2: compute hours from two HH:mm strings.
     */
    public double computeHours(String timeInStr, String timeOutStr) {
        try {
            if (timeInStr == null || timeInStr.isBlank()) return 0.0;
            if (timeOutStr == null || timeOutStr.isBlank()) return 0.0;
            LocalTime in  = LocalTime.parse(timeInStr.trim(), TIME_FMT);
            LocalTime out = LocalTime.parse(timeOutStr.trim(), TIME_FMT);
            return computeHours(in, out);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Overload 3: compute total hours for a single employee across all rows.
     * Each row is String[] { empId, name, date, timeIn, timeOut }.
     */
    public double computeHours(String employeeId, List<String[]> attendanceRows) {
        double total = 0.0;
        for (String[] row : attendanceRows) {
            if (row.length < 5) continue;
            if (!row[0].trim().equals(employeeId)) continue;
            total += computeHours(row[3], row[4]);
        }
        return total;
    }

    /**
     * Overload 4: compute total hours for ALL employees across all rows.
     */
    public double computeHours(List<String[]> attendanceRows) {
        double total = 0.0;
        for (String[] row : attendanceRows) {
            if (row.length < 5) continue;
            total += computeHours(row[3], row[4]);
        }
        return total;
    }

    // ── Shared helper ───────────────────────────────────────────────────────

    /**
     * Format a decimal hours value as "Xh Ym".
     */
    public String formatHours(double totalHours) {
        int h = (int) totalHours;
        int m = (int) Math.round((totalHours - h) * 60);
        return h + "h " + m + "m";
    }
}
