package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Shell {
    private final RepositoryManager repositoryManager;
    private final Map<String, Command> commands;

    public Shell(String masterDirectory) {
        this.repositoryManager = new RepositoryManager(masterDirectory);
        this.commands = new HashMap<>();
        commands.put("view", new ViewCommand(masterDirectory));
        commands.put("report", new ReportCommand(repositoryManager));
        commands.put("export", new ExportCommand(masterDirectory));
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command (view/report/export/exit): ");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                break;
            }
            if (input.isEmpty()) {
                continue;
            }

            String[] tokens = input.split("\\s+");

            String commandName = tokens[0];

            Command command = commands.get(commandName);
            if (command != null) {
                try {
                    if (command instanceof ViewCommand) {

                        if (tokens.length < 2)
                            throw new CustomException("not enough parameters");

                        String filePath = tokens[1];

                        ((ViewCommand) command).setFilePath(filePath);
                    }
                    command.execute();
                } catch (CustomException e) {
                    System.err.println("Error executing command: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid command. Available commands: view, report, export, exit");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {

        String masterDirectory = "./masterDirectory";

        Shell app = new Shell(masterDirectory);
        app.run();
    }
}
