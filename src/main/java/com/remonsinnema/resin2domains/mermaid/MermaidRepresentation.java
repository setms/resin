package com.remonsinnema.resin2domains.mermaid;

import com.remonsinnema.resin2domains.graph.Graph;
import com.remonsinnema.resin2domains.graph.Representation;
import com.remonsinnema.resin2domains.graph.Vertex;
import com.remonsinnema.resin2domains.domain.Domain;
import com.remonsinnema.resin2domains.process.*;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;


public class MermaidRepresentation implements Representation {

    @Override
    public String apply(Graph graph) {
        var result = new StringBuilder();
        result.append("graph\n");

        if (graph.vertices().findAny().isPresent()) {
            graph.vertices()
                    .map(v -> "    %s%s%s%s\n".formatted(idOf(v), openingFor(v), nameOf(v), closingFor(v)))
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
            case Command command -> "{{";
            case DomainEvent event -> ">";
            case Person person -> "([";
            case Policy policy -> "[/";
            case ReadModel readModel -> "[[";
            default -> "[";
        };
    }

    @SuppressWarnings("unused")
    private String closingFor(Vertex vertex) {
        return switch (vertex) {
            case Aggregate aggregate -> ")";
            case Command command -> "}}";
            case Person person -> "])";
            case Policy policy -> "/]";
            case ReadModel readModel -> "]]";
            default -> "]";
        };
    }

    private String nameOf(Vertex vertex) {
        if (vertex instanceof Domain domain) {
            var result = new StringBuilder();
            result.append("\"<b>").append(domain.name()).append("</b>\n");
            domain.contents().stream()
                    .map(this::nameAndTypeOf)
                    .sorted()
                    .map("    %s%n"::formatted)
                    .forEach(result::append);
            result.deleteCharAt(result.length() - 1);
            result.append('"');
            return result.toString();
        }
        return vertex.name();
    }

    private String nameAndTypeOf(Vertex step) {
        return "%s: %s".formatted(typeOf(step), step.name());
    }

    private char typeOf(Vertex step) {
        if (step instanceof Policy) {
            return 'P';
        }
        return step.getClass().getSimpleName().charAt(0);
    }

}
