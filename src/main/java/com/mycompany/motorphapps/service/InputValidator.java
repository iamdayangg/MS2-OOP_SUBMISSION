package com.mycompany.motorphapps.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Central validation utility.
 * All validation logic lives here — services call these methods,
 * panels never touch raw input directly.
 *
 * Throws IllegalArgumentException with a clear message on failure.
 * Returns the cleaned/parsed value on success.
 */
public class InputValidator {

    private static final DateTimeFormatter DATE_FMT    = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter BDAY_FMT1   = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final DateTimeFormatter BDAY_FMT2   = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ── String / text ────────────────────────────────────────────────────────

    /** Not null, not blank. */
    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(fieldName + " is required.");
        return value.trim();
    }

    /** Letters, spaces, hyphens only (names). */
    public static String requireName(String value, String fieldName) {
        String v = requireNonBlank(value, fieldName);
        if (!v.matches("[\\p{L}\\s\\-\\.]+"))
            throw new IllegalArgumentException(fieldName + " must contain letters only.");
        return v;
    }

    // ── Numbers ──────────────────────────────────────────────────────────────

    /** Must parse as a positive double. */
    public static double requirePositiveDouble(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        try {
            double d = Double.parseDouble(value.trim());
            if (d <= 0)
                throw new IllegalArgumentException(fieldName + " must be greater than 0.");
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    /** Must parse as a non-negative double (0 is allowed). */
    public static double requireNonNegativeDouble(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        try {
            double d = Double.parseDouble(value.trim());
            if (d < 0)
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    /** Must be a positive integer. */
    public static int requirePositiveInt(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        try {
            int i = Integer.parseInt(value.trim());
            if (i <= 0)
                throw new IllegalArgumentException(fieldName + " must be greater than 0.");
            return i;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
    }

    // ── Dates ────────────────────────────────────────────────────────────────

    /** Accepts MM-dd-yyyy or MM/dd/yyyy (birthday format). */
    public static LocalDate requireBirthday(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        String v = value.trim();
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{BDAY_FMT1, BDAY_FMT2}) {
            try {
                LocalDate d = LocalDate.parse(v, fmt);
                if (d.isAfter(LocalDate.now()))
                    throw new IllegalArgumentException(fieldName + " cannot be in the future.");
                if (d.isBefore(LocalDate.now().minusYears(120)))
                    throw new IllegalArgumentException(fieldName + " is not a realistic date.");
                return d;
            } catch (DateTimeParseException ignored) {}
        }
        throw new IllegalArgumentException(fieldName + " must be in MM-DD-YYYY format.");
    }

    /** Accepts yyyy-MM-dd (leave / payroll period date). */
    public static LocalDate requireDate(String value, String fieldName) {
        requireNonBlank(value, fieldName);
        try {
            return LocalDate.parse(value.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format.");
        }
    }

    /** End date must not be before start date. */
    public static void requireDateRange(LocalDate start, LocalDate end) {
        if (end.isBefore(start))
            throw new IllegalArgumentException("End date cannot be before start date.");
    }

    /** Leave start date must not be in the past. */
    public static void requireFutureOrToday(LocalDate date, String fieldName) {
        if (date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException(fieldName + " cannot be in the past.");
    }

    // ── Government IDs / phone ───────────────────────────────────────────────

    /** 11-digit Philippine phone number (may start with 09 or +63). */
    public static String requirePhone(String value) {
        requireNonBlank(value, "Phone Number");
        String v = value.trim().replaceAll("[\\s\\-]", "");
        if (!v.matches("(09|\\+639)\\d{9}") && !v.matches("\\d{11}"))
            throw new IllegalArgumentException("Phone Number must be a valid 11-digit number.");
        return v;
    }

    /** SSS format: ##-#######-# */
    public static String requireSSS(String value) {
        requireNonBlank(value, "SSS Number");
        String v = value.trim();
        if (!v.matches("\\d{2}-\\d{7}-\\d"))
            throw new IllegalArgumentException("SSS Number must be in ##-#######-# format.");
        return v;
    }

    /** PhilHealth: 12 digits. */
    public static String requirePhilHealth(String value) {
        requireNonBlank(value, "PhilHealth Number");
        String v = value.trim().replaceAll("-", "");
        if (!v.matches("\\d{12}"))
            throw new IllegalArgumentException("PhilHealth Number must be 12 digits.");
        return value.trim();
    }

    /** TIN format: ###-###-###-### or ###-###-###. */
    public static String requireTIN(String value) {
        requireNonBlank(value, "TIN");
        String v = value.trim();
        if (!v.matches("\\d{3}-\\d{3}-\\d{3}(-\\d{3})?"))
            throw new IllegalArgumentException("TIN must be in ###-###-###-### format.");
        return v;
    }

    /** Pag-IBIG: 12 digits. */
    public static String requirePagIbig(String value) {
        requireNonBlank(value, "Pag-IBIG Number");
        String v = value.trim().replaceAll("-", "");
        if (!v.matches("\\d{12}"))
            throw new IllegalArgumentException("Pag-IBIG Number must be 12 digits.");
        return value.trim();
    }

    // ── Employee ID ───────────────────────────────────────────────────────────

    /** Employee ID must be numeric and 5 digits. */
    public static String requireEmployeeId(String value) {
        requireNonBlank(value, "Employee ID");
        String v = value.trim();
        if (!v.matches("\\d{5}"))
            throw new IllegalArgumentException("Employee ID must be exactly 5 digits.");
        return v;
    }

    // ── Hours worked ─────────────────────────────────────────────────────────

    /** Hours must be > 0 and <= 744 (max hours in a month). */
    public static double requireHoursWorked(String value) {
        double h = requirePositiveDouble(value, "Hours Worked");
        if (h > 744)
            throw new IllegalArgumentException("Hours Worked cannot exceed 744 hours.");
        return h;
    }

    /** Hourly rate must be >= Philippine minimum wage floor. */
    public static double requireHourlyRate(String value) {
        double r = requirePositiveDouble(value, "Rate/Hour");
        if (r < 60.0)
            throw new IllegalArgumentException("Rate/Hour must be at least 60.00 (minimum wage).");
        return r;
    }
}
