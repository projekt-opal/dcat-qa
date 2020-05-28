package com.martenls.qasystem.services;

import com.martenls.qasystem.models.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TemplateProvider {

    @Value("${templates.file}")
    private String templateFilePath;

    private List<Template> templates;


    @PostConstruct
    public void loadTemplates() {
        String templateFile = "";
        try {
             templateFile = new String(Files.readAllBytes(Paths.get(templateFilePath)));
        } catch (IOException e) {
            log.error("Could not read template file: " + e.getMessage());
        }
        this.templates = Arrays.stream(templateFile.replaceAll("[\n\r]", " ").split("---"))
                .map(Template::new)
                .collect(Collectors.toList());
        log.debug("Successfully loaded " + templates.size() + " templates");
    }




}