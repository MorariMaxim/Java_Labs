import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.sound.midi.Soundbank;

public class ProblemAsGrid {

    int rows;
    int cols;

    Solution solution = null;

    ArrayList<ClientNode> unservedClients = new ArrayList<>();

    int[][] costMatrix;

    int[][] shortestPathMatrix;

    int[][] occupiedPositions;

    private ArrayList<DepotNode> depots = new ArrayList<>();

    private ArrayList<ClientNode> clients = new ArrayList<>();

    public void solve() {

        //this.randomlyFillProblemAsGrid(5, 5, 0, 24 * 10, 10, 1, 3, 1, 50, 60);

        this.randomlyFillProblemAsGrid(15, 15, 0, 24 * 60, 24, 5, 3, 1, 50, 60);

        ArrayList<ClientNode> remainingClients = new ArrayList<>(clients);

        // either a client or a depot the current vehicle is at
        int currentRow = -1;
        int currentCol = -1;

        int currentTime = 0;
        // trying to find a route for each vehicle

        ArrayList<Tour> tours = new ArrayList<>();

        for (Vehicle currentVehicle : this.getVehicles()) {

            currentTime = 0;

            Tour currentTour = new Tour(currentVehicle);

            currentRow = ((DepotNode) currentVehicle.getDepot()).getRow();
            currentCol = ((DepotNode) currentVehicle.getDepot()).getCol();

            boolean keepOn = true;

            if (remainingClients.isEmpty())
                keepOn = false;

            while (keepOn) {

                // getting the closest client
                int closestClientIndex = 0;
                int minServeTime = Integer.MAX_VALUE;

                int currentIndex = 0;
                for (ClientNode c : remainingClients) {

                    int serveTime = this.getServeTime(currentRow, currentCol, currentTime, c);

                    if (serveTime < minServeTime) {
                        minServeTime = serveTime;
                        closestClientIndex = currentIndex;
                    }
                    currentIndex++;
                }

                if (minServeTime < Integer.MAX_VALUE) {

                    currentTour.addClient(remainingClients.get(closestClientIndex), minServeTime);

                    var currentposition = remainingClients.get(closestClientIndex);

                    currentCol = currentposition.getCol();

                    currentRow = currentposition.getRow();

                    remainingClients.remove(closestClientIndex);

                    currentTime = minServeTime;
                } else
                    keepOn = false;

            }

            tours.add(currentTour);
        }

        solution = new Solution();

        solution.setTours(tours);

        unservedClients = remainingClients;
    }

    public void printProblemAsGrid(boolean printMatrix, boolean printActors) {

        if (solution == null) {
            solve();
        }

        if (printMatrix) {
            System.out.println("The Cost Matrix:");
            printMatrix(costMatrix);
        }

        if (printActors) {
            System.out.println("Clients, Depots, Vehicles:");

            for (DepotNode d : depots) {
                System.out.println(d.toString() + " row,col = " + d.getRow() + "," + d.getCol());
            }

            for (ClientNode c : clients) {
                System.out.println(c.toString() + " row,col = " + c.getRow() + "," + c.getCol() + ", interval = ["
                        + c.getTimeInterval()[0] + ", " + c.getTimeInterval()[1] + "]");
            }

            for (Vehicle v : getVehicles()) {

                System.out.println(v + ", starts at depot" + v.getDepot().getName());
            }
        }

        System.out.println(solution.toString());

        System.out.println("Unserved Clients:" + unservedClients.toString());

    }

    private int getServeTime(int currentRow, int currentCol, int currentTime, ClientNode client) {
 
        int arrivalTime = currentTime
                + shortestPathMatrix[indexFromRowAndCol(currentRow, currentCol)][indexFromRowAndCol(client.getRow(),
                        client.getCol())];

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

    private void fillCostMatrix(int timeCostMin, int timeCostMax) {

        costMatrix = new int[rows * cols][rows * cols];

        for (int i = 0; i < costMatrix.length; i++) {
            Arrays.fill(costMatrix[i], -1);
        }
        Random random = new Random();

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols - 1; c++) {

                int rand = random.nextInt(timeCostMax - timeCostMin);

                costMatrix[indexFromRowAndCol(r, c)][indexFromRowAndCol(r, c + 1)] = timeCostMin
                        + rand;

                costMatrix[indexFromRowAndCol(r, c + 1)][indexFromRowAndCol(r, c)] = timeCostMin
                        + rand;
            }
        }

