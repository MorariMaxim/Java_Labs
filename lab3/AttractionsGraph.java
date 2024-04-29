import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set; 

public class AttractionsGraph {

    private HashMap<Attraction, Integer> attractionIndex;
    List<Attraction> attractions;

    public List<Attraction> getAttractions() {
        return attractions;
    }

    boolean[][] matrix;
    int attractionCounter = 0;
    LocalDate day;
    boolean considerSameAttractions;

    public AttractionsGraph(LocalDate day, boolean considerSameAttractions) {
        attractionIndex = new HashMap<>();
        this.day = day;
        attractions = new ArrayList<>();
        this.considerSameAttractions = considerSameAttractions;
    }

    public void addAttraction(Attraction attraction) {

        if (!(attraction instanceof Visitable)) {
            System.err.println("an attraction is supposed to be visitable for this algorithm");
        }

        if (((Visitable) attraction).getOpeningHour(day) == null) {

            System.err.println("contrary to the usage of the algorithm, the attraction is not opened on " + day);
        }

        attractionIndex.computeIfAbsent(attraction, key -> attractionCounter++);
        attractions.add(attraction);
    }

    public void createMatrix() {

        matrix = new boolean[attractionCounter][attractionCounter];

        for (int row = 0; row < matrix.length; row++) {

            for (int col = 0; col < matrix[0].length; col++) {

                var firstTimeInterval = ((Visitable) attractions.get(row)).getInterval(day);
                var secondTimeInterval = ((Visitable) attractions.get(col)).getInterval(day);

                boolean hasEdge = visitIntervalsIntersect(firstTimeInterval, secondTimeInterval)
                        ||( (attractions.get(row)).sameAttractionType(attractions.get(col)) && 
                        !considerSameAttractions);

                matrix[row][col] = matrix[col][row] = hasEdge;
            }
        }
    }

    public static boolean visitIntervalsIntersect(Pair<LocalTime, LocalTime> first, Pair<LocalTime, LocalTime> second) {

        return !(first.second.isBefore(second.first) || second.second.isBefore(first.first));
    }

    public void printMatrix() {

        if (matrix == null)
            createMatrix();

        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Calculate maximum width for each column
        int[] maxColumnWidths = new int[numCols];
        for (int j = 0; j < numCols; j++) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < numRows; i++) {
                int length = String.valueOf(matrix[i][j] ? 1 : 0).length();
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
                System.out.printf("%-" + (maxColumnWidths[j] + 2) + "d", matrix[i][j] ? 1 : 0);
            }
            System.out.println();
        }
    }

    public int[] staticOrdersbyDegree() {
        if (matrix == null)
            createMatrix();
        int numVertices = matrix.length;

        // calculam grade
        int[] degrees = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                degrees[i] += matrix[i][j] ? 1 : 0;
            }
        }

        // sortam gradele intro lista
        List<Integer> vertices = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            vertices.add(i);
        }
        vertices.sort((v1, v2) -> degrees[v2] - degrees[v1]); // Sort by degree (descending)

        // initializam culori
        int[] colors = new int[numVertices];
        Arrays.fill(colors, -1);

        for (int vertex : vertices) {

            Set<Integer> usedColors = new HashSet<>();
            for (int neighbor = 0; neighbor < numVertices; neighbor++) {
                if (matrix[vertex][neighbor] && colors[neighbor] != -1) {
                    usedColors.add(colors[neighbor]);
                }
            }

            // asignam cea mai mica culoare
            int color;
            for (color = 0; color < numVertices; color++) {
                if (!usedColors.contains(color)) {
                    break;
                }
            }

            colors[vertex] = color;
        }

        return colors;
    }

    public int[] dsaturColoring() {

        if (matrix == null)
            createMatrix();

        int numVertices = matrix.length;
        int[] saturation = new int[numVertices];
        int[] colors = new int[numVertices];
        int[] degrees = new int[numVertices];

        Arrays.fill(colors, -1);

        PriorityQueue<Integer> pq = new PriorityQueue<>(numVertices,
                (v1, v2) -> {
                    // saturare, apoi grade
                    if (saturation[v1] != saturation[v2]) {
                        return saturation[v2] - saturation[v1];
                    } else {
                        return degrees[v2] - degrees[v1];
                    }
                });

        for (int i = 0; i < numVertices; i++) {
            saturation[i] = calculateSaturation(i, colors);
            pq.add(i);
        }

        // incepe colorarea
        while (!pq.isEmpty()) {
            int vertex = pq.poll();
            Set<Integer> usedColors = new HashSet<>();
            for (int neighbor = 0; neighbor < numVertices; neighbor++) {
                if (matrix[vertex][neighbor] && colors[neighbor] != -1) {
                    usedColors.add(colors[neighbor]);
                }
            }

            // asignam cea mai mica culoare
            int color;
            for (color = 0; color < numVertices; color++) {
                if (!usedColors.contains(color)) {
                    break;
                }
            }

            colors[vertex] = color;

            // updatam
            for (int neighbor = 0; neighbor < numVertices; neighbor++) {
                if (matrix[vertex][neighbor] && colors[neighbor] == -1) {
                    saturation[neighbor] = calculateSaturation(neighbor, colors);
                    pq.remove(neighbor);
                    pq.add(neighbor);
                }
            }
        }

        return colors;
    }

    private int calculateSaturation(int vertex, int[] colors) {
        Set<Integer> usedColors = new HashSet<>();
        for (int neighbor = 0; neighbor < matrix.length; neighbor++) {
            if (matrix[vertex][neighbor] && colors[neighbor] != -1) {
                usedColors.add(colors[neighbor]);
            }
        }
        return usedColors.size();
    }

}
