package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class ExportCommand implements Command {
    private final String directoryPath;

    public ExportCommand(String directory) {
        this.directoryPath = directory;
    }

    @Override
    public void execute() throws CustomException {
        File directory = new File(directoryPath);

        if (!directory.isDirectory())
            throw new CustomException(directoryPath + " is not a directory");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        try {
            processDirectory(directory, rootNode, mapper);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("repository.json"), rootNode);
            System.out.println("Directory successfully exported");
        } catch (IOException e) {
            throw new CustomException("IoException", e);
        }
    }

    private static void processDirectory(File directory, ObjectNode node, ObjectMapper mapper) throws IOException {
        node.put("name", directory.getName());
        node.put("path", directory.getAbsolutePath());
        if (directory.listFiles() != null) {
            ArrayNode children = node.putArray("children");
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    ObjectNode childNode = mapper.createObjectNode();
                    processDirectory(file, childNode, mapper);
                    children.add(childNode);
                } else {
                    ObjectNode childNode = mapper.createObjectNode();
                    childNode.put("name", file.getName());
                    childNode.put("type", "file");
                    childNode.put("path", file.getAbsolutePath());
                    children.add(childNode);
                }
            }
        }
    }
}
