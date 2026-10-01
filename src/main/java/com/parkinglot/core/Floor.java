package com.parkinglot.core;

import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Floor {

    private final int floorNumber;
    private final ArrayList<ParkingSpot> spots;
    private final PriorityQueue<ParkingSpot> availableSpots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        this.availableSpots = new PriorityQueue<>();
    }

    public void addSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null.");
        }
        if (spot.getFloorNumber() != floorNumber) {
            throw new IllegalArgumentException("Spot floor number must match this floor.");
        }
        if (!spots.contains(spot)) {
            spots.add(spot);
            if (!spot.isOccupied()) {
                availableSpots.offer(spot);
            }
        }
    }

    public ParkingSpot findNearestAvailableSpot(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }

        PriorityQueue<ParkingSpot> candidates = new PriorityQueue<>(availableSpots);
        while (!candidates.isEmpty()) {
            ParkingSpot spot = candidates.poll();
            if (!spot.isOccupied() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }

    public void occupySpot(ParkingSpot spot) {
        validateSpot(spot);
        availableSpots.remove(spot);
        spot.occupySpot();
    }

    public void releaseSpot(ParkingSpot spot) {
        validateSpot(spot);
        spot.releaseSpot();
        if (!availableSpots.contains(spot)) {
            availableSpots.offer(spot);
        }
    }

    public int getAvailableSpotCount() {
        return availableSpots.size();
    }

    public int getTotalSpotCount() {
        return spots.size();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public java.util.List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    private void validateSpot(ParkingSpot spot) {
        if (spot == null || !spots.contains(spot)) {
            throw new IllegalArgumentException("Spot does not belong to floor " + floorNumber + ".");
        }
    }
}
