package org.setms.resin.graph;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;


/**
 * Very simple and inefficient cycle detector. You should probably not use this for large graphs.
 */
class DetectCyclesViaExhaustiveSearch implements CycleDetector {

    @Override
    public Collection<Cycle> findAllCyclesIn(Graph graph) {
        return graph.edges()
                .map(start -> findCyclesStartingAt(start, graph))
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .toList();
    }

    private Collection<Cycle> findCyclesStartingAt(Edge start, Graph graph) {
        var result = new TreeSet<Cycle>();
        addCycles(new Path(start.from(), start.to()), graph, result);
        return result;
    }

    private void addCycles(Path path, Graph graph, Collection<Cycle> cycles) {
        if (path.isCycle()) {
            cycles.add(path.toCycle());
            return;
        }
        graph.edges()
                .filter(path::couldMakeCycleByAdding)
                .forEach(e -> addCycles(path.add(e.to()), graph, cycles));
    }


    @RequiredArgsConstructor
    private static class Path {

        private final List<Vertex> vertices;

        Path(Vertex first, Vertex second) {
            this(List.of(first, second));
        }

        boolean couldMakeCycleByAdding(Edge edge) {
            if (!edge.from().equals(vertices.getLast())) {
                // Not even connected, so definitely no
                return false;
            }
            if (vertices.getFirst().equals(edge.to())) {
                // Found cycle, so definitely yes
                return true;
            }
            // If we've already seen the vertex, then there is a cycle that's shorter than what we have so far
            // We've either already found this cycle, or we'll find it later
            return !vertices.contains(edge.to());
        }

        Path add(Vertex vertex) {
            var newVertices = new ArrayList<>(vertices);
            newVertices.add((vertex));
            return new Path(newVertices);
        }

        boolean isCycle() {
            return vertices.getFirst().equals(vertices.getLast());
        }

        Cycle toCycle() {
            return new Cycle(vertices);
        }

    }

}