        for (int c = 0; c < cols; c++) {

            for (int r = 0; r < rows - 1; r++) {

                int rand = random.nextInt(timeCostMax - timeCostMin);

                costMatrix[indexFromRowAndCol(r, c)][indexFromRowAndCol(r + 1, c)] = timeCostMin
                        + rand;

                costMatrix[indexFromRowAndCol(r + 1, c)][indexFromRowAndCol(r, c)] = timeCostMin
                        + rand;
            }
        }

    }

    private int indexFromRowAndCol(int rowVal, int colVal) {

        return this.cols * rowVal + colVal;
    }

    public Solution getSolution() {
        return solution;
    }

    public Vehicle[] getVehicles() {

        ArrayList<Vehicle> vehics = new ArrayList<>();

        for (DepotNode d : depots) {

            for (Vehicle v : d.getVehicles()) {

                vehics.add(v);

            }
        }

        return vehics.toArray(new Vehicle[0]);
    }

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

    public void addClients(ClientNode... args) {

        for (ClientNode c : args) {
            boolean add = true;
            for (ClientNode other : clients) {
                if (other.equals(c)) {
                    add = false;
                    break;
                }
            }
            if (add)
                clients.add(c);
        }

    }

    public void addDepots(DepotNode... args) {
        for (DepotNode d : args) {
            boolean add = true;
            for (DepotNode other : depots) {
                if (other.equals(d)) {
                    add = false;
                    break;
                }
            }
            if (add)
                depots.add(d);
        }
    }

    public void randomlyFillProblemAsGrid(int columns, int rows, int timeIntervalStart, int timeIntervalEnd,
            int timeIntervalSections,
            int clientsPerSection, int depotsNum, int vehiclePerDepotNum, int timeCostMin, int timeCostMax) {

        this.rows = rows;
        this.cols = columns;
        int droneCount = 0;
        int truckCount = 0;
        boolean alternateVehicleType = true;

        occupiedPositions = new int[rows][cols];

        for (int i = 0; i < depotsNum; i++) {

            DepotNode tempDepot = new DepotNode("depot" + i);

            this.occupyPosition(tempDepot);

            for (int j = 0; j < vehiclePerDepotNum; j++) {
                if (alternateVehicleType) {
                    tempDepot.addVehicles(new Drone("Drone" + droneCount++));
                } else {
                    tempDepot.addVehicles(new Truck("Truck" + truckCount++));
                }
                alternateVehicleType = !alternateVehicleType;
            }

            depots.add(tempDepot);
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

                ClientNode tempClient = new ClientNode("client" + clientCount++, ClientType.REGULAR, visitIntervalStart,
                        visitIntervalEnd);

                occupyPosition(tempClient);

                clients.add(tempClient);
            }

            currentTimeStart += timeStep;

        }

        // printMatrix(occupiedPositions);

        this.fillCostMatrix(timeCostMin, timeCostMax);

        //printMatrix(costMatrix);

        shortestPathMatrix = floydWarshall(costMatrix);

        //printMatrix(shortestPathMatrix);

    }

    static int occupied = 0;

    private void occupyPosition(GridNode d) { 
        Random random = new Random();
        while (true) {

            int row = random.nextInt(rows);

            int col = random.nextInt(cols);

            if (occupiedPositions[row][col] == 0) {
                d.setCol(col);
                d.setRow(row);
                occupiedPositions[row][col] = 1;
                break;
            }
        }

        occupied++;
    }

    private static final int INF = Integer.MAX_VALUE;

    public static int[][] floydWarshall(int[][] graph) {
        int n = graph.length;
        int[][] dist = new int[n][n];

        // Initialize array
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = graph[i][j];
            }
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != -1 && dist[k][j] != -1 &&
                            ((dist[i][k] + dist[k][j] < dist[i][j]) || dist[i][j] == -1)) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

}
