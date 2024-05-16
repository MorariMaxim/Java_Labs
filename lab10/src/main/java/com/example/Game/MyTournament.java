package com.example.Game;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.EdgeIterator;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.eq.*;
import org.graph4j.support.Tournament;
import org.graph4j.util.VertexSet;

public class MyTournament {

    static BoolVar[][][] getSchedule(int n, int p, int d) {

        Model model = new Model("Tournamnt");

        BoolVar[][][] schedule = new BoolVar[d][n][n];

        for (int i = 0; i < d; ++i) {
            schedule[i] = model.boolVarMatrix("", n, n);

            for (int r = 0; r < n; r++) {

                for (int c = r + 1; c < n; c++) {

                    // symmetry
                    model.arithm(schedule[i][r][c], "=", schedule[i][c][r]).post();

                }

                // <=p games per day
                model.sum(schedule[i][r], "<=", p).post();

                // shouldn't paly with himself
                model.arithm(schedule[i][r][r], "=", 0).post();
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // shouldn't paly with himself
                if (i == j)
                    continue;
                BoolVar[] sumVars = new BoolVar[d];
                for (int k = 0; k < d; k++) {
                    sumVars[k] = schedule[k][i][j];
                }

                // only one match across all days for each pair
                model.sum(sumVars, "=", 1).post();
            }
        }

        if (model.getSolver().solve()) {

            for (int i = 0; i < d; ++i) {
                System.out.println(stringFromMatrix(schedule[i]));
            }

            return schedule;
        }

        return null;
    }

    static String stringFromMatrix(BoolVar[][] matrix) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                stringBuilder.append(matrix[i][j].getValue() + " ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    static Digraph digraphFromSchedule(BoolVar[][][] schedule) {

        int n = schedule[0][0].length;
        int days = schedule.length;

        GraphBuilder graphBuilder = GraphBuilder.numVertices(n);

        for (int d = 0; d < days; ++d) {

            for (int p1 = 0; p1 < n - 1; ++p1) {

                for (int p2 = p1 + 1; p2 < n; ++p2) {

                    if (schedule[d][p1][p2].getValue() == 1) {

                        graphBuilder.addEdge(p1, p2);
                    }
                }

            }
        }

        EdgeIterator a = graphBuilder.buildDigraph().edgeIterator();

        while (a.hasNext()) {

            System.out.println(a.next());
        }

        return graphBuilder.buildDigraph();
    }

    public static void main(String[] args) {

        Digraph digraph = digraphFromSchedule(getSchedule(3, 2, 2));

        Tournament tournament = new Tournament(digraph);

        System.out.println(tournament.getHamiltonianPath());
    }
}
