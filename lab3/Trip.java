import java.net.ConnectException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.xml.sax.helpers.LocatorImpl;

public class Trip {
    private String cityName;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    private List<Attraction> attractions = new ArrayList<>(); 

    public Trip(String cityName, LocalDate periodStart, LocalDate periodEnd) {
        this.cityName = cityName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd; 
    }

    public void addAttraction(Attraction attraction) {
        attractions.add(attraction);
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void displayVisitableNonPayable(LocalDate day) {

        attractions.stream()
                .filter(attraction -> attraction instanceof Visitable && !(attraction instanceof Payable))
                .filter(attraction -> ((Visitable)attraction).isOpenOnDay(day))
                .sorted(Comparator.comparing(attraction -> ((Visitable) attraction).getOpeningHour(day)))
                .forEach(System.out::println);
    }

    // , int churchMinTime, int churchMaxTime, int statueMinTime, int statueMaxTime,
    // int concertMinTime, int concertMaxTime
    public Trip(int days, int churchesPerDay, int statuesPerDay, int concertsPerDay) {

        periodStart = LocalDate.now();
        periodEnd = LocalDate.now().plusDays(days);

        LocalDate currentDay = periodStart;

        int churchesCounter = 0;
        int statuesCounter = 0;
        int concertsCounter = 0;

        Random random = new Random();
        while (!currentDay.isAfter(periodEnd)) {

            for (int i = 0; i < churchesPerDay; i++) {

                Church church = new Church("Church#" + churchesCounter++, "description");

                int startHour = random.nextInt(24);
                int endHour = startHour + random.nextInt(24 - startHour);

                church.addVisitingTime(currentDay, new Pair<>(LocalTime.of(startHour, 0), LocalTime.of(endHour, 0)));

                addAttraction(church);
            }

            for (int i = 0; i < concertsPerDay; i++) {

                Concert concert = new Concert("Concert#" + concertsCounter++, "description", 20);

                int startHour = random.nextInt(24);
                int endHour = startHour + random.nextInt(24 - startHour);

                concert.addVisitingTime(currentDay, new Pair<>(LocalTime.of(startHour, 0), LocalTime.of(endHour, 0)));

                addAttraction(concert);
            }

            for (int i = 0; i < statuesPerDay; i++) {

                Statue statue = new Statue("Statue#" + statuesCounter++, "description");

                int startHour = random.nextInt(24);
                int endHour = startHour + random.nextInt(24 - startHour);

                statue.addVisitingTime(currentDay, new Pair<>(LocalTime.of(startHour, 0), LocalTime.of(endHour, 0)));

                addAttraction(statue);
            }

            currentDay = currentDay.plusDays(1);
        }
    }
}