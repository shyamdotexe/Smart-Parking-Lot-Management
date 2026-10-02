package com.parkinglot.ticket;

import com.parkinglot.billing.Payable;
import com.parkinglot.exception.InvalidTicketException;
import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.vehicle.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {

    private final int ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean settled;

    public Ticket(int ticketId, Vehicle vehicle, ParkingSpot spot) {
        this(ticketId, vehicle, spot, LocalDateTime.now());
    }

    public Ticket(int ticketId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        if (vehicle == null || spot == null || entryTime == null) {
            throw new IllegalArgumentException("Vehicle, spot and entry time are required");
        }
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
        this.settled = false;
    }

    public int getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public boolean isSettled() {
        return settled;
    }

    /** Stay length in minutes, with any partial minute counted as a full minute. */
    public long getDurationMinutes(LocalDateTime at) {
        if (at.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        long seconds = Duration.between(entryTime, at).getSeconds();
        return (seconds + 59) / 60;
    }

    public double settle(Payable pricing) throws InvalidTicketException {
        return settle(pricing, LocalDateTime.now());
    }

    public double settle(Payable pricing, LocalDateTime at) throws InvalidTicketException {
        if (settled) {
            throw new InvalidTicketException("Ticket " + ticketId + " has already been settled");
        }
        double fee = pricing.calculateFee(getDurationMinutes(at));
        this.exitTime = at;
        this.settled = true;
        return fee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return ticketId == ((Ticket) o).ticketId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " [" + vehicle + ", floor " + spot.getFloorNumber()
                + ", spot " + spot.getSpotId() + ", entry " + entryTime
                + (settled ? ", settled" : ", open") + "]";
    }
}
