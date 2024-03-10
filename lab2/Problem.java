import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.Random;
 
/**
 * Represents a problem instance for vehicle routing.
 */
public class Problem {

    /**
     * The solution to the vehicle routing problem.
     */
    Solution solution = null;

    /**
     * List of clients that have not been served yet.
     */
    ArrayList<Client> unservedClients = new ArrayList<>();

    /**
     * Map to store indices for depots and clients.
     */
    HashMap<String, Integer> indicesMap = new HashMap<>();

    /**
     * Cost matrix representing distances or times between locations.
     */
    int[][] costMatrix;

    /**
     * List of depots.
     */
    private ArrayList<Depot> depots = new ArrayList<>();

    /**
     * List of clients.
     */
    private ArrayList<Client> clients = new ArrayList<>();

    /**
     * Solves the vehicle routing problem.
     */
    public void solve() {
        // Fill the problem with random data
        this.randomlyFillProblem(0, 24 * 60, 24, 5, 3, 1, 5, 60);

        this.initDSforSolution();

        ArrayList<Client> remainingClients = new ArrayList<>(clients);

        // Initialize variables for tracking vehicle routes
        int currentPosition = -1;
        int currentTime = 0;
        ArrayList<Tour> tours = new ArrayList<>();

        for (Vehicle currentVehicle : this.getVehicles()) {
            currentTime = 0;
            Tour currentTour = new Tour(currentVehicle);
            currentPosition = indicesMap.get(currentVehicle.getDepot().toString());
            boolean keepOn = true;
            if (remainingClients.isEmpty())
                keepOn = false;
            while (keepOn) {
                // Find the closest client
                int closestClientIndex = 0;
                int minServeTime = Integer.MAX_VALUE;
                int currentIndex = 0;
                for (Client c : remainingClients) {
                    int serveTime = this.getServeTime(currentPosition, currentTime, c);
                    if (serveTime < minServeTime) {
                        minServeTime = serveTime;
                        closestClientIndex = currentIndex;
                    }
                    currentIndex++;
                }
                if (minServeTime < Integer.MAX_VALUE) {

                    currentTour.addClient(remainingClients.get(closestClientIndex), minServeTime);

                    currentPosition = indicesMap.get(remainingClients.get(closestClientIndex).toString());
                    
                    currentTime = minServeTime;

                    remainingClients.remove(closestClientIndex);                    
                    
                } else
                    keepOn = false;
            }
            tours.add(currentTour);
        }

        // Create the solution
        solution = new Solution();
        solution.setTours(tours);
        unservedClients = remainingClients;
    }

    /**
     * Prints the problem including the cost matrix and actors (clients, depots, vehicles).
     *
     * @param printMatrix  True if the cost matrix should be printed.
     * @param printActors  True if the actors (clients, depots, vehicles) should be printed.
     */
    public void printProblem(boolean printMatrix, boolean printActors) {

        if (solution == null) {
            solve();
        }

        if (printMatrix) {
            System.out.println("The Cost Matrix:");
            printMatrix(costMatrix);
        }

        if (printActors) {
            System.out.println("Clients, Depots, Vehicles:");

            for (Depot d : depots) {
                System.out.println(d.toString() + " id = " + indicesMap.get(d.toString()));
            }

            for (Client c : clients) {
                System.out.println(c.toString() + " id = " + indicesMap.get(c.toString()) + ", interval = ["
                        + c.getTimeInterval()[0] + ", " + c.getTimeInterval()[1] + "]");
            }

            for (Vehicle v : getVehicles()) {
                System.out.println(v + ", starts at id = " + indicesMap.get(v.getDepot().toString()));
            }
        }

        System.out.println(solution.toString());

        System.out.println("Unserved Clients:" + unservedClients.toString());

    }

    /**
     * Initializes data structures needed for the solution.
     *
     * @return True if initialization was successful, false otherwise.
     */
    private boolean initDSforSolution() {
        int depotsNum = depots.size();
        int clientsNum = clients.size();

        if (depotsNum == 0 || clientsNum == 0) {
            System.out.println("Invalid input data");
            solution = null;
            return false;
        }

        int counter = 0;
        for (Depot d : depots) {
            indicesMap.put(d.toString(), counter++);
        }

        for (Client c : clients) {
            indicesMap.put(c.toString(), counter++);
        }
        return true;
    }

    /**
     * Calculates the time it takes to serve a client at a specific location and time.
     *
     * @param location The current location.
     * @param currentTime The current time.
     * @param client The client to serve.
     * @return The time it takes to serve the client.
     */
    private int getServeTime(int location, int currentTime, Client client) {

        int arrivalTime = currentTime + costMatrix[location][indicesMap.get(client.toString())];

        var interval = client.getTimeInterval();

        int serveTime;

        if (arrivalTime > interval[1])
            serveTime = Integer.MAX_VALUE;
        else if (arrivalTime < interval[0])
            serveTime = interval[0];
        else
            serveTime = arrivalTime;

        return serveTime;

    }

