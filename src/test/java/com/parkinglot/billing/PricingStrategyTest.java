package com.parkinglot.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {

    private static final double DELTA = 0.0001;

    // ---- HourlyPricing ----
    @Test
    void hourly_twoHoursFifteenMinutes_roundsUpToThreeHours() {
        assertEquals(60.0, new HourlyPricing(20.0).calculateFee(135), DELTA);
    }

    @Test
    void hourly_exactHourIsNotRoundedUp() {
        assertEquals(40.0, new HourlyPricing(20.0).calculateFee(120), DELTA);
    }

    @Test
    void hourly_oneMinuteChargesOneHour() {
        assertEquals(20.0, new HourlyPricing(20.0).calculateFee(1), DELTA);
    }

    @Test
    void hourly_zeroMinutesIsFree() {
        assertEquals(0.0, new HourlyPricing().calculateFee(0), DELTA);
    }

    @Test
    void hourly_defaultRateUsed() {
        assertEquals(HourlyPricing.DEFAULT_RATE_PER_HOUR, new HourlyPricing().calculateFee(30), DELTA);
    }

    @Test
    void hourly_negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HourlyPricing().calculateFee(-1));
    }

    @Test
    void hourly_negativeRateRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HourlyPricing(-5));
    }

    // ---- FlatRatePricing ----
    @Test
    void flat_sameFeeRegardlessOfDuration() {
        FlatRatePricing p = new FlatRatePricing(50.0);
        assertEquals(50.0, p.calculateFee(1), DELTA);
        assertEquals(50.0, p.calculateFee(600), DELTA);
    }

    @Test
    void flat_defaultFee() {
        assertEquals(FlatRatePricing.DEFAULT_FLAT_FEE, new FlatRatePricing().calculateFee(10), DELTA);
    }

    @Test
    void flat_negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FlatRatePricing().calculateFee(-1));
    }

    @Test
    void flat_negativeFeeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FlatRatePricing(-1));
    }

    // ---- FirstHourFreePricing ----
    @Test
    void firstHourFree_fortyFiveMinutesIsFree() {
        assertEquals(0.0, new FirstHourFreePricing(20.0).calculateFee(45), DELTA);
    }

    @Test
    void firstHourFree_exactlyOneHourIsFree() {
        assertEquals(0.0, new FirstHourFreePricing(20.0).calculateFee(60), DELTA);
    }

    @Test
    void firstHourFree_oneMinuteOverChargesOneHour() {
        assertEquals(20.0, new FirstHourFreePricing(20.0).calculateFee(61), DELTA);
    }

    @Test
    void firstHourFree_twoHoursFifteen_chargesTwoHours() {
        assertEquals(40.0, new FirstHourFreePricing(20.0).calculateFee(135), DELTA);
    }

    @Test
    void firstHourFree_negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FirstHourFreePricing().calculateFee(-1));
    }

    @Test
    void firstHourFree_negativeRateRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FirstHourFreePricing(-1));
    }

    // ---- Strategy swapping via Payable ----
    @Test
    void strategiesAreInterchangeableThroughPayable() {
        Payable[] strategies = {new HourlyPricing(20), new FlatRatePricing(50), new FirstHourFreePricing(20)};
        double[] expected = {60.0, 50.0, 40.0};
        for (int i = 0; i < strategies.length; i++) {
            assertEquals(expected[i], strategies[i].calculateFee(135), DELTA);
        }
    }
}
