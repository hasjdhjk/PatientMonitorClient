package Services;

import Models.Patients.Patient;
import Models.Vitals.VitalRecord;
import Models.Vitals.VitalRecordIO;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VitalsPdfReportService {

    public static void exportPdfForLastHours(Patient patient, int hours) {

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Patient Vital Summary Report");

        job.setPrintable((g, pf, pageIndex) -> {

            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            /* =====================================================
               FIX 1: AUTO SCALE TO FIT PAGE HEIGHT (ONE PAGE ONLY)
               ===================================================== */

            double pageHeight = pf.getImageableHeight();

            // Estimated total content height of your layout (px)
            // This is intentionally generous
            double contentHeightEstimate = 1100;

            double scale = Math.min(1.0, pageHeight / contentHeightEstimate);
            g2.scale(scale, scale);

            /* ===================================================== */

            int pageWidth = (int) (pf.getImageableWidth() / scale);
            int y = 40;

            // theme
            Color textPrimary = new Color(30, 41, 59);
            Color textMuted = new Color(100, 116, 139);
            Color blue = new Color(37, 99, 235);
            Color border = new Color(220, 225, 230);
            Color bgCard = new Color(248, 250, 252);

            /* ================= TITLE ================= */
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.setColor(textPrimary);
            g2.drawString("Summary Report", 0, y);
            y += 48;

            /* ================= META ================= */
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(textMuted);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            g2.drawString("Generated: " + LocalDateTime.now().format(fmt), 0, y);
            y += 20;
            g2.drawString("Range: Last " + hours + " hours", 0, y);
            y += 36;

            /* ================= OVERVIEW CARD ================= */
            int overviewHeight = 140;
            drawCard(g2, 0, y, pageWidth, overviewHeight, bgCard, border);

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.setColor(blue);
            g2.drawString("Overview", 24, y + 38);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(textPrimary);

            drawWrappedText(
                    g2,
                    "This report summarizes the aggregated vital signs captured by the Patient Monitor client.",
                    24,
                    y + 78,
                    pageWidth - 48,
                    22
            );

            y += overviewHeight + 40;

            /* ================= LOAD DATA ================= */
            List<VitalRecord> records =
                    VitalRecordIO.loadLastHours(patient.getId(), hours);

            if (records.isEmpty()) {
                g2.drawString("No data available for this time window.", 0, y);
                return Printable.PAGE_EXISTS;
            }

            double hrMean = records.stream().mapToDouble(VitalRecord::getAvgHeartRate).average().orElse(0);
            double hrMin = records.stream().mapToDouble(VitalRecord::getAvgHeartRate).min().orElse(0);
            double hrMax = records.stream().mapToDouble(VitalRecord::getAvgHeartRate).max().orElse(0);

            double rrMean = records.stream().mapToDouble(VitalRecord::getAvgRespRate).average().orElse(0);
            double rrMin = records.stream().mapToDouble(VitalRecord::getAvgRespRate).min().orElse(0);
            double rrMax = records.stream().mapToDouble(VitalRecord::getAvgRespRate).max().orElse(0);

            double tMean = records.stream().mapToDouble(VitalRecord::getAvgTemperature).average().orElse(0);
            double tMin = records.stream().mapToDouble(VitalRecord::getAvgTemperature).min().orElse(0);
            double tMax = records.stream().mapToDouble(VitalRecord::getAvgTemperature).max().orElse(0);

            double sMean = records.stream().mapToDouble(VitalRecord::getAvgSpO2).average().orElse(0);
            double sMin = records.stream().mapToDouble(VitalRecord::getAvgSpO2).min().orElse(0);
            double sMax = records.stream().mapToDouble(VitalRecord::getAvgSpO2).max().orElse(0);

            /* ================= PATIENT SUMMARY CARD ================= */
            int vitalsHeight = 600;
            drawCard(g2, 0, y, pageWidth, vitalsHeight, bgCard, border);

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.setColor(textPrimary);
            g2.drawString("Patient Vital Summary", 24, y + 38);

            g2.setFont(new Font("Arial", Font.BOLD, 17));
            g2.drawString("Patient: " + patient.getName(), 24, y + 72);

            int py = y + 118;

            py = drawVitalBlock(g2, "Heart Rate", "bpm", hrMean, hrMin, hrMax, 24, py, textPrimary, textMuted);
            py = drawVitalBlock(g2, "Respiratory Rate", "/min", rrMean, rrMin, rrMax, 24, py, textPrimary, textMuted);
            py = drawVitalBlock(g2, "Temperature", "°C", tMean, tMin, tMax, 24, py, textPrimary, textMuted);
            py = drawVitalBlock(g2, "SpO₂", "%", sMean, sMin, sMax, 24, py, textPrimary, textMuted);

            y += vitalsHeight + 36;

            /* ================= FOOTER ================= */
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.setColor(textMuted);
            g2.drawString("Generated by Patient Monitor Client", 0, y);

            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* ================= HELPERS ================= */

    private static void drawCard(Graphics2D g, int x, int y, int w, int h, Color bg, Color border) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 18, 18);
        g.setColor(border);
        g.drawRoundRect(x, y, w, h, 18, 18);
    }

    private static int drawVitalBlock(
            Graphics2D g,
            String title,
            String unit,
            double mean,
            double min,
            double max,
            int x,
            int y,
            Color primary,
            Color muted
    ) {
        g.setFont(new Font("Arial", Font.BOLD, 17));
        g.setColor(primary);
        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(muted);
        y += 22;
        g.drawString(String.format("Mean: %.1f %s", mean, unit), x + 20, y);
        y += 20;
        g.drawString(String.format("Min:  %.1f %s", min, unit), x + 20, y);
        y += 20;
        g.drawString(String.format("Max:  %.1f %s", max, unit), x + 20, y);

        g.setColor(new Color(230, 235, 240));
        g.drawLine(x, y + 12, x + 520, y + 12);

        return y + 44;
    }

    private static void drawWrappedText(
            Graphics2D g,
            String text,
            int x,
            int y,
            int width,
            int lineHeight
    ) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (fm.stringWidth(line + word) > width) {
                g.drawString(line.toString(), x, y);
                line = new StringBuilder(word).append(" ");
                y += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }
        g.drawString(line.toString(), x, y);
    }
}
