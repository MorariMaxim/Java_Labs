package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.github.javafaker.Faker;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.clique.*;
import org.graph4j.util.Clique;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcelAbilities {

    public static void main(String[] args) {

        int numberOfPersons = 100;
        int abilitiesPerPerson = 3;

        String excelFilePath = "./masterDirectory/abilities.xlsx";
        try {
            List<Person> persons = generatePersons(numberOfPersons, abilitiesPerPerson);
            createExcelFile(persons, excelFilePath);
            System.out.println("Excel file created successfully.");

            findPrintCliques(readExcelFile(excelFilePath));

        } catch (IOException e) {
            System.err.println("Error creating/reading Excel file : " + e.getMessage());
        }

    }

    public static List<Person> generatePersons(int numberOfPersons, int abilitiesPerPerson) {
        Faker faker = new Faker();
        List<Person> persons = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfPersons; i++) {
            String name = faker.name().fullName();
            Set<String> abilities = new HashSet<>();

            while (abilities.size() != abilitiesPerPerson) {
                abilities.add(abilitiesArray.get(random.nextInt(abilitiesArray.size())));
            }
            persons.add(new Person(name, abilities));
        }
        return persons;
    }

    public static void createExcelFile(List<Person> persons, String filename) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Abilities");

            int rowNum = 0;
            for (Person person : persons) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                Cell nameCell = row.createCell(colNum++);
                nameCell.setCellValue(person.getName());

                Cell abilitiesCell = row.createCell(colNum);
                abilitiesCell.setCellValue(String.join(",", person.getAbilities()));
            }

            try (FileOutputStream outputStream = new FileOutputStream(filename)) {
                workbook.write(outputStream);
            }
        }
    }

    public static List<Person> readExcelFile(String filename) throws IOException {
        List<Person> persons = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filename))) {
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                String name = null;
                Set<String> abilities = new HashSet<>();

                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    int columnIndex = currentCell.getColumnIndex();

                    if (columnIndex == 0) {
                        name = currentCell.getStringCellValue();
                    } else if (columnIndex == 1) {
                        String abilitiesString = currentCell.getStringCellValue();
                        abilities = Arrays.stream(abilitiesString.split(","))
                                .map(String::trim)
                                .collect(Collectors.toSet());

                    }
                }

                if (name != null && !abilities.isEmpty()) {
                    persons.add(new Person(name, abilities));
                }
            }
        }
        return persons;
    }

    public static void findPrintCliques(List<Person> persons) {
        int numberOfPersons = persons.size();

        GraphBuilder graphBuilder = GraphBuilder.numVertices(numberOfPersons);

        for (int firstPersonIndex = 0; firstPersonIndex < numberOfPersons; firstPersonIndex++) {

            Person firstPerson = persons.get(firstPersonIndex);

            for (int secondPersonIndex = 0; secondPersonIndex < numberOfPersons; secondPersonIndex++) {

                Person secondPerson = persons.get(secondPersonIndex);
                if (haveCommonAbility(firstPerson.abilities, secondPerson.abilities)) {
                    graphBuilder.addEdge(firstPersonIndex, secondPersonIndex);
                }
            }

        }

        Graph graph = graphBuilder.buildGraph();

        BronKerboschCliqueIterator cliques = new BronKerboschCliqueIterator(graph);

        while (cliques.hasNext()) {
            Clique clique = cliques.next();
            System.out.print("[");
            clique.forEach((id) -> System.out.print(persons.get(id).name + ", "));
            System.out.println("]\n");
        }
    }

    private static boolean haveCommonAbility(Set<String> set1, Set<String> set2) {

        return set1.stream()
                .anyMatch(set2::contains);
    }

    static class Person {
        private final String name;
        private final Set<String> abilities;

        public Person(String name, Set<String> abilities) {
            this.name = name;
            this.abilities = abilities;
        }

        public String getName() {
            return name;
        }

        public Set<String> getAbilities() {
            return abilities;
        }

        @Override
        public String toString() {
            return "Person [name=" + name + ", abilities=" + abilities + "]\n";
        }

    }

    static List<String> abilitiesArray = Arrays.asList(
            "Leadership",
            "Teamwork",
            "Communication",
            "Problem Solving",
            "Creativity",
            "Adaptability",
            "Time Management",
            "Organizational Skills",
            "Critical Thinking",
            "Attention to Detail",
            "Interpersonal Skills",
            "Emotional Intelligence",
            "Stress Management",
            "Conflict Resolution",
            "Negotiation Skills",
            "Public Speaking",
            "Presentation Skills",
            "Customer Service",
            "Sales Skills",
            "Marketing Skills",
            "Project Management",
            "Strategic Planning",
            "Data Analysis",
            "Research Skills",
            "Writing Skills",
            "Editing Skills",
            "Copywriting",
            "Content Creation",
            "Graphic Design",
            "UI/UX Design",
            "Web Design",
            "Video Editing",
            "Photography",
            "Videography",
            "Animation",
            "Illustration",
            "Digital Art",
            "3D Modeling",
            "Fashion Design",
            "Interior Design",
            "Architecture",
            "Music Production",
            "Singing",
            "Dancing",
            "Acting",
            "Voice Acting",
            "Scriptwriting",
            "Screenwriting",
            "Film Production",
            "Podcasting",
            "Audio Editing",
            "Gaming",
            "Game Design",
            "Game Development",
            "Level Design",
            "Virtual Reality (VR)",
            "Augmented Reality (AR)",
            "Blockchain",
            "Cryptocurrency",
            "Financial Management",
            "Investment Analysis",
            "Personal Finance",
            "Accounting",
            "Tax Preparation",
            "Legal Research",
            "Legal Writing",
            "Public Relations",
            "Event Planning",
            "Hospitality Management",
            "Culinary Arts",
            "Baking",
            "Cooking",
            "Nutrition",
            "Fitness Training",
            "Yoga Instruction",
            "Pilates Instruction",
            "Meditation Instruction",
            "Personal Training",
            "Physical Therapy",
            "Massage Therapy",
            "Acupuncture",
            "Herbal Medicine",
            "Alternative Medicine",
            "Health Coaching",
            "Life Coaching",
            "Therapy",
            "Counseling",
            "Social Work",
            "Community Outreach",
            "Nonprofit Management",
            "Volunteer Management",
            "Environmental Conservation",
            "Sustainability",
            "Renewable Energy",
            "Green Building",
            "Recycling",
            "Waste Management",
            "Gardening",
            "Landscaping",
            "Animal Care",
            "Pet Grooming",
            "Dog Training",
            "Veterinary Care",
            "Wildlife Conservation",
            "Zookeeping",
            "Education",
            "Tutoring",
            "Curriculum Development",
            "E-Learning",
            "Instructional Design",
            "Language Teaching",
            "Translation",
            "Localization",
            "Proofreading",
            "Editing",
            "Library Science",
            "Archiving",
            "Research",
            "Historical Research",
            "Political Science",
            "Economics",
            "Sociology",
            "Psychology");
}
