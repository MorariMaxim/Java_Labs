package com.Bonus;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class Graph {

	int vertices;
	ArrayList<Integer> edge[];

	Graph(int vertices) {
		this.vertices = vertices;
		edge = new ArrayList[vertices + 1];
		for (int i = 0; i <= vertices; i++) {
			edge[i] = new ArrayList<>();
		}
	}

	void addEdge(int a, int b) {
		edge[a].add(b);
	}

	void dfs(int node, ArrayList<Integer> adj[], int dp[], boolean visited[], int[] successor) {

		visited[node] = true;

		for (int i = 0; i < adj[node].size(); i++) {
			int neighbor = adj[node].get(i);

			if (!visited[neighbor])
				dfs(neighbor, adj, dp, visited, successor);

			if (dp[node] < 1 + dp[neighbor]) {
				dp[node] = 1 + dp[neighbor];
				successor[node] = neighbor;
			}
		}
	}

	List<Integer> findLongestPath(int n) {
		ArrayList<Integer> adj[] = edge;

		int[] dp = new int[n + 1];

		int[] successor = new int[n + 1];

		boolean[] visited = new boolean[n + 1];

		for (int i = 0; i <= n; i++) {
			successor[i] = -1;
		}

		for (int i = 1; i <= n; i++) {
			if (!visited[i])
				dfs(i, adj, dp, visited, successor);
		}

		int maxLength = 0;
		int rootVertex = -1;
		for (int i = 1; i <= n; i++) {
			if (dp[i] > maxLength) {
				maxLength = dp[i];
				rootVertex = i;
			}
		}

		List<Integer> longestPath = new ArrayList<>();
		while (rootVertex != -1) {
			longestPath.add(rootVertex);
			rootVertex = successor[rootVertex];
		}
		return longestPath;
	}
}

public class Main {

	public static void main(String[] args) {
		int n = 5;
		Graph graph = new Graph(n);

		graph.addEdge(1, 2);
		graph.addEdge(1, 3);
		graph.addEdge(3, 2);
		graph.addEdge(2, 4);
		graph.addEdge(3, 4);

		List<Integer> longestPath = graph.findLongestPath(n);
		System.out.println("Longest path length: " + (longestPath.size() - 1));
		System.out.println("Vertices in the longest path: " + longestPath);
	}
}
