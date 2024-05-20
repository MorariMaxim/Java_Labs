package com.DataBase.example.persistence;

import java.util.List;
import java.util.logging.FileHandler;
import javax.persistence.Query; 

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter; 
import javax.persistence.EntityManager;

public class MyLogger {
    private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());

    public List executeJpqlStatement(Query query) {
        try {

            long startTime = System.currentTimeMillis();

            List result = query.getResultList();

            long endTime = System.currentTimeMillis();

            long executionTime = endTime - startTime;

            LOGGER.log(Level.INFO, "Execution time: " + executionTime + " milliseconds");

            LOGGER.log(Level.INFO, "JPQL statement executed successfully");

            return result;
        } catch (Exception | Error e) {

            LOGGER.log(Level.SEVERE, "Exception occurred", e);
        }
        return null;
    }

    public static void main(String[] args) {

        try {
            FileHandler fileHandler = new FileHandler("logfile.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error configuring logger", e);
        }

        MyLogger loggerInstance = new MyLogger();

        EntityManager entityManager = EntityManagerFactorySingleton.getEntityManagerFactory().createEntityManager();
        Query query = entityManager.createQuery("SELECT b FROM Book b WHERE b.title = :title");
        query.setParameter("title", "LOT");
        loggerInstance.executeJpqlStatement(query);
    }
}
