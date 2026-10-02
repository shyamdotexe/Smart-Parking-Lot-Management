package com.parkinglot.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstHourFreePricingTest {

    private static final double DELTA = 0.0001;

    // TC05
    @Test
    void fortyFiveMinutesIsFree() {
        assertEquals(0.0, new FirstHourFreePricing(20.0).calculateFee(45), DELTA);
    }

    @Test
    void zeroMinutesIsFree() {
        assertEquals(0.0, new FirstHourFreePricing(20.0).calculateFee(0), DELTA);
    }

    @Test
    void exactlyOneHourIsFree() {
        assertEquals(0.0, new FirstHourFreePricing(20.0).calculateFee(60), DELTA);
    }

    @Test
    void oneMinuteOverFirstHourChargesOneHour() {
        assertEquals(20.0, new FirstHourFreePricing(20.0).calculateFee(61), DELTA);
    }

    @Test
    void exactlyTwoHoursChargesOneHour() {
        assertEquals(20.0, new FirstHourFreePricing(20.0).calculateFee(120), DELTA);
    }

    @Test
    void twoHoursFifteen_chargesTwoHours() {
        assertEquals(40.0, new FirstHourFreePricing(20.0).calculateFee(135), DELTA);
    }

    @Test
    void defaultRateUsedByNoArgConstructor() {
        FirstHourFreePricing p = new FirstHourFreePricing();
        assertEquals(FirstHourFreePricing.DEFAULT_RATE_PER_HOUR, p.getRatePerHour(), DELTA);
        assertEquals(FirstHourFreePricing.DEFAULT_RATE_PER_HOUR, p.calculateFee(90), DELTA);
    }

    @Test
    void negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FirstHourFreePricing().calculateFee(-1));
    }

    @Test
    void negativeRateRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FirstHourFreePricing(-1));
    }

    @Test
    void worksThroughPayableInterface() {
        Payable p = new FirstHourFreePricing(10.0);
        assertEquals(20.0, p.calculateFee(180), DELTA);
    }
}
