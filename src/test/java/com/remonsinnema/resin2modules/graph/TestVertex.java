package com.remonsinnema.resin2modules.graph;

import java.util.UUID;


public record TestVertex(String name) implements Vertex, Comparable<TestVertex> {

    public TestVertex() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public int compareTo(TestVertex that) {
        return this.name.compareTo(that.name);
    }

}
