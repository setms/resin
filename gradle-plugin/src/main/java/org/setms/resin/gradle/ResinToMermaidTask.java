package org.setms.resin.gradle;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.setms.resin.format.yaml.YamlToMermaid;

/**
 * Task to convert RESIN files to Mermaid files.
 */
@Getter(onMethod_ = @InputDirectory)
@Setter
public class ResinToMermaidTask extends DefaultTask {

    private Directory inputDir;
    private Directory outputDir;

    @TaskAction
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        var output = outputDir.getAsFile();
        output.mkdirs();
        System.out.printf("Transforming RESIN files from %s to %s%n",  inputDir.getAsFile().getAbsolutePath(),
                output.getAbsolutePath());
        var converter = new YamlToMermaid(output);
        inputDir.getAsFileTree().forEach(converter::apply);
    }

}
