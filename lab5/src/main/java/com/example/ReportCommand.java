package com.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.awt.Desktop;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ReportCommand implements Command {
    private final RepositoryManager repositoryManager;

    public ReportCommand(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public void execute() throws CustomException {
        System.out.println("Generating HTML Report...");

        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setDirectoryForTemplateLoading(new File("./templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template template = cfg.getTemplate("report_template.ftl");

            Map<String, Object> data = new HashMap<>();
            data.put("content", repositoryManager.getRepositoryContent());
        
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            String htmlContent = writer.toString();

            try (PrintWriter output = new PrintWriter("repository_report.html")) {
                output.println(htmlContent);
            }

            System.out.println("Report Generated: repository_report.html");

            Desktop.getDesktop().open(new File("repository_report.html"));
        } catch (Exception e) {
            throw new CustomException("Error generating HTML report", e);
        }
    }
}
