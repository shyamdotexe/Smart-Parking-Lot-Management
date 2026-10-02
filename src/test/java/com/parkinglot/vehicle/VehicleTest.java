package com.parkinglot.vehicle;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    // Minimal concrete subclass so the abstract Vehicle can be exercised in isolation.
    private static class StubVehicle extends Vehicle {
        StubVehicle(String number, VehicleType type) {
            super(number, type);
        }
    }

    @Test
    void gettersReturnConstructorValues() {
        Vehicle v = new StubVehicle("TN01AB1234", VehicleType.CAR);
        assertEquals("TN01AB1234", v.getVehicleNumber());
        assertEquals(VehicleType.CAR, v.getVehicleType());
    }

    @Test
    void toStringShowsTypeAndNumber() {
        assertEquals("BIKE - KA05XY9999", new StubVehicle("KA05XY9999", VehicleType.BIKE).toString());
        assertEquals("TRUCK - MH12TR0001", new StubVehicle("MH12TR0001", VehicleType.TRUCK).toString());
    }

    @Test
    void vehicleTypeEnumHasThreeCategories() {
        assertEquals(3, VehicleType.values().length);
        assertNotNull(VehicleType.valueOf("BIKE"));
        assertNotNull(VehicleType.valueOf("CAR"));
        assertNotNull(VehicleType.valueOf("TRUCK"));
    }

    @Test
    void invalidVehicleTypeNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> VehicleType.valueOf("BUS"));
    }

    @Test
    void subtypesAreSubstitutableForVehicle() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new StubVehicle("A1", VehicleType.BIKE));
        vehicles.add(new StubVehicle("B2", VehicleType.CAR));
        vehicles.add(new StubVehicle("C3", VehicleType.TRUCK));
        assertEquals(VehicleType.BIKE, vehicles.get(0).getVehicleType());
        assertEquals(VehicleType.CAR, vehicles.get(1).getVehicleType());
        assertEquals(VehicleType.TRUCK, vehicles.get(2).getVehicleType());
    }

    @Test
    void vehicleNumbersCanBeTrackedInHashSet() {
        Set<String> parked = new HashSet<>();
        Vehicle v = new StubVehicle("TN01AB1234", VehicleType.CAR);
        assertTrue(parked.add(v.getVehicleNumber()));
        assertFalse(parked.add(v.getVehicleNumber()));
        assertTrue(parked.contains("TN01AB1234"));
    }
}
