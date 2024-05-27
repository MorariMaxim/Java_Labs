package com.ParallelSp;

import org.graph4j.Graph;
import org.graph4j.alg.sp.SingleSourceShortestPath;

public class Compare {

    public static void main(String[] args) {

        Graph graph = GRParse.parseGRFile("USA-road-d.FLA.gr");

        long startTime = System.nanoTime();

        SingleSourceShortestPath shortestPath = SingleSourceShortestPath.getInstance(graph, 0);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Time taken to solve the graph: " + duration + " ms");
 
    }
}
