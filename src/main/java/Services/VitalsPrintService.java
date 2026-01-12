package Services;

import Models.VitalRecord;
import Models.VitalRecordIO;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VitalsPrintService {

    public static void print() {

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("24-Hour Vital Report");

        job.setPrintable((graphics, pageFormat, pageIndex) -> {

            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int y = 20;
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("24-Hour Vital Signs Report", 0, y);

            y += 25;
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString(
                    "Time    HR (bpm)   RR (breaths/min)   Temp (°C)   SpO₂ (%)",
                    0, y
            );

            y += 15;
            g2.drawLine(0, y, 500, y);
            y += 15;

            List<VitalRecord> records = VitalRecordIO.loadAll();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

            for (VitalRecord r : records) {

                if (y > pageFormat.getImageableHeight() - 20) break;

                String line = String.format(
                        "%s     %6.1f        %6.1f            %5.2f        %5.1f",
                        r.getTimestamp().toLocalTime().format(fmt),
                        r.getAvgHeartRate(),
                        r.getAvgRespRate(),
                        r.getAvgTemperature(),
                        r.getAvgSpO2()
                );

                g2.drawString(line, 0, y);
                y += 14;
            }

            return Printable.PAGE_EXISTS;
        });

        try {
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Printing failed",
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
