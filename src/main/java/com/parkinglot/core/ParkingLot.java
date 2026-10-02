package com.parkinglot.core;

import com.parkinglot.exception.ParkingLotFullException;
import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.vehicle.Vehicle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ParkingLot {

    private static final ParkingLot INSTANCE = new ParkingLot();

    private final TreeMap<Integer, Floor> floors;
    private final Set<String> parkedVehicleNumbers;

    private ParkingLot() {
        floors = new TreeMap<>();
        parkedVehicleNumbers = new HashSet<>();
    }

    public static ParkingLot getInstance() {
        return INSTANCE;
    }

    /**
     * Clears configured floors and parking state for package-level test setup.
     */
    void reset() {
        floors.clear();
        parkedVehicleNumbers.clear();
    }

    public void addFloor(Floor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null.");
        }
        floors.put(floor.getFloorNumber(), floor);
    }

    public ParkingSpot findNearestAvailableSpot(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        for (Floor floor : floors.values()) {
            ParkingSpot spot = floor.findNearestAvailableSpot(vehicle);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) throws ParkingLotFullException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (isVehicleParked(vehicle.getVehicleNumber())) {
            throw new IllegalArgumentException("Vehicle is already parked: " + vehicle.getVehicleNumber());
        }

        ParkingSpot spot = findNearestAvailableSpot(vehicle);
        if (spot == null) {
            throw new ParkingLotFullException("No suitable parking spot is available.");
        }

        floors.get(spot.getFloorNumber()).occupySpot(spot);
        parkedVehicleNumbers.add(vehicle.getVehicleNumber());
        return spot;
    }

    public void releaseVehicle(String vehicleNumber, ParkingSpot spot) {
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException("Vehicle number cannot be blank.");
        }
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null.");
        }

        Floor floor = floors.get(spot.getFloorNumber());
        if (floor == null) {
            throw new IllegalArgumentException("No floor exists for the supplied parking spot.");
        }
        floor.releaseSpot(spot);
        parkedVehicleNumbers.remove(vehicleNumber);
    }

    public boolean isVehicleParked(String vehicleNumber) {
        return parkedVehicleNumbers.contains(vehicleNumber);
    }

    public int getTotalAvailableSpots() {
        return floors.values().stream().mapToInt(Floor::getAvailableSpotCount).sum();
    }

    public int getTotalSpots() {
        return floors.values().stream().mapToInt(Floor::getTotalSpotCount).sum();
    }

    public Map<Integer, Floor> getFloors() {
        return Collections.unmodifiableMap(floors);
    }
}
