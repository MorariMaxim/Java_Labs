package com.example;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ViewCommand implements Command {
    private String filePath;
    private String masterDirectory;

    public ViewCommand(String masterDirectory) {
        this.masterDirectory = masterDirectory;
    }

    @Override
    public void execute() throws CustomException {
        try {
            File file = new File(masterDirectory +"/"+ filePath);
            if (!file.exists()) {
                throw new CustomException("File not found: " + masterDirectory +"/"+ filePath);
            }
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new CustomException("Error opening file: " + filePath, e);
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
