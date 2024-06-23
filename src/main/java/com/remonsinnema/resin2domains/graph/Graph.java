package com.remonsinnema.resin2domains.graph;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;


@RequiredArgsConstructor
public class Graph {

    private final Collection<Vertex> vertices = new ArrayList<>();
    private final Collection<Edge> edges = new ArrayList<>();
    private final Constraints constraints;
    private final CycleDetector cycleDetector;

    public Graph(Constraints constraints) {
        this(constraints, new DetectCyclesViaExhaustiveSearch());
    }

    public <T extends Vertex> T vertex(T vertex) {
        if (constraints.canAddVertex(vertex)) {
            if (!vertices.contains(vertex)) {
                vertices.add(vertex);
            }
            return vertex;
        }
        throw new IllegalArgumentException("Can't add %s".formatted(vertex));
    }

    public void edge(Vertex from, Vertex to) {
        if (!vertices.contains(from)) {
            throw new IllegalArgumentException("Unknown <from> vertex %s".formatted(from));
        }
        if (!vertices.contains(to)) {
            throw new IllegalArgumentException("Unknown <to> vertex %s".formatted(to));
        }
        var result = new Edge(from, to);
        if (edges.contains(result)) {
            return;
        }
        if (constraints.canAddEdge(result)) {
            edges.add(result);
            return;
        }
        throw new IllegalArgumentException("Can't add edge %s".formatted(result));
    }

    public void edges(Vertex... nodes) {
        for (var i = 1; i < nodes.length; i++) {
            edge(nodes[i - 1], nodes[i]);
        }
    }

    public Stream<? extends Vertex> vertices() {
        return vertices.stream();
    }

    public <T extends Vertex> Stream<T> vertices(Class<T> type) {
        return vertices.stream()
                .filter(type::isInstance)
                .map(type::cast);
    }

    public Stream<Edge> edges() {
        return edges.stream();
    }

    public Stream<Vertex> edgesFrom(Vertex from) {
        return edges().filter(e -> e.from().equals(from))
                .map(Edge::to);
    }

    public Stream<Vertex> edgesTo(Vertex to) {
        return edges().filter(e -> e.to().equals(to))
                .map(Edge::from);
    }

    public Collection<Cycle> cycles() {
        return cycleDetector.findAllCyclesIn(this);
    }

    @Override
    public String toString() {
        var result = new StringBuilder();
        result.append("graph {\n");
        vertices()
                .map(Vertex::name)
                .sorted()
                .map("    %s\n"::formatted)
                .forEach(result::append);
        edges().map(Edge::toString)
                .sorted()
                .map("    %s\n"::formatted)
                .forEach(result::append);
        result.append("}");
        return result.toString();
    }

}
