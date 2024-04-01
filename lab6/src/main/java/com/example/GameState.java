package com.example;

import javafx.util.Pair;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;
import org.jgrapht.alg.matching.DenseEdmondsMaximumCardinalityMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GameState {

    int rows;
    int cols;
    NodeState[][] gridStones;
    Set<GridEdge> edges;

    Set<NodeXY> firstPlayerFront;
    Set<NodeXY> secondPlayerFront;

    boolean display = false;

    public Set<GridEdge> getEdges() {
        return edges;
    }

    Graph<Integer, DefaultEdge> graphForMatchingComputing;

    public GameState(int rows, int cols, int numOfEdges) {
        this.rows = rows;
        this.cols = cols;
        gridStones = new NodeState[rows][cols];

        Arrays.stream(gridStones).forEach(row -> Arrays.fill(row, NodeState.ISOLATED));

        this.edges = generateEdges(numOfEdges);

        graphForMatchingComputing = graphFromGridEdges(edges);

        edges.stream().forEach(edge -> {
            gridStones[edge.row1][edge.col1] = NodeState.UNOCCUPIED;
            gridStones[edge.row2][edge.col2] = NodeState.UNOCCUPIED;
        });
    }

    Set<GridEdge> generateEdges(int numOfEdges) {

        numOfEdges = numOfEdges > getMaxNumberOfEdge(rows, cols) ? getMaxNumberOfEdge(rows, cols)
                : numOfEdges;

        Set<GridEdge> edges = new HashSet<>();

        Random random = new Random(System.currentTimeMillis());

        int[] row2Shift = new int[] { -1, 0, 1, 0 };
        int[] col2Shift = new int[] { 0, 1, 0, -1 };
        while (edges.size() != numOfEdges) {

            int row1 = random.nextInt(rows);
            int col1 = random.nextInt(cols);

            int direction = random.nextInt(4);

            int row2 = row1 + row2Shift[direction];
            int col2 = col1 + col2Shift[direction];

            if (row2 == -1 || row2 == rows || col2 == -1 || col2 == cols)
                continue;

            if (row1 > row2) {
                // swap, first node will be below the first one
                row1 = row1 ^ row2;
                row2 = row1 ^ row2;
                row1 = row1 ^ row2;

                col1 = col1 ^ col2;
                col2 = col1 ^ col2;
                col1 = col1 ^ col2;
            }

            else if (col1 > col2) {
                // swap, first node will be below the first one
                row1 = row1 ^ row2;
                row2 = row1 ^ row2;
                row1 = row1 ^ row2;

                col1 = col1 ^ col2;
                col2 = col1 ^ col2;
                col1 = col1 ^ col2;
            }

            edges.add(new GridEdge(row1, col1, row2, col2));
        }

        return edges;
    }

    Graph<Integer, DefaultEdge> graphFromGridEdges(Set<GridEdge> edges) {

        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        IntStream.range(0, rows * cols).forEach(i -> graph.addVertex(i));

        edges.stream().forEach(edge -> {

            graph.addEdge(rowColToUniqueId(edge.row1, edge.col1), rowColToUniqueId(edge.row2, edge.col2));
        });

        return graph;
    }

    int rowColToUniqueId(int row, int col) {

        return row * cols + col;
    }

    int[] uniqueIdToRowCol(int id) {

        return new int[] { id / cols, id - (id / cols) * cols };
    }

    public static int getMaxNumberOfEdge(int rows, int cols) {

        System.out.println((rows - 1) * cols + (cols - 1) * rows);
        return (rows - 1) * cols + (cols - 1) * rows;
    }

    Set<NodeXY> computePlayerFront(NodeState player) {

        return edges.stream()
                .map(edge -> {
                    if (gridStones[edge.row1][edge.col1] == player
                            && gridStones[edge.row2][edge.col2] == NodeState.UNOCCUPIED) {
                        return new NodeXY(edge.row2, edge.col2);
                    } else if (gridStones[edge.row2][edge.col2] == player
                            && gridStones[edge.row1][edge.col1] == NodeState.UNOCCUPIED) {
                        return new NodeXY(edge.row1, edge.col1);
                    }
                    return null;
                })
                .filter(node -> node != null)
                .collect(Collectors.toSet());
    }

    void setGridStone(int row, int col, NodeState player) {

        gridStones[row][col] = player;
    }

    Pair<Set<Pair<Integer, Integer>>, Set<Integer>> computeMatching() {

        var components = getConnectedComponents();

        Pair<Set<DefaultEdge>, Set<Integer>> defaultResult = null;

        Pair<Set<DefaultEdge>, Set<Integer>> unperfect = null;

        for (var component : components) {

            if (component.vertexSet().size() == 1)
                continue;

            DenseEdmondsMaximumCardinalityMatching<Integer, DefaultEdge> matching = new DenseEdmondsMaximumCardinalityMatching<>(
                    component);

            Matching<Integer, DefaultEdge> maxMatching = matching.getMatching();

            Set<DefaultEdge> edgeSet = maxMatching.getEdges();

            if (display)
                System.out.println("Maximum Cardinality Matching:");
            for (DefaultEdge edge : edgeSet) {
                if (display)
                    System.out.println(graphForMatchingComputing.getEdgeSource(edge) + " - " +
                            graphForMatchingComputing.getEdgeTarget(edge));
            }

            if (edgeSet.size() * 2 == component.vertexSet().size()) {

                if (display)
                    System.out.println("is a perfect matching");
            } else {
                if (display)
                    System.out.println("is not a perfect matching");
                if (defaultResult == null || defaultResult.getKey().size() > edgeSet.size())
                    defaultResult = new Pair<>(edgeSet, component.vertexSet());

                defaultResult = new Pair<>(edgeSet, component.vertexSet());

            }

        }
        var result = unperfect == null ? defaultResult : unperfect;

        Set<Pair<Integer, Integer>> edges = new HashSet<>();
        result.getKey().stream().forEach(edge -> {
            List<Integer> vertices = Arrays.stream(edge.toString().replaceAll("[()\\s]+", "").split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            edges.add(new Pair<Integer, Integer>(vertices.get(0), vertices.get(1)));

        });

        return new Pair<Set<Pair<Integer, Integer>>, Set<Integer>>(edges, result.getValue());
    }

    List<AsSubgraph<Integer, DefaultEdge>> getConnectedComponents() {
        // Graph<Integer, DefaultEdge>
        ConnectivityInspector<Integer, DefaultEdge> inspector = new ConnectivityInspector<>(graphForMatchingComputing);

        List<Set<Integer>> connectedComponents = inspector.connectedSets();

        for (Set<Integer> componentVertices : connectedComponents) {
            AsSubgraph<Integer, DefaultEdge> subgraph = new AsSubgraph<>(
                    graphForMatchingComputing,
                    componentVertices);

            if (display)
                System.out.println("Subgraph for Component:");
            if (display)
                System.out.println(subgraph);
        }
        return connectedComponents.stream().map(component -> new AsSubgraph<>(
                graphForMatchingComputing,
                component)).collect(Collectors.toList());
    }
}

class NodeXY {

    int x;
    int y;

    public NodeXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeXY other = (NodeXY) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

    void getMatching() {

    }
}

class GridEdge implements Serializable {

    int row1, col1, row2, col2;

    public GridEdge(int row1, int col1, int row2, int col2) {
        this.row1 = row1;
        this.col1 = col1;
        this.row2 = row2;
        this.col2 = col2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row1, col1, row2, col2);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridEdge other = (GridEdge) obj;
        if (row1 != other.row1)
            return false;
        if (col1 != other.col1)
            return false;
        if (row2 != other.row2)
            return false;
        if (col2 != other.col2)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GridEdge [row1=" + row1 + ", col1=" + col1 + ", row2=" + row2 + ", col2=" + col2 + "]";
    }

}
