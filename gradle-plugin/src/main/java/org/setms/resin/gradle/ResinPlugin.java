package org.setms.resin.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle plugin for processing RESIN files.
 */
public class ResinPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("resinToMermaid", ResinToMermaidTask.class, task -> {
            task.setGroup("resin");
            task.setDescription("Converts RESIN files to a Mermaid graph.");
            task.setInputDir(project.getLayout().getProjectDirectory().dir("src/main/resin"));
            task.setOutputDir(project.getLayout().getBuildDirectory().dir("resin").get());
        });
    }

}
