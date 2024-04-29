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

        double edgeProbability = 0.0001;
        int numberOfDestinations = (int) (1 / edgeProbability);

        System.out
                .println("edge probability = " + edgeProbability + ", numberOfDestinations = " + numberOfDestinations);

        homework(5000, 5000, numberOfDestinations, false);

        bonus(5000, 5000, edgeProbability);

    }

    private static void homework(int driverCount, int passengerCount, int numberOfDestinations, boolean printCoupling) {

        List<Person> people = generateRandomPeople(passengerCount, driverCount, numberOfDestinations);
        List<Driver> drivers = filterDrivers(people);
        Set<Passenger> passengers = new TreeSet<>(Comparator.comparing(Passenger::getName));
        passengers.addAll(filterPassengers(people));
        Map<Destination, List<Person>> destinationsMap = computeDestinationsMap(people);
        List<Destination> allDestinations = computeAllDestinations(drivers);

        if (false) {
            System.out.println("Drivers (sorted by age):");
            drivers.stream().sorted(Comparator.comparing(Driver::getAge)).forEach(System.out::println);

            System.out.println("\nPassengers (sorted by name):");

            passengers.stream().forEach(passenger -> System.out.println(passenger));
        }

        if (printCoupling) {
            System.out.println("\nAll destinations that drivers pass through:");
            allDestinations.forEach(System.out::println);

            System.out.println("\nMap of destinations and people who want to go there:");
            destinationsMap.forEach((destination, persons) -> {
                System.out.println(destination.getLocation() + ": " +
                        persons.stream().map(Person::getName).collect(Collectors.joining(", ")));
            });
        }

        var solution = solveGreedy(filterPassengers(people), filterDrivers(people));

        var number = solution.stream()
                .filter((matching) -> matching.driver != null && matching.passenger != null).count();

        if (printCoupling) {
            solution.stream()
                    .filter((matching) -> matching.driver != null && matching.passenger != null)
                    .forEach(matching -> System.out.println("\t" + matching));
        }

        if (printCoupling) {
            solution.stream()
                    .filter((matching) -> matching.driver == null || matching.passenger == null)
                    .forEach(matching -> System.out.println("\t" + matching));
        }

        System.out.println("\nThe Greedy solution:");
        System.out.println(number + " matching and " + (solution.size() - number) + " lone people");

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

        System.out.println("The HopcroftKarpMaximumMatching solution: ");
        System.out.println(matching.size() + " matchings and " + (driverCount + passengerCount - matching.size() * 2)
                + " lone people");

        System.out.println("Cardinality of the maximum stable set: " + algorithm.getMaximumStableSet().size());
    }

    private static List<Person> generateRandomPeople(int passengerCount, int driverCount, int numberOfDestinations) {
        Faker faker = new Faker();
        List<Person> people = new LinkedList<>();
        Random rand = new Random();

        Set<Destination> destinationSet = new HashSet<>();

        while (destinationSet.size() != numberOfDestinations) {
            Destination destination = new Destination(faker.address().city());

            destinationSet.add(destination);
        }

        List<Destination> destinations = new ArrayList<>(destinationSet);

        for (int i = 0; i < passengerCount; i++) {

            String name = faker.name().fullName();

            int age = faker.number().numberBetween(16, 80);

            Passenger person = new Passenger(name, age, destinations.get(rand.nextInt(numberOfDestinations)));

            people.add(person);
        }

        for (int i = 0; i < driverCount; i++) {

            String name = faker.name().fullName();

            int age = faker.number().numberBetween(16, 80);

            Driver person = new Driver(name, age, destinations.get(rand.nextInt(numberOfDestinations)));

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

    private static List<DestinationMatching> solveGreedy(List<Passenger> passengers, List<Driver> drivers) {

        var destinationsMap = computeDestinationsMap(drivers);

        List<DestinationMatching> solution = new ArrayList<>();

        for (Passenger passenger : passengers) {

            DestinationMatching matching = new DestinationMatching();
            matching.setPassenger(passenger);

            var possibleDrivers = destinationsMap.get(passenger.getDestination());

            if (possibleDrivers != null) {

                if (possibleDrivers.size() > 0) {
                    Driver driver = (Driver) possibleDrivers.get(0);

                    possibleDrivers.remove(0);

                    matching.setDriver(driver);

                    drivers.remove(driver);
                }

            }

            solution.add(matching);
        }

        for (var driver : drivers) {
            DestinationMatching matching = new DestinationMatching();

            matching.setDriver(driver);

            solution.add(matching);
        }
        return solution;
    }
}

class DestinationMatching {

    Driver driver;
    Passenger passenger;

    @Override
    public String toString() {

        return "DestinationMatching for " + (driver == null ? passenger : driver).getDestination().getLocation()
                + " with "
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