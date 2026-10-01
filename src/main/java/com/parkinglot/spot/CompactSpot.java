package com.parkinglot.spot;

import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.VehicleType;

public class CompactSpot extends ParkingSpot {

    public CompactSpot(int spotId, int floorNumber) {
        super(spotId, floorNumber, SpotType.COMPACT);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle != null
                && (vehicle.getVehicleType() == VehicleType.BIKE
                || vehicle.getVehicleType() == VehicleType.CAR);
    }
}
