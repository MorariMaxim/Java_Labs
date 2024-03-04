import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Problem {

    Solution solution;

    private Depot[] depots;

    private Client[] clients;

    public Depot[] getDepots() {
        return depots;
    }

    public void setDepots(Depot[] depots) {
        this.depots = depots;
    }

    public Client[] getClients() {
        return clients;
    }

    public void setClients(Client[] clients) {
        this.clients = clients;
    }

    public void solve() {

        int depotsNum = depots.length;
        int clientsNum = clients.length;

        if (depotsNum == 0 || clientsNum == 0) {
            System.out.println("Invalid input data");
            solution = null;
            return;
        }

        int[][] depotsToClients = new int[depotsNum][clientsNum];

        int[][] clientToClient = new int[clientsNum][clientsNum];

        HashMap<Depot, Integer> depotIndices = new HashMap<>();
        HashMap<Client, Integer> clientIndices = new HashMap<>();

        // hashmaps
        int counter = 0;
        for (Depot d : depots) {
            depotIndices.put(d, counter++);
        }

        for (Client c : clients) {
            clientIndices.put(c, counter++);
        }

        // generating time costs

        Random random = new Random();

        for (int i = 0; i < depotsNum; i++) {

            for (int j = 0; j < clientsNum; j++) {

                depotsToClients[i][j] = 10 + random.nextInt(81);

            }
        }

        for (int i = 0; i < clientsNum; i++) {

            for (int j = 0; j < clientsNum; j++) {

                clientToClient[i][j] = 10 + random.nextInt(81);

            }
        }

    }

    public Solution getSolution() {
        return solution;
    }

    public Vehicle[] getVehicles() {

        ArrayList<Vehicle> vehics = new ArrayList<>();

        for (Depot d : depots) {

            for (Vehicle v : d.getVehicles()) {

                vehics.add(v);

            }
        }

        return vehics.toArray(new Vehicle[0]);
    }
}
