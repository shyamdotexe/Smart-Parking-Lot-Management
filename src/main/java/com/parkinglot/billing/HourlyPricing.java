package com.parkinglot.billing;

public class HourlyPricing implements Payable {

    public static final double DEFAULT_RATE_PER_HOUR = 20.0;

    private final double ratePerHour;

    public HourlyPricing() {
        this(DEFAULT_RATE_PER_HOUR);
    }

    public HourlyPricing(double ratePerHour) {
        if (ratePerHour < 0) {
            throw new IllegalArgumentException("Rate per hour cannot be negative");
        }
        this.ratePerHour = ratePerHour;
    }

    public double getRatePerHour() {
        return ratePerHour;
    }

    @Override
    public double calculateFee(long durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        long billableHours = (durationMinutes + 59) / 60;
        return billableHours * ratePerHour;
    }
}
