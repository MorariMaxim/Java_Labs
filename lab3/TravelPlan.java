import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TravelPlan {

    private TreeMap<LocalDate, TreeSet<Visitable>> agenda = new TreeMap<>();
    //private Map<LocalDate, Set<Visitable>> agenda = new TreeMap<LocalDate, TreeSet<Visitable>>();

    public void setAgenda(TreeMap<LocalDate, TreeSet<Visitable>> agenda) {
        this.agenda = agenda;
    }

    public void clearAgenda() {
        agenda = new TreeMap<>();
    }

    public void addAttractionToVisit(LocalDate day, Visitable attraction) {

        agenda.computeIfAbsent(day, key -> new TreeSet<>(new Visitable.VisitableComparator(key)));

        agenda.get(day).add(attraction);

    }

    public void dislpayAgenda(boolean writeToFile, String fileName) {
        StringBuilder result = new StringBuilder();

        int totalAttractions = 0;
        for (var pair : agenda.entrySet()) {

            result.append(pair.getKey() + "\n");

            for (var attraction : pair.getValue()) {

                String interval = (attraction).getInterval(pair.getKey()).first.toString()
                        + " to " + (attraction).getInterval(pair.getKey()).second.toString();

                totalAttractions++;
                result.append("\t during " + interval + " " + attraction.toString() + "\n");
            }
        }

        result.append("\nA total of " + totalAttractions + " attractions to visit \n");
        if (writeToFile) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"))) {
                writer.write(result.toString());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } else {
            System.out.println(result.toString());
        }
    }

    public enum Heuristic {
        DSATUR, STATICORDERSBYDEGREE
    }

    public void makeAgendaForTrip(Trip trip, Heuristic heuristic, boolean considerSameAttractions) {

        LocalDate firstDay = trip.getPeriodStart();
        LocalDate lastDay = trip.getPeriodEnd();

        LocalDate currentDay = firstDay;

        while (!currentDay.isAfter(lastDay)) {

            AttractionsGraph attractionsGraph = new AttractionsGraph(currentDay, considerSameAttractions);

            boolean atLeastOneAttraction = false;
            for (Attraction attraction : trip.getAttractions()) {

                if (((Visitable) attraction).isOpenOnDay(currentDay)) {
                    attractionsGraph.addAttraction(attraction);
                    atLeastOneAttraction = true;
                }
            }

            if (atLeastOneAttraction) {
                int[] colours = heuristic == Heuristic.DSATUR ? attractionsGraph.dsaturColoring()
                        : attractionsGraph.staticOrdersbyDegree();

                List<Attraction> attractions = attractionsGraph.getAttractions();

                int maxFrequencyColour = findMostFrequentElement(colours);

                for (int index = 0; index < colours.length; index++) {

                    if (colours[index] == maxFrequencyColour) {

                        addAttractionToVisit(currentDay, (Visitable) attractions.get(index));
                    }
                }
            }

            currentDay = currentDay.plusDays(1);
        }
    }

    public static int findMostFrequentElement(int[] array) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        for (int num : array) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        return frequencyMap.entrySet()
                .stream()
                .max((a, b) -> a.getValue() - b.getValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }
}
