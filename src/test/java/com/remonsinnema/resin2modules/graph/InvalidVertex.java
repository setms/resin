package com.remonsinnema.resin2modules.graph;


public record InvalidVertex(String name) implements Vertex {

    public InvalidVertex() {
        this(null);
    }

}
