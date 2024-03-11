import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public interface Visitable {
    Map<LocalDate, Pair<LocalTime, LocalTime>> getVisitingTimetable();

    default LocalTime getOpeningHour(LocalDate date) {

        var temp = getVisitingTimetable().get(date);
        if (temp == null) {
            System.err.println(this.toString() + "has no entry for " + date);
            return null;}
        return temp.getFirst();        
    }

    default void setVisitInterval(LocalDate day, Pair<LocalTime, LocalTime> interval){

        getVisitingTimetable().put(day, interval);
    }

    static class VisitableComparator implements Comparator<Visitable> {
        private LocalDate day;

        @Override
        public int compare(Visitable s1, Visitable s2) {
            return s1.getOpeningHour(day).compareTo(s2.getOpeningHour(day));
        }

        public VisitableComparator(LocalDate day) {
            this.day = day;
        }

    }

}