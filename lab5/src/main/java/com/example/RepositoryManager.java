package com.example;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class RepositoryManager {
    private final String masterDirectory;
    private Map<Person, List<Document>> structure = null;

    private int personCounter = 0;

    public RepositoryManager(String masterDirectory) {
        this.masterDirectory = masterDirectory;
        this.structure = new HashMap<>();

        try {
            loadStructure();
        } catch (Exception e) {

            System.err.println("Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) { 

        RepositoryManager repositoryManager = new RepositoryManager("mastearDirectory");

        repositoryManager.displayRepositoryContent();

    }

    public void displayRepositoryContent() {

        for (var entry : structure.entrySet()) {

            System.out.println(entry.getKey());

            for (Document document : entry.getValue()) {

                System.out.println("\t" + document);
            }
        }
    }

    public List<String> getRepositoryContent() throws CustomException {
        List<String> contentList = new ArrayList<>();

        File masterDir = new File(masterDirectory);
        if (!masterDir.exists() || !masterDir.isDirectory())
            throw new CustomException("Master directory does not exist or is not a directory.");

        File[] personDirs = masterDir.listFiles();
        if (personDirs == null || personDirs.length == 0)
            throw new CustomException("Master directory is empty");

        for (File personDir : personDirs) {
            if (personDir.isDirectory()) {
                contentList.add("\t" + personDir.getName() + ":\n");
                File[] personFiles = personDir.listFiles();
                if (personFiles != null) {
                    for (File file : personFiles) {
                        contentList.add("\t\t" + file.getName() + "\n");
                    }
                }
            }
        }

        return contentList;
    }

    private void loadStructure() throws CustomException {
        File masterDir = new File(masterDirectory);
        if (!masterDir.exists() || !masterDir.isDirectory())
            throw new CustomException("Master directory does not exist or is not a directory.");

        File[] personDirs = masterDir.listFiles();
        if (personDirs == null)
            throw new CustomException("Master directory is empty");

        for (File personDir : personDirs) {
            if (personDir.isDirectory()) {
                String personName = personDir.getName();

                List<Document> documents = new LinkedList<>();
                File[] personFiles = personDir.listFiles();
                if (personFiles != null) {
                    for (File file : personFiles) {
                        documents.add(new Document(file.getName()));
                    }
                }
                structure.put(new Person(personName, personCounter++), documents);
            }
        }
    }

}
