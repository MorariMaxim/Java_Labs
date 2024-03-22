package org.example;

import com.github.javafaker.Faker;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.matching.HopcroftKarpMaximumMatching;
import org.graph4j.util.Matching;

import java.util.*;
import java.util.stream.Collectors;

public class CarpoolingApp {

    public static void main(String[] args) {

        homework();
        
        //bonus(5000, 5000, 0.00001);

    }

    private static void homework() {
        int driverCount = 10;
        int passengerCount = 10;
        int destinationCount = 5;

        List<Person> people = generateRandomPeople(passengerCount, driverCount, destinationCount);
        List<Driver> drivers = filterDrivers(people);
        Set<Passenger> passengers = new TreeSet<>(Comparator.comparing(Passenger::getName));
        passengers.addAll(filterPassengers(people));
        Map<Destination, List<Person>> destinationsMap = computeDestinationsMap(people);
        List<Destination> allDestinations = computeAllDestinations(drivers);

        System.out.println("Drivers (sorted by age):");
        drivers.stream().sorted(Comparator.comparing(Driver::getAge)).forEach(System.out::println);

        System.out.println("\nPassengers (sorted by name):");

        passengers.stream().forEach(passenger -> System.out.println(passenger));

        System.out.println("\nAll destinations that drivers pass through:");
        allDestinations.forEach(System.out::println);

        System.out.println("\nMap of destinations and people who want to go there:");
        destinationsMap.forEach((destination, persons) -> {
            System.out.println(destination.getLocation() + ": " +
                    persons.stream().map(Person::getName).collect(Collectors.joining(", ")));
        });

        var solution = solveGreedy(filterPassengers(people), filterDrivers(people));

        System.out.println("\nThe solution:");

        solution.stream().forEach(System.out::println);

        System.out.println("A total of " + driverCount + " drivers, " + passengerCount + " passengers, "
                + destinationCount + " destinations and " + solution.size() + " successfull couplings");
    }

    private static void bonus(int driverCount, int passengerCount, double edgeProbability) {

        GraphBuilder graphBuilder = GraphBuilder.numVertices(driverCount + passengerCount);

        Random rand = new Random();
        for (int driver = 0; driver < driverCount; driver++) {

            for (int passenger = driverCount; passenger < passengerCount + driverCount; passenger++) {

                boolean hasEdge = rand.nextDouble() < edgeProbability;

                if (hasEdge)
                graphBuilder.addEdge(driver, passenger);
                    
            }
        }

        Graph graph = graphBuilder.buildGraph();

        HopcroftKarpMaximumMatching algorithm = new HopcroftKarpMaximumMatching(graph);

        Matching matching = algorithm.getMatching();


        System.out.println(matching.size());
    }

    private static List<Person> generateRandomPeople(int passengerCount, int driverCount, int destinationCount) {
        Faker faker = new Faker();
        List<Person> people = new LinkedList<>();
        Random rand = new Random();

        List<Destination> destinations = new ArrayList<>(destinationCount);

        for (int i = 0; i < destinationCount; i++) {

            Destination destination = new Destination(faker.address().city());

            destinations.add(destination);
        }

        for (int i = 0; i < passengerCount; i++) {

            String name = faker.name().fullName();

            int age = faker.number().numberBetween(16, 80);

            Passenger person = new Passenger(name, age, destinations.get(rand.nextInt(destinationCount)));

            people.add(person);
        }

        for (int i = 0; i < driverCount; i++) {

            String name = faker.name().fullName();

            int age = faker.number().numberBetween(16, 80);

            Driver person = new Driver(name, age, destinations.get(rand.nextInt(destinationCount)));

            people.add(person);
        }
        return people;
    }

    private static List<Driver> filterDrivers(List<Person> people) {
        return people.stream()
                .filter(person -> person instanceof Driver)
                .map(person -> (Driver) person)
                .collect(Collectors.toList());
    }

    private static List<Passenger> filterPassengers(List<Person> people) {
        return people.stream()
                .filter(person -> person instanceof Passenger)
                .map(person -> (Passenger) person)
                .collect(Collectors.toList());
    }

    private static List<Destination> computeAllDestinations(List<Driver> drivers) {
        return drivers.stream()
                .map(Person::getDestination)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Map<Destination, List<Person>> computeDestinationsMap(List<? extends Person> people) {
        return people.stream()
                .collect(Collectors.groupingBy(Person::getDestination, Collectors.toCollection(LinkedList::new)));

    }

    private static List<Coupling> solveGreedy(List<Passenger> passengers, List<Driver> drivers) {

        var destinationsMap = computeDestinationsMap(drivers);

        List<Coupling> solution = new ArrayList<>();

        for (Passenger passenger : passengers) {

            Coupling coupling = new Coupling();
            coupling.setPassenger(passenger);

            var possibleDrivers = destinationsMap.get(passenger.getDestination());

            if (possibleDrivers != null) {

                if (possibleDrivers.size() > 0) {
                    Driver driver = (Driver) possibleDrivers.get(0);

                    possibleDrivers.remove(0);

                    coupling.setDriver(driver);

                    drivers.remove(driver);
                }

            }

            solution.add(coupling);
        }

        for (var driver : drivers) {
            Coupling coupling = new Coupling();

            coupling.setDriver(driver);

            solution.add(coupling);
        }
        return solution;
    }
}

class Coupling {

    Driver driver;
    Passenger passenger;

    @Override
    public String toString() {

        return "Coupling for " + (driver == null ? passenger : driver).getDestination().getLocation() + " with "
                + (driver == null ? "no Driver" : driver.toString()) + " and "
                + (passenger == null ? "no Passenger" : passenger.toString());
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }


    public static void printMatrix(int[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Calculate maximum width for each column
        int[] maxColumnWidths = new int[numCols];
        for (int j = 0; j < numCols; j++) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < numRows; i++) {
                int length = String.valueOf(matrix[i][j]).length();
                if (length > max) {
                    max = length;
                }
            }
            maxColumnWidths[j] = max;
        }

        // Print column indices
        System.out.print("   ");
        for (int j = 0; j < numCols; j++) {
            System.out.printf("%-" + (maxColumnWidths[j] + 2) + "d", j);
        }
        System.out.println();

        // Print rows with row indices
        for (int i = 0; i < numRows; i++) {
            System.out.printf("%-3d", i); // Row index
            for (int j = 0; j < numCols; j++) {
                System.out.printf("%-" + (maxColumnWidths[j] + 2) + "d", matrix[i][j]);
            }
            System.out.println();
        }
    }
}