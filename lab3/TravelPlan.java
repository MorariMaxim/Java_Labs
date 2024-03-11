import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class TravelPlan {

    private TreeMap<LocalDate, TreeSet<Visitable>> agenda;

    public TravelPlan() {
        agenda = new TreeMap<>();
    }

    public void addAttractionToVisit(LocalDate day, Visitable attraction) {

        agenda.computeIfAbsent(day, key -> new TreeSet<>(new Visitable.VisitableComparator(key)));
 
        agenda.get(day).add(attraction);

    }

    public void dislpayAgenda(){

        for (var pair : agenda.entrySet()) {

            System.out.println(pair.getKey());

            for ( var attraction : pair.getValue()) {

                System.out.println("\t" + attraction.toString());
            }
        }
    }
}