    /**
     * Fills the cost matrix with random time costs.
     *
     * @param timeCostMin The minimum time cost.
     * @param timeCostMax The maximum time cost.
     */
    private void fillCostMatrix(int timeCostMin, int timeCostMax) {

        int depotsNum = depots.size();
        int clientsNum = clients.size();
        int n = depotsNum + clientsNum;

        costMatrix = new int[n][n];
        Random random = new Random();

        // no depot to depot
        for (int i = 0; i < depotsNum; i++) {
            for (int j = 0; j < depotsNum; j++) {
                costMatrix[i][j] = Integer.MAX_VALUE;
            }
        }

        // depot to client
        for (int i = 0; i < depotsNum; i++) {
            for (int j = depotsNum; j < n; j++) {
                costMatrix[i][j] = timeCostMin + random.nextInt(timeCostMax - timeCostMin);
            }
        }

        // no client to depot
        for (int i = depotsNum; i < n; i++) {
            for (int j = 0; j < depotsNum; j++) {
                costMatrix[i][j] = Integer.MAX_VALUE;
            }
        }

        // client to client
        for (int i = depotsNum; i < n; i++) {
            for (int j = depotsNum; j < n; j++) {
                costMatrix[i][j] = timeCostMin + random.nextInt(timeCostMax - timeCostMin);
            }
        }
    }

    /**
     * Gets the solution to the vehicle routing problem.
     *
     * @return The solution.
     */
    public Solution getSolution() {
        return solution;
    }

    /**
     * Gets an array of vehicles from the depots.
     *
     * @return An array of vehicles.
     */
    public Vehicle[] getVehicles() {

        ArrayList<Vehicle> vehics = new ArrayList<>();

        for (Depot d : depots) {

            for (Vehicle v : d.getVehicles()) {

                vehics.add(v);

            }
        }

        return vehics.toArray(new Vehicle[0]);
    }

    /**
     * Prints a matrix to the console.
     *
     * @param matrix The matrix to print.
     */
    public static void printMatrix(int[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Calculate maximum width for each column
        int[] maxColumnWidths = new int[numCols];
        for (int j = 0; j < numCols; j++) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < numRows; i++) {
                int length = String.valueOf(matrix[i][j]).length();
                if (length > max) {
                    max = length;
                }
            }
            maxColumnWidths[j] = max;
        }

        // Print column indices
        System.out.print("   ");
        for (int j = 0; j < numCols; j++) {
            System.out.printf("%-" + (maxColumnWidths[j] + 2) + "d", j);
        }
        System.out.println();

        // Print rows with row indices
        for (int i = 0; i < numRows; i++) {
            System.out.printf("%-3d", i); // Row index
            for (int j = 0; j < numCols; j++) {
                System.out.printf("%-" + (maxColumnWidths[j] + 2) + "d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Adds clients to the problem instance.
     *
     * @param args The clients to add.
     */
    public void addClients(Client... args) {

        for (Client c : args) {
            boolean add = true;
            for (Client other : clients) {
                if (other.equals(c)) {
                    add = false;
                    break;
                }
            }
            if (add)
                clients.add(c);
        }

    }

    /**
     * Adds depots to the problem instance.
     *
     * @param args The depots to add.
     */
    public void addDepots(Depot... args) {
        for (Depot d : args) {
            boolean add = true;
            for (Depot other : depots) {
                if (other.equals(d)) {
                    add = false;
                    break;
                }
            }
            if (add)
                depots.add(d);
        }
    }

    /**
     * Randomly fills the problem with clients, depots, and assigns random time costs.
     *
     * @param timeIntervalStart     Start of the time interval.
     * @param timeIntervalEnd       End of the time interval.
     * @param timeIntervalSections  Number of time interval sections.
     * @param clientsPerSection     Number of clients per time interval section.
     * @param depotsNum             Number of depots.
     * @param vehiclePerDepotNum    Number of vehicles per depot.
     * @param timeCostMin           Minimum time cost.
     * @param timeCostMax           Maximum time cost.
     */
    public void randomlyFillProblem(int timeIntervalStart, int timeIntervalEnd, int timeIntervalSections,
                                    int clientsPerSection, int depotsNum, int vehiclePerDepotNum,
                                    int timeCostMin, int timeCostMax) {

        int droneCount = 0;
        int truckCount = 0;
        boolean alternateVehicleType = true;
        for (int i = 0; i < depotsNum; i++) {

            Depot temp = new Depot("depot" + i);

            for (int j = 0; j < vehiclePerDepotNum; j++) {
                if (alternateVehicleType) {
                    temp.addVehicles(new Drone("Drone" + droneCount++));
                } else {
                    temp.addVehicles(new Truck("Truck" + truckCount++));
                }
                alternateVehicleType = !alternateVehicleType;
            }

            depots.add(temp);
        }

        int timeStep = (timeIntervalEnd - timeIntervalStart) / timeIntervalSections;

        if (timeStep < 0) {
            System.out.println("error input");
            return;
        }

        int currentTimeStart = 0;
        int clientCount = 0;
        Random random = new Random();
        for (int ts = 0; ts < timeIntervalSections; ts++) {

            for (int c = 0; c < clientsPerSection; c++) {

                int tempRandNum = random.nextInt(timeStep - timeStep / 10);
                int visitIntervalStart = currentTimeStart + tempRandNum;
                int visitIntervalEnd = visitIntervalStart + random.nextInt(timeStep - tempRandNum);

                Client tempClient = new Client("client" + clientCount++, ClientType.REGULAR, visitIntervalStart,
                        visitIntervalEnd);

                clients.add(tempClient);
            }

            currentTimeStart += timeStep;

        }

        this.fillCostMatrix(timeCostMin, timeCostMax);
    }

}
