public class Drone extends Vehicle {

    private int capacity;

    public Drone(String name) {
        super(name);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
