package com.finalVersion;

import static org.junit.Assert.assertTrue;

import org.graph4j.Graph;
import org.graph4j.alg.sp.SingleSourceShortestPath;
import org.junit.Test;

import com.ParallelSp.GRParse;

/**
 * Unit test for DeltaSteppingAlgorithm.
 */
public class DeltaSteppingAlgorithmTest {

    /**
     * runs testOnGraph() on the roads graph of Rome (which contains 3353 nodes and
     * 8870 arcs)
     */

    @Test
    public void testOnRomeRoadsGraph() {

        Graph graph = GRParse.parseGRFile("rome.gr");

        testOnGraph(graph);
    }

    /**
     * runs testOnGraph() on the roads graph of NY city (which contains 264346 nodes
     * and 733846 arcs)
     */

    @Test
    public void testOnNYRoadsGraph() {

        Graph graph = GRParse.parseGRFile("ny.gr");

        testOnGraph(graph);
    }

    
    /**
     * runs testOnGraph() on the roads graph of USA city (which contains 1070376
     * nodes and 2712798 arcs)
     */

     @Test
     public void testOnUSARoadsGraph() {
 
         Graph graph = GRParse.parseGRFile("USA-road-d.FLA.gr");
 
         testOnGraph(graph);
     }

    /**
     * runs the algortithm on the given graph and checks if the computed pathWeights
     * match the ones
     * computed using the default implementation of the SSSP family problem
     */
    private void testOnGraph(Graph graph) {
        SingleSourceShortestPath shortestPath = SingleSourceShortestPath.getInstance(graph, 0);

        try {

            DeltaSteppingAlgorithm instance = new DeltaSteppingAlgorithm(graph, 0, 3);

            instance.solve();

            var pathWeights = instance.getPathWeights();
            for (var vertex : instance.getGraph().vertices())

                assertTrue(shortestPath.getPathWeight(vertex) == pathWeights[vertex]);

        } catch (Exception e) {
            System.err.println("Algorithm failed: " + e.getMessage());

        }

    }
}
