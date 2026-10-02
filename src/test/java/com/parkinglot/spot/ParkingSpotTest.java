package com.parkinglot.spot;

import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotTest {

    private static class StubVehicle extends Vehicle {
        StubVehicle(String number, VehicleType type) {
            super(number, type);
        }
    }

    // Stub spot: fits only the vehicle type it is told to accept.
    private static class StubSpot extends ParkingSpot {
        private final VehicleType accepted;

        StubSpot(int id, int floor, SpotType type, VehicleType accepted) {
            super(id, floor, type);
            this.accepted = accepted;
        }

        @Override
        public boolean canFitVehicle(Vehicle vehicle) {
            return vehicle.getVehicleType() == accepted;
        }
    }

    private ParkingSpot spot;

    @BeforeEach
    void setUp() {
        spot = new StubSpot(5, 2, SpotType.LARGE, VehicleType.CAR);
    }

    @Test
    void gettersReturnConstructorValues() {
        assertEquals(5, spot.getSpotId());
        assertEquals(2, spot.getFloorNumber());
        assertEquals(SpotType.LARGE, spot.getSpotType());
    }

    @Test
    void newSpotIsNotOccupied() {
        assertFalse(spot.isOccupied());
    }

    @Test
    void occupySpotMarksOccupied() {
        spot.occupySpot();
        assertTrue(spot.isOccupied());
    }

    @Test
    void releaseSpotMarksFree() {
        spot.occupySpot();
        spot.releaseSpot();
        assertFalse(spot.isOccupied());
    }

    @Test
    void releasingAFreeSpotKeepsItFree() {
        spot.releaseSpot();
        assertFalse(spot.isOccupied());
    }

    @Test
    void occupyReleaseCycleRepeats() {
        for (int i = 0; i < 3; i++) {
            spot.occupySpot();
            assertTrue(spot.isOccupied());
            spot.releaseSpot();
            assertFalse(spot.isOccupied());
        }
    }

    @Test
    void canFitVehicleIsResolvedPolymorphically() {
        assertTrue(spot.canFitVehicle(new StubVehicle("C1", VehicleType.CAR)));
        assertFalse(spot.canFitVehicle(new StubVehicle("B1", VehicleType.BIKE)));
        assertFalse(spot.canFitVehicle(new StubVehicle("T1", VehicleType.TRUCK)));
    }

    @Test
    void spotTypeEnumHasThreeCategories() {
        assertEquals(3, SpotType.values().length);
        assertNotNull(SpotType.valueOf("COMPACT"));
        assertNotNull(SpotType.valueOf("LARGE"));
        assertNotNull(SpotType.valueOf("HANDICAPPED"));
    }

    // ---- Comparable: floor first, then spot id ----
    @Test
    void lowerFloorComesFirst() {
        ParkingSpot a = new StubSpot(99, 0, SpotType.COMPACT, VehicleType.BIKE);
        ParkingSpot b = new StubSpot(1, 1, SpotType.COMPACT, VehicleType.BIKE);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void sameFloorOrdersBySpotId() {
        ParkingSpot a = new StubSpot(1, 3, SpotType.COMPACT, VehicleType.BIKE);
        ParkingSpot b = new StubSpot(2, 3, SpotType.COMPACT, VehicleType.BIKE);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void sameFloorAndIdCompareAsEqual() {
        ParkingSpot a = new StubSpot(4, 1, SpotType.COMPACT, VehicleType.BIKE);
        ParkingSpot b = new StubSpot(4, 1, SpotType.LARGE, VehicleType.CAR);
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void sortingProducesNearestFirstOrder() {
        List<ParkingSpot> spots = new ArrayList<>(List.of(
                new StubSpot(2, 1, SpotType.COMPACT, VehicleType.BIKE),
                new StubSpot(1, 2, SpotType.COMPACT, VehicleType.BIKE),
                new StubSpot(1, 1, SpotType.COMPACT, VehicleType.BIKE),
                new StubSpot(3, 0, SpotType.COMPACT, VehicleType.BIKE)));
        Collections.sort(spots);
        assertEquals(3, spots.get(0).getSpotId());   // floor 0
        assertEquals(1, spots.get(1).getSpotId());   // floor 1, id 1
        assertEquals(2, spots.get(2).getSpotId());   // floor 1, id 2
        assertEquals(1, spots.get(3).getSpotId());   // floor 2
    }

    // TC01 / TC06 building block: PriorityQueue yields the nearest free spot
    @Test
    void priorityQueuePollsNearestSpotAndReleasedSpotIsReallocatable() {
        PriorityQueue<ParkingSpot> free = new PriorityQueue<>();
        ParkingSpot far = new StubSpot(1, 3, SpotType.COMPACT, VehicleType.CAR);
        ParkingSpot near = new StubSpot(7, 0, SpotType.COMPACT, VehicleType.CAR);
        free.add(far);
        free.add(near);

        ParkingSpot allocated = free.poll();
        assertSame(near, allocated);
        allocated.occupySpot();
        assertTrue(allocated.isOccupied());

        allocated.releaseSpot();
        free.add(allocated);
        assertFalse(allocated.isOccupied());
        assertSame(near, free.poll());
    }
}
