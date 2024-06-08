package com.remonsinnema.resin2modules.graph;

import lombok.NonNull;


public record Edge(@NonNull Vertex from, @NonNull Vertex to) implements Comparable<Edge> {

    public boolean equals(Vertex start, Vertex end) {
        return from.equals(start) && to.equals(end);
    }

    @Override
    public String toString() {
        return "%s -> %s".formatted(from.name(), to.name());
    }

    @Override
    public int compareTo(Edge that) {
        var result = this.from.compareTo(that.from);
        if (result != 0) {
            return result;
        }
        return this.to.compareTo(that.to);
    }

}
