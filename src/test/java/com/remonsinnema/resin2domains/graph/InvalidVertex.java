package com.remonsinnema.resin2domains.graph;


public record InvalidVertex(String name) implements Vertex {

    public InvalidVertex() {
        this(null);
    }

}
