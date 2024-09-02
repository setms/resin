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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File apply(File file) {
        outputDir.mkdirs();
        var process = new YamlToSoftwareProcess().apply(file);
        var mermaid = new MermaidRepresentation().apply(process);
        var result = new File(outputDir, toOutputName(file));
        try (var writer = new FileWriter(result)) {
            writer.write(mermaid);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write MermaidJS output to " + result, e);
        }
        return result;
    }

    private String toOutputName(File file) {
        return file.getName()
                .replace(".yaml", ".mmd")
                .replace(".yml", ".mmd");
    }

}
