package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Vertex;

import java.util.LinkedHashSet;
import java.util.Set;


public record Module(String name, Set<Vertex> contents) implements Vertex {

    public static Module from(Vertex vertex) {
        return new Module(vertex.name(), new LinkedHashSet<>(Set.of(vertex)));
    }

    public boolean contains(Vertex vertex) {
        return contents.contains(vertex);
    }

    void add(Vertex vertex) {
        contents.add(vertex);
    }

}
