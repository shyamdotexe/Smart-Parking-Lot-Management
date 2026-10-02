package com.src.main.java.com.parkinglot.billing;

public interface Payable {

    double calculateFee(long durationMinutes);

}