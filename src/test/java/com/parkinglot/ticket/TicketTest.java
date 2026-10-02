package com.parkinglot.ticket;

import com.parkinglot.billing.FirstHourFreePricing;
import com.parkinglot.billing.FlatRatePricing;
import com.parkinglot.billing.HourlyPricing;
import com.parkinglot.exception.InvalidTicketException;
import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.spot.SpotType;
import com.parkinglot.vehicle.Vehicle;
import com.parkinglot.vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private static final double DELTA = 0.0001;
    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

    // Minimal test doubles: the core's concrete subclasses are not part of this branch.
    private static class TestVehicle extends Vehicle {
        TestVehicle(String number) {
            super(number, VehicleType.CAR);
        }
    }

    private static class TestSpot extends ParkingSpot {
        TestSpot(int id, int floor) {
            super(id, floor, SpotType.COMPACT);
        }

        @Override
        public boolean canFitVehicle(Vehicle vehicle) {
            return true;
        }
    }

    private Vehicle car;
    private ParkingSpot spot;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        car = new TestVehicle("TN01AB1234");
        spot = new TestSpot(1, 0);
        spot.occupySpot();
        ticket = new Ticket(1, car, spot, ENTRY);
    }

    @Test
    void newTicketExposesDetailsAndIsOpen() {
        assertEquals(1, ticket.getTicketId());
        assertSame(car, ticket.getVehicle());
        assertSame(spot, ticket.getSpot());
        assertEquals(ENTRY, ticket.getEntryTime());
        assertNull(ticket.getExitTime());
        assertFalse(ticket.isSettled());
    }

    @Test
    void convenienceConstructorStampsCurrentTime() {
        LocalDateTime before = LocalDateTime.now();
        Ticket t = new Ticket(2, car, spot);
        assertFalse(t.getEntryTime().isBefore(before));
    }

    @Test
    void nullArgumentsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Ticket(3, null, spot, ENTRY));
        assertThrows(IllegalArgumentException.class, () -> new Ticket(3, car, null, ENTRY));
        assertThrows(IllegalArgumentException.class, () -> new Ticket(3, car, spot, null));
    }

    // TC03
    @Test
    void settleHourly_twoHoursFifteen_billsThreeHours() throws Exception {
        double fee = ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(135));
        assertEquals(60.0, fee, DELTA);
        assertTrue(ticket.isSettled());
        assertEquals(ENTRY.plusMinutes(135), ticket.getExitTime());
    }

    // TC05
    @Test
    void settleFirstHourFree_fortyFiveMinutes_isFree() throws Exception {
        assertEquals(0.0, ticket.settle(new FirstHourFreePricing(20.0), ENTRY.plusMinutes(45)), DELTA);
    }

    @Test
    void settleFlatRate_chargesFlatFee() throws Exception {
        assertEquals(50.0, ticket.settle(new FlatRatePricing(50.0), ENTRY.plusMinutes(300)), DELTA);
    }

    @Test
    void settleWithoutExplicitTimeUsesNow() throws Exception {
        double fee = ticket.settle(new FlatRatePricing(50.0));
        assertEquals(50.0, fee, DELTA);
        assertNotNull(ticket.getExitTime());
    }

    // TC04 (already-closed ticket)
    @Test
    void settlingTwiceThrowsInvalidTicketException() throws Exception {
        ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(60));
        assertThrows(InvalidTicketException.class,
                () -> ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(120)));
    }

    @Test
    void failedSecondSettleDoesNotChangeExitTime() throws Exception {
        ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(60));
        try {
            ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(500));
        } catch (InvalidTicketException expected) {
            // expected
        }
        assertEquals(ENTRY.plusMinutes(60), ticket.getExitTime());
    }

    @Test
    void exitBeforeEntryRejectedAndTicketStaysOpen() {
        assertThrows(IllegalArgumentException.class,
                () -> ticket.settle(new HourlyPricing(20.0), ENTRY.minusMinutes(1)));
        assertFalse(ticket.isSettled());
    }

    @Test
    void partialMinuteCountsAsFullMinute() {
        assertEquals(61, ticket.getDurationMinutes(ENTRY.plusMinutes(60).plusSeconds(1)));
        assertEquals(60, ticket.getDurationMinutes(ENTRY.plusMinutes(60)));
        assertEquals(0, ticket.getDurationMinutes(ENTRY));
    }

    @Test
    void invalidPricingDoesNotSettleTicket() {
        assertThrows(IllegalArgumentException.class, () -> ticket.settle(d -> {
            throw new IllegalArgumentException("bad");
        }, ENTRY.plusMinutes(10)));
        assertFalse(ticket.isSettled());
    }

    // TC06
    @Test
    void releasedSpotIsFreeAndReallocatable() throws Exception {
        PriorityQueue<ParkingSpot> free = new PriorityQueue<>();
        ticket.settle(new HourlyPricing(20.0), ENTRY.plusMinutes(60));
        assertTrue(spot.isOccupied());
        ticket.getSpot().releaseSpot();
        free.add(ticket.getSpot());
        assertFalse(spot.isOccupied());
        assertSame(spot, free.poll());
    }

    // TC07
    @Test
    void ticketLookupForVehicleNotParkedReturnsNull() {
        Map<String, Ticket> tickets = new HashMap<>();
        tickets.put(car.getVehicleNumber(), ticket);
        assertSame(ticket, tickets.get("TN01AB1234"));
        assertNull(tickets.get("UNKNOWN"));
    }

    @Test
    void equalsAndHashCodeBasedOnTicketId() {
        Ticket same = new Ticket(1, new TestVehicle("OTHER"), new TestSpot(9, 2), ENTRY.plusDays(1));
        Ticket different = new Ticket(2, car, spot, ENTRY);
        assertEquals(ticket, same);
        assertEquals(ticket.hashCode(), same.hashCode());
        assertNotEquals(ticket, different);
        assertNotEquals(ticket, null);
    }

    @Test
    void toStringContainsKeyDetails() throws Exception {
        String open = ticket.toString();
        assertTrue(open.contains("#1"));
        assertTrue(open.contains("TN01AB1234"));
        assertTrue(open.contains("open"));
        ticket.settle(new FlatRatePricing(), ENTRY.plusMinutes(5));
        assertTrue(ticket.toString().contains("settled"));
    }
}
