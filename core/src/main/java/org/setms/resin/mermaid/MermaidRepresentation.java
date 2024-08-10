package org.setms.resin.mermaid;

import org.setms.resin.graph.Graph;
import org.setms.resin.graph.Representation;
import org.setms.resin.graph.Vertex;
import org.setms.resin.domain.Domain;
import org.setms.resin.process.*;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;


/**
 * A representation of a graph in Mermaid format.
 */
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
            return "\"%s\"".formatted(contentOf(domain));
        }
        return vertex.name();
    }

    private String contentOf(Domain domain) {
        var result = new StringBuilder();
        result.append("<b>").append(domain.name()).append("</b>\n");
        domain.contents().stream()
                .map(this::contentOfDomainElement)
                .sorted()
                .map("%s%n"::formatted)
                .forEach(result::append);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private String contentOfDomainElement(Vertex vertex) {
        if (vertex instanceof Domain domain) {
            return "\n    %s".formatted(contentOf(domain));
        }
        return "    %s: %s".formatted(typeOf(vertex), vertex.name());
    }

    private char typeOf(Vertex step) {
        if (step instanceof Policy) {
            return 'P';
        }
        return step.getClass().getSimpleName().charAt(0);
    }

}
