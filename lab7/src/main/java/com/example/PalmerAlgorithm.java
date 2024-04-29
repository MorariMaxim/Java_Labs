package com.example;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import java.util.*;

public class PalmerAlgorithm {
    List<Integer> circle;

    public PalmerAlgorithm(Graph graph) {

        if (respectsOreProperty(graph) == false) {
            throw new RuntimeException("Doesn't respect ore property");
        }

        int[] vertices = graph.vertices();
        final int n = vertices.length;

        List<Integer> circle = new ArrayList<>();
        for (int vertex : vertices) {
            circle.add(vertex);
        }

        while (searchAndCloseGap(circle, n, graph)) {
        }

        this.circle = circle;
    }

    public static void main(String[] args) {
        var graphbuilder = GraphBuilder.numVertices(6).addEdges("0-1,0-2,0-5,2-1,2-3,4-1,4-3,5-3,5-4");

        Graph graph = graphbuilder.buildGraph();

        PalmerAlgorithm instance = new PalmerAlgorithm(graph);

        System.out.println(instance.circle);
    }

    private static boolean searchAndCloseGap(List<Integer> circle, final int n, Graph graph) {

        Integer vi = circle.get(n - 1);

        for (int i = 0; i < n; i++) {
            Integer vN = circle.get(i);

            if (!graph.containsEdge(vi, vN)) {

                Integer uj = circle.get(n - 1);
                for (int j = 0; j < n; j++) {
                    Integer uN = circle.get(j);

                    if (!Objects.equals(vi, uj) && !Objects.equals(vN, uj) && !Objects.equals(vi, uN)
                            && !Objects.equals(vN, uN) && (graph.containsEdge(vi, uj) && graph.containsEdge(vN, uN))) {
                        reverseInCircle(circle, i, j - 1);
                        return true;
                    }
                    uj = uN;
                }
                throw new RuntimeException("Something went wrong closing a gap");
            }
            vi = vN;
        }
        return false;
    }

    private static <Integer> void reverseInCircle(List<Integer> array, int start, int end) {
        if (start < end) {
            Collections.reverse(array.subList(start, end + 1));

        } else {
            Collections.reverse(array.subList(end + 1, start));

            // start = 4, end = 1
            // 1 2 3 4
            // 1 3 2 4

        }
    }

    private boolean respectsOreProperty(Graph graph) {
        int n = graph.vertices().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (i != j && !graph.containsEdge(i, j) && (graph.degree(j) + graph.degree(i) < n))
                    return false;
            }
        }

        return true;
    }

}