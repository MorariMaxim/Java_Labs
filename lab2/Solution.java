import java.util.ArrayList;

public class Solution {

    private ArrayList<Tour> tours;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Solution tours:\n");

        for (Tour t : tours) {

            result.append("\t"+t.toString() + "\n");
        }

        return result.toString();
    }

    public ArrayList<Tour> getTours() {
        return tours;
    }

    public void setTours(ArrayList<Tour> tours) {
        this.tours = tours;
    }

    
}


