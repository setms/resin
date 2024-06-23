package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.graph.Vertex;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;


public record Domain(String name, Set<Vertex> contents) implements Vertex {

    public static Domain from(Vertex vertex) {
        return new Domain(vertex.name(), new LinkedHashSet<>(Set.of(vertex)));
    }

    public boolean contains(Vertex vertex) {
        return contents.contains(vertex);
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
