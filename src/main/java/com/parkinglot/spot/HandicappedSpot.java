package com.parkinglot.spot;

import com.parkinglot.vehicle.Vehicle;

public class HandicappedSpot extends ParkingSpot {

    public HandicappedSpot(int spotId, int floorNumber) {
        super(spotId, floorNumber, SpotType.HANDICAPPED);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        // Vehicle currently has no accessibility eligibility attribute.
        return vehicle != null;
    }
}
