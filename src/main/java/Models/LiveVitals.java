package Models;

public class LiveVitals {

    private final int patientId;

    private double heartRate = 75;
    private double respRate = 16;
    private double temperature = 36.8;
    private double spo2 = 98;
    private String bloodPressure = "120/80";

    public LiveVitals(int patientId) {
        this.patientId = patientId;
    }

    public int getPatientId() { return patientId; }

    public double getHeartRate() { return heartRate; }
    public void setHeartRate(double heartRate) { this.heartRate = heartRate; }

    public double getRespRate() { return respRate; }
    public void setRespRate(double respRate) { this.respRate = respRate; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getSpO2() { return spo2; }
    public void setSpO2(double spo2) { this.spo2 = spo2; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
}
