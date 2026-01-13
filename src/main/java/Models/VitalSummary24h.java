package Models;

import java.time.LocalDateTime;

public class VitalSummary24h {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private final double meanHR;
    private final double meanRR;
    private final double meanTemp;
    private final double meanSpO2;

    public VitalSummary24h(
            LocalDateTime start,
            LocalDateTime end,
            double meanHR,
            double meanRR,
            double meanTemp,
            double meanSpO2
    ) {
        this.start = start;
        this.end = end;
        this.meanHR = meanHR;
        this.meanRR = meanRR;
        this.meanTemp = meanTemp;
        this.meanSpO2 = meanSpO2;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public double getMeanHR() { return meanHR; }
    public double getMeanRR() { return meanRR; }
    public double getMeanTemp() { return meanTemp; }
    public double getMeanSpO2() { return meanSpO2; }
}
