package com.parkinglot.core;

import com.parkinglot.exception.ParkingLotFullException;
import com.parkinglot.spot.CompactSpot;
import com.parkinglot.spot.LargeSpot;
import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.vehicle.Bike;
import com.parkinglot.vehicle.Car;
import com.parkinglot.vehicle.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotCoreTest {

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = ParkingLot.getInstance();
        parkingLot.reset();
    }

    @Test
    void compactSpotSupportsBikesAndCarsButNotTrucks() {
        CompactSpot spot = new CompactSpot(1, 10);

        assertTrue(spot.canFitVehicle(new Bike("BIKE-1")));
        assertTrue(spot.canFitVehicle(new Car("CAR-1")));
        assertFalse(spot.canFitVehicle(new Truck("TRUCK-1")));
    }

    @Test
    void largeSpotSupportsTruck() {
        LargeSpot spot = new LargeSpot(1, 11);

        assertTrue(spot.canFitVehicle(new Truck("TRUCK-2")));
    }

    @Test
    void floorSelectsNearestSuitableSpotBySpotId() {
        Floor floor = new Floor(12);
        floor.addSpot(new CompactSpot(3, 12));
        floor.addSpot(new LargeSpot(1, 12));

        ParkingSpot nearestSpot = floor.findNearestAvailableSpot(new Car("CAR-2"));

        assertEquals(1, nearestSpot.getSpotId());
    }

    @Test
    void parkingOccupiesSpotAndReleasingItMakesItAvailableAgain() throws ParkingLotFullException {
        Floor floor = new Floor(20);
        CompactSpot spot = new CompactSpot(1, 20);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);

        ParkingSpot allocatedSpot = parkingLot.parkVehicle(new Car("CAR-3"));

        assertEquals(spot, allocatedSpot);
        assertTrue(spot.isOccupied());
        assertEquals(0, floor.getAvailableSpotCount());

        parkingLot.releaseVehicle("CAR-3", spot);

        assertFalse(spot.isOccupied());
        assertEquals(1, floor.getAvailableSpotCount());
    }

    @Test
    void duplicateVehicleParkingIsRejected() throws ParkingLotFullException {
        Floor floor = new Floor(21);
        floor.addSpot(new LargeSpot(1, 21));
        parkingLot.addFloor(floor);
        Car car = new Car("CAR-DUPLICATE");

        parkingLot.parkVehicle(car);

        assertThrows(IllegalArgumentException.class, () -> parkingLot.parkVehicle(car));
    }

    @Test
    void parkingLotSearchesFloorsInAscendingOrder() throws ParkingLotFullException {
        Floor higherFloor = new Floor(31);
        higherFloor.addSpot(new CompactSpot(1, 31));
        Floor lowerFloor = new Floor(30);
        lowerFloor.addSpot(new CompactSpot(99, 30));
        parkingLot.addFloor(higherFloor);
        parkingLot.addFloor(lowerFloor);

        ParkingSpot allocatedSpot = parkingLot.parkVehicle(new Bike("BIKE-2"));

        assertEquals(30, allocatedSpot.getFloorNumber());
        assertEquals(99, allocatedSpot.getSpotId());
    }

    @Test
    void fullLotThrowsParkingLotFullException() {
        Floor floor = new Floor(40);
        LargeSpot spot = new LargeSpot(1, 40);
        floor.addSpot(spot);
        parkingLot.addFloor(floor);
        floor.occupySpot(spot);

        assertNull(parkingLot.findNearestAvailableSpot(new Truck("TRUCK-FULL")));
        assertThrows(ParkingLotFullException.class,
                () -> parkingLot.parkVehicle(new Truck("TRUCK-FULL")));
    }
}
