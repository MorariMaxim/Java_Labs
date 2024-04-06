package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TokenGraph {

    Map<Integer, List<Integer>> graph;

    public TokenGraph(List<Token> tokens) {

        graph = new HashMap<>();

        for (Token token : tokens) {
            addEdge(graph, token.getNumber1(), token.getNumber2());
            addEdge(graph, token.getNumber2(), token.getNumber1());
        }
    }

    public List<Integer> getCycle() {
        return findCycle(graph);
    }

    private static void addEdge(Map<Integer, List<Integer>> graph, int u, int v) {
        graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
    }

    private static List<Integer> findCycle(Map<Integer, List<Integer>> graph) {
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> cycle = new ArrayList<>();

        for (Integer vertex : graph.keySet()) {
            if (!visited.contains(vertex) && (dfs(graph, vertex, -1, visited, parent, cycle))) {
                break;

            }
        }

        return cycle.isEmpty() ? null : cycle;
    }

    private static boolean dfs(Map<Integer, List<Integer>> graph, int u, int parentVertex,
            Set<Integer> visited, Map<Integer, Integer> parent, List<Integer> cycle) {
        visited.add(u);
        parent.put(u, parentVertex);

        for (int v : graph.getOrDefault(u, Collections.emptyList())) {
            if (!visited.contains(v)) {
                if (dfs(graph, v, u, visited, parent, cycle)) {
                    return true;
                }
            } else if (v != parentVertex) {

                int current = u;
                cycle.add(current);
                while (current != v) {
                    current = parent.get(current);
                    cycle.add(current);
                }
                return true;
            }
        }
        return false;
    }
}