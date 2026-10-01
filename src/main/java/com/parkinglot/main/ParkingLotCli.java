package com.parkinglot.main;

import com.parkinglot.core.Floor;
import com.parkinglot.core.ParkingLot;
import com.parkinglot.exception.ParkingLotFullException;
import com.parkinglot.spot.CompactSpot;
import com.parkinglot.spot.HandicappedSpot;
import com.parkinglot.spot.LargeSpot;
import com.parkinglot.spot.ParkingSpot;
import com.parkinglot.vehicle.Bike;
import com.parkinglot.vehicle.Car;
import com.parkinglot.vehicle.Truck;
import com.parkinglot.vehicle.Vehicle;

import java.util.Locale;
import java.util.Scanner;

public class ParkingLotCli {

    private final ParkingLot parkingLot;
    private final Scanner scanner;

    public ParkingLotCli() {
        parkingLot = ParkingLot.getInstance();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        ParkingLotCli cli = new ParkingLotCli();
        cli.initializeSampleParkingLot();
        cli.run();
    }

    private void initializeSampleParkingLot() {
        Floor floorOne = new Floor(1);
        floorOne.addSpot(new CompactSpot(1, 1));
        floorOne.addSpot(new LargeSpot(2, 1));
        floorOne.addSpot(new HandicappedSpot(3, 1));

        Floor floorTwo = new Floor(2);
        floorTwo.addSpot(new CompactSpot(1, 2));
        floorTwo.addSpot(new CompactSpot(2, 2));
        floorTwo.addSpot(new LargeSpot(3, 2));
        floorTwo.addSpot(new HandicappedSpot(4, 2));

        parkingLot.addFloor(floorOne);
        parkingLot.addFloor(floorTwo);
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> parkVehicle();
                case "2" -> releaseVehicle();
                case "3" -> displayParkingStatus();
                case "4" -> running = false;
                default -> System.out.println("Please enter a number from 1 to 4.");
            }
        }
        System.out.println("Thank you for using Smart Parking Lot Management.");
    }

    private void printMenu() {
        System.out.println("\n--- Smart Parking Lot ---");
        System.out.println("1. Park vehicle");
        System.out.println("2. Release vehicle");
        System.out.println("3. Display parking status");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private void parkVehicle() {
        System.out.print("Vehicle number: ");
        String vehicleNumber = scanner.nextLine().trim();
        System.out.print("Vehicle type (BIKE / CAR / TRUCK): ");
        Vehicle vehicle = createVehicle(vehicleNumber, scanner.nextLine().trim());

        if (vehicle == null) {
            System.out.println("Invalid vehicle type.");
            return;
        }

        try {
            ParkingSpot spot = parkingLot.parkVehicle(vehicle);
            System.out.printf("Parked at floor %d, spot %d.%n",
                    spot.getFloorNumber(), spot.getSpotId());
        } catch (ParkingLotFullException | IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private Vehicle createVehicle(String vehicleNumber, String vehicleType) {
        return switch (vehicleType.toUpperCase(Locale.ROOT)) {
            case "BIKE" -> new Bike(vehicleNumber);
            case "CAR" -> new Car(vehicleNumber);
            case "TRUCK" -> new Truck(vehicleNumber);
            default -> null;
        };
    }

    private void releaseVehicle() {
        System.out.print("Vehicle number: ");
        String vehicleNumber = scanner.nextLine().trim();
        System.out.print("Floor number: ");
        Integer floorNumber = readNumber();
        System.out.print("Spot ID: ");
        Integer spotId = readNumber();

        if (floorNumber == null || spotId == null) {
            System.out.println("Floor number and spot ID must be whole numbers.");
            return;
        }

        ParkingSpot spot = findSpot(floorNumber, spotId);
        if (spot == null) {
            System.out.println("No matching parking spot was found.");
            return;
        }

        try {
            parkingLot.releaseVehicle(vehicleNumber, spot);
            System.out.println("Vehicle released successfully.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private Integer readNumber() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private ParkingSpot findSpot(int floorNumber, int spotId) {
        Floor floor = parkingLot.getFloors().get(floorNumber);
        if (floor == null) {
            return null;
        }
        return floor.getSpots().stream()
                .filter(spot -> spot.getSpotId() == spotId)
                .findFirst()
                .orElse(null);
    }

    private void displayParkingStatus() {
        System.out.println("\nFloor | Spot | Type        | Status");
        System.out.println("-------------------------------------");
        parkingLot.getFloors().values().forEach(floor -> floor.getSpots().forEach(spot ->
                System.out.printf("%-5d | %-4d | %-11s | %s%n",
                        spot.getFloorNumber(), spot.getSpotId(), spot.getSpotType(),
                        spot.isOccupied() ? "OCCUPIED" : "FREE")));
        System.out.printf("Total spots: %d%n", parkingLot.getTotalSpots());
        System.out.printf("Available spots: %d%n", parkingLot.getTotalAvailableSpots());
    }
}
