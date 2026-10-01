package com.parkinglot.spot;

import com.parkinglot.vehicle.Vehicle;

public abstract class ParkingSpot implements Comparable<ParkingSpot> {

    private int spotId;
    private int floorNumber;
    private SpotType spotType;
    private boolean occupied;

    public ParkingSpot(int spotId, int floorNumber, SpotType spotType) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.spotType = spotType;
        this.occupied = false;
    }

    public int getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void occupySpot() {
        occupied = true;
    }

    public void releaseSpot() {
        occupied = false;
    }

    public abstract boolean canFitVehicle(Vehicle vehicle);

    @Override
    public int compareTo(ParkingSpot other) {
        if (this.floorNumber != other.floorNumber) {
            return Integer.compare(this.floorNumber, other.floorNumber);
        }

        return Integer.compare(this.spotId, other.spotId);
    }
}