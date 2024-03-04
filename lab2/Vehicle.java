public class Vehicle {

    private Depot depot;

    private String name;

    public Vehicle(String name) {
        this.name = name;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Depot getDepot() {
        return depot;
    }
}
