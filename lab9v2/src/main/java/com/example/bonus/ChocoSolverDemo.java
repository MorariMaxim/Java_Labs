package com.example.bonus;

import com.example.model.*;
import com.example.persistence.EntityManagerFactorySingleton;
import com.example.repository.Repository;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChocoSolverDemo {
    public static void main(String[] args) {

        /*
         * int p = 100;
         * 
         * String[] titles = { "Alice in Wonderland", "A Tale of Two Cities",
         * "The Great Gatsby", "To Kill a Mockingbird",
         * "Pride and Prejudice" };
         * 
         * int[] years = { 1865, 1932, 12945, 1949, 1925 };
         * 
         * List<Book> books = new ArrayList<>();
         * 
         * for (int i = 0; i < titles.length; i++) {
         * 
         * books.add(new Book(titles[i], getDateFromYear(years[i])));
         * }
         */

        Repository<Book> bookRepository = new Repository<>(EntityManagerFactorySingleton.getEntityManagerFactory(),
                Book.class);

        List<Book> foundBooks = bookRepository.findByName("A%");

        System.out.println(solve(foundBooks, 1, 100));

    }

    public static List<Book> solve(List<Book> books, int k, int p) {

        int n = books.size();
        Model model = new Model("Maximize Boolean Variable Sum");

        BoolVar[] boolVars = model.boolVarArray("boolVars", n);

        IntVar sum = model.intVar("sum", 0, n);

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (books.get(i).getTitle().charAt(0) != books.get(j).getTitle().charAt(0)) {
                    model.arithm(boolVars[i], "+", boolVars[j], "<=", 1).post();
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(getYear(books.get(i)) - getYear(books.get(j))) > p) {
                    model.arithm(boolVars[i], "+", boolVars[j], "<=", 1).post();
                }
            }
        }

        model.sum(boolVars, "=", sum).post();

        model.arithm(sum, ">=", k).post();

        model.setObjective(Model.MAXIMIZE, sum);

        List<Book> resulList = new ArrayList<>();

        if (model.getSolver().solve()) {
            for (int i = 0; i < n; i++) {
                if (boolVars[i].getValue() == 1) {
                    resulList.add(books.get(i));
                }
            }
        }

        return resulList;
    }

    static int getYear(Book book) {

        LocalDate localDate = book.getPublication().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int year = localDate.getYear();

        return year;
    }

    public static Date getDateFromYear(int year) {

        return Date.from(LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    }
}
