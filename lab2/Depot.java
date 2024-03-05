import java.util.ArrayList;

public class Depot {

    private String name;

    ArrayList<Vehicle> vehicles = new ArrayList<>();

    public Depot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Depot " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Depot)) {
            return false;
        }
        Depot other = (Depot) obj;
        return name.equals(other.name);

    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addVehicles(Vehicle... args) {
        for (Vehicle v : args) {
            boolean add = true;
            for (Vehicle other : vehicles) {
                if (other.equals(v)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                vehicles.add(v);
                v.setDepot(this);
            }
        }
    }

}