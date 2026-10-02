package com.parkinglot.spot;

import com.parkinglot.vehicle.Vehicle;

public class LargeSpot extends ParkingSpot {

    public LargeSpot(int spotId, int floorNumber) {
        super(spotId, floorNumber, SpotType.LARGE);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle != null;
    }
}
