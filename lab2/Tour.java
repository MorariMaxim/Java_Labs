import java.util.ArrayList;

public class Tour {

    private Vehicle vehicle;

    private ArrayList<ClientServing> visitingOrder = new ArrayList<>();

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ArrayList<ClientServing> getVisitingOrder() {
        return visitingOrder;
    }

    public void addClient(Client c, int time) {
        visitingOrder.add(new ClientServing(c,time));
    }

    public Tour(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Tour of " + vehicle + " visits: " );

        for (ClientServing cs : visitingOrder) {

            result.append("" + cs.toString() + ", ");
        }

        return result.toString();
        
    }

    
}

class ClientServing {

    private Client client;

    private int serveTime;

    public ClientServing(Client client, int serveTime) {
        this.client = client;
        this.serveTime = serveTime;
    }

    @Override
    public String toString() {
        return client + " at " + serveTime ;
    }

    
}