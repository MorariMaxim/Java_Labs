import java.time.LocalDate;
import java.util.*;

public class Trip {
    private String cityName;
    private LocalDate  periodStart;
    private LocalDate  periodEnd;
    private List<Attraction> attractions;
    private TravelPlan travevPlan;

    public Trip(String cityName, LocalDate  periodStart, LocalDate  periodEnd) {
        this.cityName = cityName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.attractions = new ArrayList<>();
    }

    public void addAttraction(Attraction attraction) {
        attractions.add(attraction);
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void displayVisitableNonPayable() {

        attractions.stream()
                .filter(attraction -> attraction instanceof Visitable && !(attraction instanceof Payable))
                .sorted(Comparator.comparing(attraction -> ((Visitable) attraction).getOpeningHour(periodStart)))
                .forEach(attraction -> System.out.println(attraction));
    }
}