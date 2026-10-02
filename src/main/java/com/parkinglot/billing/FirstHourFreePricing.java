package com.parkinglot.billing;

public class FirstHourFreePricing implements Payable {

    public static final double DEFAULT_RATE_PER_HOUR = 20.0;
    private static final long FREE_MINUTES = 60;

    private final double ratePerHour;

    public FirstHourFreePricing() {
        this(DEFAULT_RATE_PER_HOUR);
    }

    public FirstHourFreePricing(double ratePerHour) {
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
        if (durationMinutes <= FREE_MINUTES) {
            return 0.0;
        }
        long chargeableMinutes = durationMinutes - FREE_MINUTES;
        long billableHours = (chargeableMinutes + 59) / 60;
        return billableHours * ratePerHour;
    }
}
