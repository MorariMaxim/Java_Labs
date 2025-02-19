
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        boolean homework = false;

        if (homework) {
            LocalDate today = LocalDate.of(2024, 3, 11);
            LocalDate tomorrow = LocalDate.of(2024, 3, 12);
            LocalDate afterTomorrow = LocalDate.of(2024, 3, 13);
            LocalTime morning = LocalTime.of(8, 0, 0);
            LocalTime midday = LocalTime.of(12, 0, 0);
            LocalTime evening = LocalTime.of(18, 0, 0);

            Church church = new Church("Church1", "description of church1");
            church.setVisitInterval(today, new Pair<>(morning, midday));

            Church anotheChurch = new Church("Church2", "description of church2");
            anotheChurch.setVisitInterval(today, new Pair<>(midday, evening));

            Statue statue = new Statue("statue1", "description of statue1");
            statue.setVisitInterval(tomorrow, new Pair<>(morning, midday));

            Concert concert = new Concert("Concert1", "description of Concert", 20);
            concert.setVisitInterval(afterTomorrow, new Pair<>(morning, midday));

            Trip tr = new Trip("Iasi", LocalDate.of(2024, 3, 11), LocalDate.of(2024, 3, 18));

            tr.addAttraction(church);

            tr.addAttraction(anotheChurch);

            tr.addAttraction(statue);

            tr.addAttraction(concert);

            LocalDate day = today;

            System.out.println("Sorted by opening hour, opened on " + day);

            tr.displayVisitableNonPayable(day); 

            
            TravelPlan travelPlan = new TravelPlan();

            travelPlan.addAttractionToVisit(today, church);

            travelPlan.addAttractionToVisit(today, anotheChurch);

            travelPlan.addAttractionToVisit(tomorrow, statue);

            travelPlan.addAttractionToVisit(afterTomorrow, concert);


            System.out.println("Travel plan:");
            travelPlan.dislpayAgenda(false, null);
        } else {

            Trip trip = new Trip(7, 3, 3, 3);

            TravelPlan travelPlan = new TravelPlan();

            boolean considerSameAttractions = true;

            travelPlan.makeAgendaForTrip(trip, TravelPlan.Heuristic.STATICORDERSBYDEGREE, considerSameAttractions);

            travelPlan.dislpayAgenda(true, "Static");

            travelPlan.clearAgenda();

            travelPlan.makeAgendaForTrip(trip, TravelPlan.Heuristic.DSATUR, considerSameAttractions);

            travelPlan.dislpayAgenda(true, "Dsatur");
        }
    }
}