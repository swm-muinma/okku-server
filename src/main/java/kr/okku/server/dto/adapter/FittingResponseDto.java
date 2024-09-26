package kr.okku.server.dto.adapter;

public class FittingResponseDto {
    private double responseTime;

    // Getters and setters
    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return "FittingResponseDto{" +
                "responseTime=" + responseTime +
                '}';
    }
}