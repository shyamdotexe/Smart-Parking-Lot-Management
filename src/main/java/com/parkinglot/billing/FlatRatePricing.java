package com.parkinglot.billing;

public class FlatRatePricing implements Payable {

    public static final double DEFAULT_FLAT_FEE = 50.0;

    private final double flatFee;

    public FlatRatePricing() {
        this(DEFAULT_FLAT_FEE);
    }

    public FlatRatePricing(double flatFee) {
        if (flatFee < 0) {
            throw new IllegalArgumentException("Flat fee cannot be negative");
        }
        this.flatFee = flatFee;
    }

    public double getFlatFee() {
        return flatFee;
    }

    @Override
    public double calculateFee(long durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        return flatFee;
    }
}
