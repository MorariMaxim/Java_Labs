package com.example.bonus;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class MaximizeBoolVarSum {
    public static void main(String[] args) {

        int p = 100;

        String[] titles = { "Alice in Wonderland", "A Tale of Two Cities", "The Great Gatsby", "To Kill a Mockingbird",
                "Pride and Prejudice" };

        int[] years = { 1865, 1932, 12945, 1949, 1925 };

        int n = titles.length;
        Model model = new Model("Maximize Boolean Variable Sum");

        BoolVar[] boolVars = model.boolVarArray("boolVars", n);

        IntVar sum = model.intVar("sum", 0, n);

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (titles[i].charAt(0) != titles[j].charAt(0)) {
                    model.arithm(boolVars[i], "+", boolVars[j], "<=", 1).post();
                }
            }
        }

        for (int i = 0; i < titles.length; i++) {
            for (int j = i + 1; j < titles.length; j++) {
                if (Math.abs(years[i] - years[j]) > p) {
                    model.arithm(boolVars[i], "+", boolVars[j], "<=", 1).post();
                }
            }
        }

        model.sum(boolVars, "=", sum).post();

        model.setObjective(Model.MAXIMIZE, sum);

        while (model.getSolver().solve()) {
            System.out.println("Maximized sum of Boolean variables: " + sum.getValue());
            for (int i = 0; i < n; i++) {
                if (boolVars[i].getValue() == 1)
                    System.out.print(titles[i] + ", ");
            }
            System.out.println();
        }

    }
}
