package org.setms.resin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import org.setms.resin.format.yaml.YamlToMermaid;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;

@Mojo(name = "yaml-to-mermaid", defaultPhase = PROCESS_SOURCES)
public class ResinMojo extends AbstractMojo {

    private static final File OUTPUT_DIR = new File("target/resin");

    private final YamlToMermaid yamlToMermaid = new YamlToMermaid(OUTPUT_DIR);

    @Override
    public void execute() throws MojoExecutionException {
        var input = new File("src/main/resin")
                .listFiles((dir, name) -> name.endsWith(".yaml") || name.endsWith(".yml"));
        try {
            var generated = Optional.ofNullable(input)
                    .stream()
                    .flatMap(Arrays::stream)
                    .map(this::convertToMermaid)
                    .map(File::getName)
                    .sorted()
                    .collect(joining(", "));
            getLog().info("Generated " + generated);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to convert yaml to mermaid", e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File convertToMermaid(File file) {
        OUTPUT_DIR.mkdirs();
        return yamlToMermaid.apply(file);
    }
}
