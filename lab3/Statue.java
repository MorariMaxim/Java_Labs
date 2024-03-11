import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Statue extends Attraction implements Visitable { 

    public Statue(String name, String description) {
        super(name, description);
        this.visitingTimetable = new HashMap<>();
    }

    public void addVisitingTime(LocalDate date, Pair<LocalTime,LocalTime> timeInterval) {
        visitingTimetable.put(date, timeInterval);
    }

    @Override
    public Map<LocalDate, Pair<LocalTime, LocalTime>> getVisitingTimetable() {
        return visitingTimetable;
    }

    @Override
    public boolean sameAttractionType(Attraction other) {
        return other instanceof Statue;
    }
}