package com.martenls.qasystem.services;

import com.martenls.qasystem.models.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TemplateProvider {


    private List<Template> templates;

    private List<Template> askTemplates;

    public TemplateProvider(@Value("${data.templates}") String templateFilePath) {
        String templateFile = "";
        try {
            templateFile = new String(Files.readAllBytes(Paths.get(templateFilePath)));
        } catch (IOException e) {
            log.error("Could not read template file: " + e.getMessage());
        }
        this.templates = Arrays.stream(templateFile.replaceAll("\r", "").replaceAll("#.*\n", "").split("\n---\n"))
                .map(Template::new)
                .collect(Collectors.toList());
        log.debug("Successfully loaded " + templates.size() + " templates");
        this.askTemplates = new ArrayList<>();
        for (Template template : templates) {
            Template askTemplate;
            // replace SELECT, WHERE with ASK and remove ORDER BY, GROUP BY etc
            askTemplate = new Template(template.getTemplateStr().replaceAll("[\\s\\S]*\\{", "ASK \\{").replaceAll("}[\\s\\S]*", "}"));

            this.askTemplates.add(askTemplate);
        }
        // remove duplicates
        this.askTemplates = new ArrayList<>(this.askTemplates.stream().collect(Collectors.toMap(t -> t.getTemplateStr().replaceAll("\\s+",""), t -> t, (tA, tB) -> tA)).values());
        log.debug("Successfully loaded " + askTemplates.size() + " askTemplates");
    }



    public List<Template> getTemplates() {
        return templates;
    }

    public List<Template> getAskTemplates() {
        return askTemplates;
    }


}
