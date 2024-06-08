package com.remonsinnema.resin2modules.graph;

import lombok.NonNull;


public record Edge(@NonNull Vertex from, @NonNull Vertex to) {

    public boolean equals(Vertex start, Vertex end) {
        return from.equals(start) && to.equals(end);
    }

    @Override
    public String toString() {
        return "%s -> %s".formatted(from.name(), to.name());
    }

}
