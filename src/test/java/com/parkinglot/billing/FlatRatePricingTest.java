package com.parkinglot.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlatRatePricingTest {

    private static final double DELTA = 0.0001;

    @Test
    void sameFeeRegardlessOfDuration() {
        FlatRatePricing p = new FlatRatePricing(50.0);
        assertEquals(50.0, p.calculateFee(1), DELTA);
        assertEquals(50.0, p.calculateFee(135), DELTA);
        assertEquals(50.0, p.calculateFee(1440), DELTA);
    }

    @Test
    void zeroMinutesStillChargesFlatFee() {
        assertEquals(50.0, new FlatRatePricing(50.0).calculateFee(0), DELTA);
    }

    @Test
    void defaultFeeUsedByNoArgConstructor() {
        FlatRatePricing p = new FlatRatePricing();
        assertEquals(FlatRatePricing.DEFAULT_FLAT_FEE, p.getFlatFee(), DELTA);
        assertEquals(FlatRatePricing.DEFAULT_FLAT_FEE, p.calculateFee(10), DELTA);
    }

    @Test
    void customFeeIsApplied() {
        assertEquals(99.5, new FlatRatePricing(99.5).calculateFee(30), DELTA);
    }

    @Test
    void zeroFeeIsAllowed() {
        assertEquals(0.0, new FlatRatePricing(0).calculateFee(30), DELTA);
    }

    @Test
    void negativeDurationRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FlatRatePricing().calculateFee(-1));
    }

    @Test
    void negativeFeeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FlatRatePricing(-1));
    }

    @Test
    void worksThroughPayableInterface() {
        Payable p = new FlatRatePricing(40.0);
        assertEquals(40.0, p.calculateFee(500), DELTA);
    }
}
