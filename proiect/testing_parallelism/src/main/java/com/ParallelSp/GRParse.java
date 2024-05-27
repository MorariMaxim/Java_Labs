package com.ParallelSp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.Tools;

public class GRParse {

    public static void main(String[] args) {
        String filePath = "rome.gr"; // The file path relative to the resources directory
        String outputFilePath = "output.txt";
        parseGRFile(filePath, outputFilePath);

        Graph graph = parseGRFile(filePath);

        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Tools.printMatrix(graph.adjacencyMatrix());
    }

    public static void parseGRFile(String filePath, String outputFilePath) {
        try (InputStream inputStream = GRParse.class.getClassLoader().getResourceAsStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputFilePath)) {

            if (inputStream == null) {
                System.err.println("File not found: " + filePath);
                return;
            }

            String line;
            int numberOfVertices = 0;
            int numberOfEdges = 0;

            while ((line = br.readLine()) != null) {
                // Ignore comment lines
                if (line.startsWith("c")) {
                    continue;
                }

                // Parse problem line
                if (line.startsWith("p")) {
                    String[] parts = line.split(" ");
                    numberOfVertices = Integer.parseInt(parts[2]);
                    numberOfEdges = Integer.parseInt(parts[3]);
                    writer.println("Number of vertices: " + numberOfVertices);
                    writer.println("Number of edges: " + numberOfEdges);
                }

                // Parse edge lines
                if (line.startsWith("a")) {
                    String[] parts = line.split(" ");
                    int source = Integer.parseInt(parts[1]);
                    int target = Integer.parseInt(parts[2]);
                    double weight = Double.parseDouble(parts[3]);
                    writer.println("Edge from " + source + " to " + target + " with weight " + weight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Graph parseGRFile(String filePath) {
        Graph graph = null;

        try (InputStream inputStream = GRParse.class.getClassLoader().getResourceAsStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                System.err.println("File not found: " + filePath);
                return null;
            }

            String line;
            int numberOfVertices = 0;

            while ((line = br.readLine()) != null) {
                // Ignore comment lines
                if (line.startsWith("c")) {
                    continue;
                }

                // Parse problem line
                if (line.startsWith("p")) {
                    String[] parts = line.split(" ");
                    numberOfVertices = Integer.parseInt(parts[2]);
                    graph = GraphBuilder.empty().estimatedNumVertices(numberOfVertices).buildGraph();

                    for (int i = 0; i < numberOfVertices; i++) {
                        graph.addVertex(i);
                    }
                }

                // Parse edge lines
                if (line.startsWith("a")) {
                    String[] parts = line.split(" ");
                    int source = Integer.parseInt(parts[1]) - 1; // Adjusting for 0-based index
                    int target = Integer.parseInt(parts[2]) - 1; // Adjusting for 0-based index
                    double weight = Double.parseDouble(parts[3]);
                    graph.addEdge(source, target, weight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

}
