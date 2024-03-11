import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Concert extends Attraction implements Payable, Visitable {
    private double entryFee;

    public Concert(String name, String description, double entryFee) {
        super(name, description);
        this.entryFee = entryFee;
        visitingTimetable = new HashMap<>();
    }

    public void addVisitingTime(LocalDate date, Pair<LocalTime,LocalTime> timeInterval) {
        visitingTimetable.put(date, timeInterval);
    }

    @Override
    public Map<LocalDate, Pair<LocalTime,LocalTime>> getVisitingTimetable() {
        return visitingTimetable;
    }

    @Override
    public double getEntryFee() {
        return entryFee;
    }

    
}