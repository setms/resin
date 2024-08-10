package org.setms.resin.format.yaml;

import org.junit.jupiter.api.Test;

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

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldConvertYamlFileToMmd() throws IOException {
        var inputFile = givenInputFile();
        try {
            var outputFile = new YamlToMermaid(new File(".")).apply(inputFile);
            try {
                assertOutput(outputFile);
            } finally {
                outputFile.delete();
            }
        } finally {
            inputFile.delete();
        }
    }

    private File givenInputFile() throws IOException {
        var result = new File("test.yaml");
        try (var writer = new FileWriter(result)) {
            writer.write(YAML);
        }
        return result;
    }

    private void assertOutput(File outputFile) throws IOException {
        try (var lines = Files.lines(outputFile.toPath())) {
            var output = lines.collect(Collectors.joining(System.lineSeparator()));
            assertEquals(MERMAID, output); 
        }
    }

}
