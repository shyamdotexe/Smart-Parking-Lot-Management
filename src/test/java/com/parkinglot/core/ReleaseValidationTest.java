package com.parkinglot.core;

import com.parkinglot.exception.ParkingLotFullException;
import com.parkinglot.spot.CompactSpot;
import com.parkinglot.vehicle.Bike;
import com.parkinglot.vehicle.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseValidationTest {
    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = ParkingLot.getInstance();
        parkingLot.reset();
    }

    @Test
    void invalidVehicleCannotReleaseOccupiedSpot() throws ParkingLotFullException {
        Floor floor = new Floor(1);
        CompactSpot spot = new CompactSpot(1, 1);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);
        parkingLot.parkVehicle(new Bike("TN05DB2480"));
        assertTrue(spot.isOccupied());
        assertTrue(parkingLot.isVehicleParked("TN05DB2480"));
        assertThrows(IllegalArgumentException.class, () -> parkingLot.releaseVehicle("INVALID9999", spot));
        assertTrue(spot.isOccupied());
        assertTrue(parkingLot.isVehicleParked("TN05DB2480"));
        assertFalse(parkingLot.isVehicleParked("INVALID9999"));
    }

    @Test
    void correctVehicleReleasesSpotSuccessfully() throws ParkingLotFullException {
        Floor floor = new Floor(1);
        CompactSpot spot = new CompactSpot(1, 1);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);
        parkingLot.parkVehicle(new Bike("TN05DB2480"));
        assertTrue(spot.isOccupied());
        assertTrue(parkingLot.isVehicleParked("TN05DB2480"));
        assertEquals(0, floor.getAvailableSpotCount());
        parkingLot.releaseVehicle("TN05DB2480", spot);
        assertFalse(spot.isOccupied());
        assertFalse(parkingLot.isVehicleParked("TN05DB2480"));
        assertEquals(1, floor.getAvailableSpotCount());
    }

    @Test
    void releasingFromInvalidSpotThrows() throws ParkingLotFullException {
        Floor floor = new Floor(1);
        CompactSpot spot = new CompactSpot(1, 1);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);
        parkingLot.parkVehicle(new Car("TN02CD5678"));
        CompactSpot wrongSpot = new CompactSpot(2, 1);
        floor.addSpot(wrongSpot);
        assertThrows(IllegalArgumentException.class, () -> parkingLot.releaseVehicle("TN02CD5678", wrongSpot));
    }

    @Test
    void releasingAlreadyFreeSpotThrows() {
        Floor floor = new Floor(1);
        CompactSpot spot = new CompactSpot(1, 1);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);
        assertFalse(spot.isOccupied());
        assertThrows(IllegalArgumentException.class, () -> parkingLot.releaseVehicle("SOMEVEH", spot));
    }
}