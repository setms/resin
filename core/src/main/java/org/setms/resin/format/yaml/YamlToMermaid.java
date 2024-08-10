package org.setms.resin.format.yaml;

import lombok.RequiredArgsConstructor;
import org.setms.resin.mermaid.MermaidRepresentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

@RequiredArgsConstructor
public class YamlToMermaid implements Function<File, File> {

    private final File outputDir;

    @Override
    public File apply(File file) {
        var process = new YamlToSoftwareProcess().apply(file);
        var mermaid = new MermaidRepresentation().apply(process);
        var result = new File(outputDir, file.getName().replace(".yaml", ".mmd"));
        try (var writer = new FileWriter(result)) {
            writer.write(mermaid);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write MermaidJS output to " + result, e);
        }
        return result;
    }

}
