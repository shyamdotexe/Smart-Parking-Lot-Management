package com.parkinglot.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HourlyPricingTest {

    private static final double DELTA = 0.0001;

    // TC03
    @Test
    void twoHoursFifteenMinutes_roundsUpToThreeHours() {
        assertEquals(60.0, new HourlyPricing(20.0).calculateFee(135), DELTA);
    }

    @Test
    void exactHourIsNotRoundedUp() {
        assertEquals(40.0, new HourlyPricing(20.0).calculateFee(120), DELTA);
    }

    @Test
    void oneMinuteChargesOneFullHour() {
        assertEquals(20.0, new HourlyPricing(20.0).calculateFee(1), DELTA);
    }

    @Test
    void sixtyOneMinutesChargesTwoHours() {
        assertEquals(40.0, new HourlyPricing(20.0).calculateFee(61), DELTA);
    }

    @Test
    void zeroMinutesIsFree() {
        assertEquals(0.0, new HourlyPricing().calculateFee(0), DELTA);
    }

    @Test
    void defaultRateIsUsedByNoArgConstructor() {
        HourlyPricing p = new HourlyPricing();
        assertEquals(HourlyPricing.DEFAULT_RATE_PER_HOUR, p.getRatePerHour(), DELTA);
        assertEquals(HourlyPricing.DEFAULT_RATE_PER_HOUR, p.calculateFee(30), DELTA);
    }

    @Test
    void customRateIsApplied() {
        assertEquals(75.0, new HourlyPricing(25.0).calculateFee(180), DELTA);
    }

    @Test
    void zeroRateIsAllowedAndAlwaysFree() {
        assertEquals(0.0, new HourlyPricing(0).calculateFee(500), DELTA);
    }

    @Test
    void negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HourlyPricing().calculateFee(-1));
    }

    @Test
    void negativeRateRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HourlyPricing(-5));
    }

    @Test
    void worksThroughPayableInterface() {
        Payable p = new HourlyPricing(10.0);
        assertEquals(30.0, p.calculateFee(150), DELTA);
    }

    @Test
    void strategiesAreInterchangeableThroughPayable() {
        Payable[] strategies = {new HourlyPricing(20), new FlatRatePricing(50), new FirstHourFreePricing(20)};
        double[] expected = {60.0, 50.0, 40.0};
        for (int i = 0; i < strategies.length; i++) {
            assertEquals(expected[i], strategies[i].calculateFee(135), DELTA);
        }
    }
}
