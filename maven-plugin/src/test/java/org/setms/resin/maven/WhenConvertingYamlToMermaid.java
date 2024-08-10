package org.setms.resin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    private static final File INPUT_DIR = new File("src/main/resin");
    private static final File OUTPUT_DIR = new File("target/resin");

    private final ResinMojo mojo = new ResinMojo();
    private final Log log = mock(Log.class);

    @BeforeEach
    void init() {
        mojo.setLog(log);
    }

    @Test
    void shouldConvertYamlFileToMmd() throws IOException, MojoExecutionException {
        givenInputFile();
        try {
            mojo.execute();
            try {
                assertOutput("foo.mmd");
                assertOutput("bar.yml");
                verify(log).info("Generated bar.yml, foo.mmd");
            } finally {
                delete(OUTPUT_DIR);
            }
        } finally {
            delete(INPUT_DIR);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void givenInputFile() throws IOException {
        INPUT_DIR.mkdirs();
        writeYaml("foo.yaml");
        writeYaml("bar.yml");
        writeYaml("baz.ignored");
    }

    private void writeYaml(String file) throws IOException {
        var result = new File(INPUT_DIR, file);
        try (var writer = new FileWriter(result)) {
            writer.write(YAML);
        }
    }

    private void assertOutput(String file) throws IOException {
        var outputFile = new File(OUTPUT_DIR, file).getCanonicalFile();
        try (var lines = Files.lines(outputFile.toPath())) {
            var output = lines.collect(Collectors.joining(System.lineSeparator()));
            assertEquals(MERMAID, output); 
        }
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            Optional.ofNullable(file.listFiles())
                    .stream()
                    .flatMap(Arrays::stream)
                    .forEach(this::delete);
        }
        if (!file.delete()) {
            file.deleteOnExit();
        }
    }

}
