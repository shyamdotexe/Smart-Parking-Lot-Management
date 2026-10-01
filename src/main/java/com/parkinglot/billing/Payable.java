package com.parkinglot.billing;

public interface Payable {

    double calculateFee(long durationMinutes);

}