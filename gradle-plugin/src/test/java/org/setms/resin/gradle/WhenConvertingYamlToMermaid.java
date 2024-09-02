package org.setms.resin.gradle;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenConvertingYamlToMermaid {

    private static final String YAML = """
            process:
              elements:
                usr:
                  type: person
                cmd:
                  type: command
                agg:
                  type: aggregate
                  data:
                    - data1
                    - data2
                evt:
                  type: event
              flows:
                - flow:
                  - usr
                  - cmd
                  - agg
                  - evt
            """;
    private static final String MERMAID = """
            graph
                usrPerson([usr])
                cmdCommand{{cmd}}
                aggAggregate(agg)
                evtEvent>evt]
            
                usrPerson --> cmdCommand
                cmdCommand --> aggAggregate
                aggAggregate --> evtEvent""";

    @TempDir
    File tempDir;
    private File inputDir;
    private File outputDir;
    private ResinToMermaidTask task;

    @BeforeEach
    void init() {
        var project = ProjectBuilder.builder().withProjectDir(tempDir).build();
        new ResinPlugin().apply(project);
        inputDir = project.getLayout().getProjectDirectory().dir("src/main/resin").getAsFile();
        outputDir = project.getLayout().getBuildDirectory().dir("resin").get().getAsFile();
        task = (ResinToMermaidTask) project.getTasks().findByName("resinToMermaid");
    }

    @Test
    void shouldConvertYamlFileToMmd() throws IOException {
        givenInputFile();
        task.run();
        assertOutput();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void givenInputFile() throws IOException {
        inputDir.mkdirs();
        var result = new File(inputDir, "test.yaml");
        try (var writer = new FileWriter(result)) {
            writer.write(YAML);
        }
    }

    private void assertOutput() throws IOException {
        var outputFile = new File(outputDir, "test.mmd");
        try (var lines = Files.lines(outputFile.toPath())) {
            var output = lines.collect(Collectors.joining(System.lineSeparator()));
            assertEquals(MERMAID, output); 
        }
    }

}
