package com.remonsinnema.resin2modules.mermaid;

import com.remonsinnema.resin2modules.graph.Graph;
import com.remonsinnema.resin2modules.graph.Representation;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.*;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;


public class MermaidRepresentation implements Representation {

    @Override
    public String apply(Graph graph) {
        var result = new StringBuilder();
        result.append("graph\n");

        if (graph.vertices().findAny().isPresent()) {
            graph.vertices()
                    .map(v -> "    %s%s%s%s\n".formatted(idOf(v), openingFor(v), v.name(), closingFor(v)))
                    .forEach(result::append);
            result.append('\n');
            graph.edges()
                    .map(e -> "    %s --> %s\n".formatted(idOf(e.from()), idOf(e.to())))
                    .forEach(result::append);
        }

        return result.toString();
    }

    private String idOf(Vertex vertex) {
        return toCamelCase("%s%s".formatted(vertex.name(), vertex.getClass().getSimpleName()));
    }

    private String toCamelCase(String value) {
        var result = new StringBuilder(value.trim());
        result.setCharAt(0, toLowerCase(result.charAt(0)));
        var index = result.indexOf(" ");
        while (index != -1) {
            result.deleteCharAt(index);
            result.setCharAt(index, toUpperCase(result.charAt(index)));
            index = result.indexOf(" ");
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    private String openingFor(Vertex vertex) {
        return switch (vertex) {
            case Aggregate aggregate -> "(";
            case ReadModel readModel -> "[[";
            case Policy policy -> "[/";
            case Command command -> "{{";
            case DomainEvent event -> ">";
            default -> "[";
        };
    }

    @SuppressWarnings("unused")
    private String closingFor(Vertex vertex) {
        return switch (vertex) {
            case Aggregate aggregate -> ")";
            case ReadModel readModel -> "]]";
            case Policy policy -> "/]";
            case Command command-> "}}";
            default -> "]";
        };
    }

}
