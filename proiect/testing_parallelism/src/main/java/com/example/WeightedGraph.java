package com.example;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.Tools;

public class WeightedGraph {

    public static void main(String[] args) {

        GraphBuilder graphBuilder = GraphBuilder.empty();

        Digraph g1 = graphBuilder.buildDigraph();

        for (int i = 0; i < 5; i++) {

            g1.addVertex(i);
        }
        g1.addEdge(0, 1, 3.0);
        g1.addEdge(0, 2, 3.0);

        Tools.printMatrix(g1.adjacencyMatrix());

        var edges = g1.edges();

        for (Edge edge : edges) {

            System.out.println(edge.weight() + ", " + edge.source() + ", " + edge.target());

        }

        for (Edge edge : g1.edgesOf(0)) {
            System.out.println(edge.weight() + ", " + edge.source() + ", " + edge.target() + ", " + edge.isDirected()
                    + ", " + edge.isDirected());
        }

    }

    public static Graph getSample() {
        int n = 7;

        Graph digraph = GraphBuilder.empty().estimatedNumVertices(n).buildGraph();

        for (int i = 0; i < n; i++) {

            digraph.addVertex(i);
        }

        digraph.addEdge(0, 1, 5.0);
        digraph.addEdge(1, 3, 3.0);
        digraph.addEdge(2, 3, 3.0);
        digraph.addEdge(3, 4, 5.0);
        digraph.addEdge(5, 6, 3.0);
        digraph.addEdge(3, 5, 3.0);
        digraph.addEdge(4, 6, 1.0);

        // Tools.printMatrix(digraph.adjacencyMatrix());

        return digraph;
    }

    public static Graph generateRandomGraph(int n, int m, double minWeight, double maxWeight) {
        // Create an empty graph with an estimated number of vertices
        Graph digraph = GraphBuilder.empty().estimatedNumVertices(n).buildGraph();

        // Add vertices
        for (int i = 0; i < n; i++) {
            digraph.addVertex(i);
        }

        // Add random edges
        Random random = new Random();
        Set<String> edgeSet = new HashSet<>();
        int edgeCount = 0;

        while (edgeCount < m) {
            int source = random.nextInt(n);
            int target = random.nextInt(n);

            // Ensure no self-loops and no duplicate edges
            String edgeKey = source + "-" + target;

            if (source != target && !edgeSet.contains(edgeKey)) {
                double weight = minWeight + (maxWeight - minWeight) * random.nextDouble();
                digraph.addEdge(source, target, weight);
                edgeSet.add(edgeKey);
                edgeSet.add(target + "-" + source);
                edgeCount++;
            }
        }

        return digraph;
    }

}
