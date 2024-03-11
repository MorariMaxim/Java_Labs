import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

abstract class Attraction implements Comparable<Attraction> {

    protected String name;
    protected String description;  

    protected Map<LocalDate, Pair<LocalTime, LocalTime>> visitingTimetable;

    protected Attraction(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }

    @Override
    public int compareTo(Attraction other) {
        return this.name.compareTo(other.name);
    }

    public abstract boolean sameAttractionType(Attraction other);
}