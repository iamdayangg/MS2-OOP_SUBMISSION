package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.AttendanceRow;
import java.util.ArrayList;
import com.mycompany.motorphapps.model.AttendanceSummaryResult;
import com.mycompany.motorphapps.model.AttendanceSummaryResult.EmployeeHoursRow;
import com.mycompany.motorphapps.service.AttendanceService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

/**
 * Attendance Panel — DISPLAY ONLY. No logic here.
 *
 * All decisions (who sees what, how hours are calculated,
 * what the summary shows) come from AttendanceService.
 *
 * This panel only:
 *   1. Calls service methods to get ready-made data
 *   2. Puts that data into the table / labels
 *   3. Delegates button actions back to the service
 *
 * @author DAYANG GWAPA
 */
public class AttendancePanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private final AttendanceService attendanceService;
    private final String employeeID;
    private final String role;

    // Summary widgets — populated by refreshSummary(), not by logic
    private JLabel  summaryTitleLabel;
    private JPanel  summaryChipsPanel;
    private JPanel  summaryWrapper;
    private final java.util.List<AttendanceRow> rowStyles = new ArrayList<>(); // style lookup for renderer

    private final Color blue  = new Color(70, 130, 180);
    private final Color pink  = new Color(255, 105, 180);
    private final Color green = new Color(46, 160, 67);

    public AttendancePanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role       = role;
        attendanceService = new AttendanceService();

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Employee ID", "Employee Name", "Date", "Time In", "Time Out", "Total Hours", "OT Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildSummaryWrapper(), BorderLayout.SOUTH);

        refresh(); // load data from service
    }

    // ── Build header (buttons only — no logic) ───────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setBackground(Color.WHITE);
        JLabel icon = new JLabel(new ImageIcon(
                new ImageIcon(getClass().getResource("/images/attendance.png"))
                        .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        JLabel title = new JLabel("Attendance Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        left.add(icon);
        left.add(title);
        header.add(left, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.setBackground(Color.WHITE);

        GradientButton timeInBtn  = new GradientButton("Time In");
        GradientButton timeOutBtn = new GradientButton("Time Out");

        timeInBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/clock.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        timeInBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        timeInBtn.setIconTextGap(8);
        timeOutBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/clock.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        timeOutBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        timeOutBtn.setIconTextGap(8);

        // Action delegates entirely to service — panel just shows result message
        timeInBtn.addActionListener(e -> {
            boolean ok = attendanceService.recordTimeIn(employeeID, LocalTime.now());
            showMessage(ok ? "Success" : "Warning",
                        ok ? "Timed in successfully." : "Already timed in today.");
            refresh();
        });

        timeOutBtn.addActionListener(e -> {
            boolean ok = attendanceService.recordTimeOut(employeeID, LocalTime.now());
            showMessage(ok ? "Success" : "Warning",
                        ok ? "Timed out successfully." : "No Time In found. Please Time In first.");
            refresh();
        });

        buttons.add(timeInBtn);
        buttons.add(timeOutBtn);

        // Service decides if delete button is shown — panel just reads the boolean
        if (attendanceService.canDeleteAttendance(role)) {
            GradientButton deleteBtn = new GradientButton("Delete");
            deleteBtn.addActionListener(e -> deleteSelected());
            buttons.add(deleteBtn);
        }

        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    // ── Build table (pure display) ────────────────────────────────────────────

    private JScrollPane buildTable() {
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(pink);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(blue);
        h.setForeground(Color.WHITE);

        // Renderer applies styles — reads pre-computed values from the row, no decisions here
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (!sel) {
                    if (col == 5) {
                        // Total Hours column — fixed green background
                        c.setBackground(new Color(232, 255, 232));
                        ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 13));
                        ((JLabel) c).setForeground(Color.BLACK);
                    } else if (col == 6 && row < rowStyles.size()) {
                        // OT Status — colors come from AttendanceRow, panel just applies them
                        AttendanceRow ar = rowStyles.get(row);
                        int[] bg = ar.getStatusBackgroundRGB();
                        int[] fg = ar.getStatusForegroundRGB();
                        c.setBackground(new Color(bg[0], bg[1], bg[2]));
                        ((JLabel) c).setForeground(new Color(fg[0], fg[1], fg[2]));
                        ((JLabel) c).setFont(new Font("Segoe UI",
                                ar.isStatusBold() ? Font.BOLD : Font.PLAIN, 13));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 245, 255));
                        ((JLabel) c).setForeground(Color.BLACK);
                        ((JLabel) c).setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    // ── Build summary section skeleton (content filled by refresh()) ─────────

    private JPanel buildSummaryWrapper() {
        summaryWrapper = new JPanel(new BorderLayout(5, 5));
        summaryWrapper.setBackground(Color.WHITE);
        summaryWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        summaryTitleLabel = new JLabel("");
        summaryTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        summaryTitleLabel.setForeground(blue);

        summaryChipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        summaryChipsPanel.setBackground(Color.WHITE);

        summaryWrapper.add(summaryTitleLabel, BorderLayout.NORTH);
        summaryWrapper.add(summaryChipsPanel, BorderLayout.CENTER);
        return summaryWrapper;
    }

    // ── Refresh: asks service for data, then displays it ─────────────────────

    private void refresh() {
        populateTable();
        populateSummary();
    }

    /**
     * Asks service for ready-made rows and puts them in the table.
     * No filtering or calculation here.
     */
    private void populateTable() {
        model.setRowCount(0);
        rowStyles.clear(); // reset style lookup
        List<AttendanceRow> rows = attendanceService.getAttendanceRows(employeeID, role);
        for (AttendanceRow r : rows) {
            rowStyles.add(r); // store so renderer can read styles by row index
            model.addRow(new Object[]{
                r.getEmployeeId(),
                r.getEmployeeName(),
                r.getDate(),
                r.getTimeIn(),
                r.getTimeOut(),
                r.getTotalHours(),
                r.getOvertimeStatus()
            });
        }
    }

    /**
     * Asks service for the summary result and renders it.
     * Panel reads fields from the result object — makes no decisions.
     */
    private void populateSummary() {
        AttendanceSummaryResult result = attendanceService.getSummaryResult(employeeID, role);

        // Set title — comes from overridden getSummaryLabel() in subclass
        summaryTitleLabel.setText(result.getSummaryLabel());

        // Rebuild chips — values come from service, panel just places them
        summaryChipsPanel.removeAll();
        addChip(summaryChipsPanel, "My Total Hours", result.getMyTotalHours(), blue);

        if (result.isShowAllTotal()) {
            addChip(summaryChipsPanel, "All Employees Total", result.getAllEmployeesTotal(), green);
        }

        // Remove old calculate button if present, then add if allowed
        if (summaryWrapper.getComponentCount() > 2) {
            summaryWrapper.remove(2);
        }
        if (result.isShowCalculateBtn()) {
            GradientButton calcBtn = new GradientButton("Calculate All Employee Hours");
            calcBtn.addActionListener(e -> showBreakdownDialog(result));
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            btnRow.setBackground(Color.WHITE);
            btnRow.add(calcBtn);
            summaryWrapper.add(btnRow, BorderLayout.SOUTH);
        }

        summaryChipsPanel.revalidate();
        summaryChipsPanel.repaint();
        summaryWrapper.revalidate();
        summaryWrapper.repaint();
    }

    // ── Dialog: shows breakdown table — pure display ─────────────────────────

    private void showBreakdownDialog(AttendanceSummaryResult result) {
        String[] cols = {"Employee ID", "Employee Name", "Total Hours"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Data comes from result object — no calculation in this method
        for (EmployeeHoursRow row : result.getEmployeeBreakdown()) {
            dm.addRow(new Object[]{
                row.getEmployeeId(),
                row.getEmployeeName(),
                row.getTotalHours()
            });
        }

        JTable t = new JTable(dm);
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(blue);
        t.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(t);
        scroll.setPreferredSize(new Dimension(480, 300));

        JLabel grandTotalLabel = new JLabel("Grand Total: " + result.getAllEmployeesTotal());
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        grandTotalLabel.setForeground(green);
        grandTotalLabel.setBorder(BorderFactory.createEmptyBorder(10, 6, 4, 6));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(grandTotalLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel,
                "Total Hours Worked — All Employees", JOptionPane.PLAIN_MESSAGE);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addChip(JPanel parent, String label, String value, Color color) {
        JPanel chip = new JPanel(new BorderLayout(4, 2));
        chip.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.DARK_GRAY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));
        val.setForeground(color);
        chip.add(lbl, BorderLayout.NORTH);
        chip.add(val, BorderLayout.CENTER);
        parent.add(chip);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { showMessage("Warning", "Please select a record to delete."); return; }
        String empId = model.getValueAt(row, 0).toString();
        String date  = model.getValueAt(row, 2).toString();
        if (showConfirm("Confirm Delete", "Delete this attendance record?")) {
            attendanceService.deleteAttendance(empId, date);
            showMessage("Success", "Record deleted successfully.");
            refresh();
        }
    }

    private void showMessage(String title, String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(this);
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());
        JLabel hdr = new JLabel(title, JLabel.CENTER);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hdr.setForeground(Color.WHITE);
        hdr.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        JLabel msg = new JLabel("<html><center>" + message + "</center></html>", JLabel.CENTER);
        msg.setForeground(Color.WHITE);
        GradientButton ok = new GradientButton("OK");
        ok.addActionListener(e -> dialog.dispose());
        JPanel bp = new JPanel(); bp.setOpaque(false); bp.add(ok);
        panel.add(hdr, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(bp,  BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private boolean showConfirm(String title, String message) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(this);
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());
        JLabel hdr = new JLabel(title, JLabel.CENTER);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hdr.setForeground(Color.WHITE);
        hdr.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        JLabel msg = new JLabel("<html><center>" + message + "</center></html>", JLabel.CENTER);
        msg.setForeground(Color.WHITE);
        GradientButton yes = new GradientButton("Yes");
        GradientButton no  = new GradientButton("No");
        yes.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        no.addActionListener(e -> dialog.dispose());
        JPanel bp = new JPanel(); bp.setOpaque(false); bp.add(yes); bp.add(no);
        panel.add(hdr, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(bp,  BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
        return result[0];
    }

    // ── Inner UI classes ─────────────────────────────────────────────────────

    class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, blue, getWidth(), getHeight(), pink));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, blue, getWidth(), getHeight(), pink));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
