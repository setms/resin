package org.setms.resin.format.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.setms.resin.format.SoftwareProcessDeserialization;
import org.setms.resin.graph.Vertex;
import org.setms.resin.process.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Deserializes a {@link SoftwareProcess} from a YAML file.
 */
public class YamlToSoftwareProcess implements SoftwareProcessDeserialization {

    @Override
    public SoftwareProcess apply(InputStream input) {
        Root root = new Yaml(new Constructor(Root.class, new LoaderOptions())).load(input);
        var result = new SoftwareProcess();
        var verticesByName = new HashMap<String, Vertex>();
        root.getProcess().getElements().forEach((name, element) ->
                verticesByName.put(name, addVertexFrom(element, name, result)));
        root.getProcess().getFlows().forEach(flow ->
                result.connect(flow.getFlow().stream()
                        .map(verticesByName::get)
                        .toArray(Vertex[]::new)));
        return result;
    }

    private Vertex addVertexFrom(Element element, String name, SoftwareProcess process) {
        return switch (element.getType()) {
            case "read-model" -> process.element(new ReadModel(name, element.getData()));
            case "person" -> process.element(new Person(name));
            case "command" -> process.element(new Command(name));
            case "aggregate" -> process.element(new Aggregate(name, element.getData()));
            case "event" -> process.element(new Event(name));
            case "automatic-policy" -> process.element(new AutomaticPolicy(name));
            case "external-system" -> process.element(new ExternalSystem(name));
            default -> throw new IllegalStateException("Unexpected element type: " + element.getType());
        };
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
