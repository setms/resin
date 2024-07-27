package org.setms.resin.format.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.setms.resin.format.SoftwareProcessSerialization;
import org.setms.resin.graph.Vertex;
import org.setms.resin.process.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlToSoftwareProcess implements SoftwareProcessSerialization {

    @Override
    public SoftwareProcess apply(InputStream input) {
        Root root = new Yaml(new Constructor(Root.class, new LoaderOptions())).load(input);
        var result = new SoftwareProcess();
        var verticesByName = new HashMap<String, Vertex>();
        root.getProcess().getElements().forEach((name, element) -> {
            var vertex = switch (element.getType()) {
                case "read-model" -> result.element(new ReadModel(name, element.getData()));
                case "person" -> result.element(new Person(name));
                case "command" -> result.element(new Command(name));
                case "aggregate" -> result.element(new Aggregate(name, element.getData()));
                case "event" -> result.element(new Event(name));
                case "automatic-policy" -> result.element(new AutomaticPolicy(name));
                case "external-system" -> result.element(new ExternalSystem(name));
                default -> throw new IllegalStateException("Unexpected element type: " + element.getType());
            };
            verticesByName.put(name, vertex);
        });
        root.getProcess().getFlows().forEach(flow -> {
            result.connect(flow.getFlow().stream().map(verticesByName::get).toArray(Vertex[]::new));
        });
        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Root {
        private Process process;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Process {
        private Map<String, Element> elements;
        private List<Flow> flows;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element {
        private String type;
        private List<String> data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flow {
        private List<String> flow;
    }

}
