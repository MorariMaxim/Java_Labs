
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        LocalDate today = LocalDate.of(2024, 3, 11);
        LocalDate tomorrow = LocalDate.of(2024, 3, 12);
        LocalDate afterTomorrow = LocalDate.of(2024, 3, 13);
        LocalTime morning = LocalTime.of(8, 0, 0);
        LocalTime midday = LocalTime.of(12, 0, 0);
        LocalTime evening = LocalTime.of(18, 0, 0);
        
        Church church = new Church("Church1", "description of church1");
        church.setVisitInterval(today, new Pair<>(morning, midday));

        Church anotheChurch = new Church("Church2", "description of church2");
        anotheChurch.setVisitInterval(today, new Pair<>(midday,evening));

        Statue statue = new Statue("statue1", "description of statue1");
        statue.setVisitInterval(tomorrow, new Pair<>(morning, midday));

        Concert concert = new Concert("Concert1", "description of Concert", 20);
        concert.setVisitInterval(afterTomorrow, new Pair<>(morning, midday));

        Trip tr = new Trip("Iasi", LocalDate.of(2024, 3, 11), LocalDate.of(2024, 3, 18));

        tr.addAttraction(church);

        tr.addAttraction(anotheChurch);

        tr.addAttraction(statue);

        tr.addAttraction(concert);

        TravelPlan travelPlan = new TravelPlan();

        travelPlan.addAttractionToVisit(today, church);

        travelPlan.addAttractionToVisit(today, anotheChurch);

        travelPlan.addAttractionToVisit(tomorrow, statue);

        travelPlan.addAttractionToVisit(afterTomorrow, concert);

        travelPlan.dislpayAgenda();
    }
}