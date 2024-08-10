package org.setms.resin.graph;


public record InvalidVertex(String name) implements Vertex {

    public InvalidVertex() {
        this(null);
    }

}
