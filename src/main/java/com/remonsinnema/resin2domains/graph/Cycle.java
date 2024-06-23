package com.remonsinnema.resin2domains.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SequencedCollection;


public record Cycle(SequencedCollection<Vertex> vertices) implements Comparable<Cycle> {

    public Cycle {
        if (vertices.isEmpty()) {
            throw new IllegalArgumentException("Need at least one vertex in a cycle");
        }
        if (!vertices.getFirst().equals(vertices.getLast())) {
            throw new IllegalArgumentException("Not a cycle: " + vertices);
        }
        if (vertices.stream().distinct().count() + 1 != vertices.size()) {
            throw new IllegalArgumentException("Cycle must contain unique vertices");
        }
    }

    public Cycle(Vertex... vertices) {
        this(Arrays.asList(vertices));
    }

    public boolean contains(Vertex vertex) {
        return vertices.contains(vertex);
    }

    @Override
    public int hashCode() {
        return normalize().hashCode();
    }

    private SequencedCollection<Vertex> normalize() {
        var original = new ArrayList<>(vertices);
        var min = original.stream().min(Vertex::compareTo).orElseThrow();
        var index = original.indexOf(min);
        if (index == 0) {
            return original;
        }
        var end = original.subList(1, index);
        var start = original.subList(index, original.size());

        var result = new ArrayList<Vertex>();
        result.addAll(start);
        result.addAll(end);
        result.add(min);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Cycle that) {
            return this.vertices.size() == that.vertices.size()
                    && this.normalize().equals(that.normalize());
        }
        return false;
    }

    @Override
    public int compareTo(Cycle that) {
        var result = that.vertices.size() - this.vertices.size();
        if (result != 0) {
            return result;
        }
        var thisIterator = this.normalize().iterator();
        var thatIterator = that.normalize().iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext()) {
            var thisItem = thisIterator.next();
            var thatItem = thatIterator.next();
            result = thisItem.compareTo(thatItem);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return vertices.toString();
    }

}
