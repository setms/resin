package org.setms.resin.domain;

import org.setms.resin.graph.Vertex;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;


/**
 * A domain is a set of active event storming items: aggregates, events, commands, policies, and read models.
 * @param name the name of the domain
 * @param contents the active event storming items making up the domain
 */
public record Domain(String name, Set<Vertex> contents) implements Vertex {

    public static Domain from(Vertex vertex) {
        return new Domain(vertex.name(), new LinkedHashSet<>(Set.of(vertex)));
    }

    public boolean contains(Vertex vertex) {
        return contents.stream().anyMatch(content -> contains(content, vertex));
    }

    private boolean contains(Vertex content, Vertex vertex) {
        return content.equals(vertex) || content instanceof Domain domain && domain.contains(vertex);
    }

    public void add(Vertex vertex) {
        contents.add(vertex);
    }

    public <T extends Vertex> Stream<T> contents(Class<T> type) {
        return contents.stream()
                .filter(type::isInstance)
                .map(type::cast);
    }

}
