package com.remonsinnema.resin2modules.graph;


public interface Vertex {

    String name();

    default String id() {
        return "%s '%s'".formatted(getClass().getSimpleName(), name());
    }

}
