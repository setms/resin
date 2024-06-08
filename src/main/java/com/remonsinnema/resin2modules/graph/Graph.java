package com.remonsinnema.resin2modules.graph;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;


@RequiredArgsConstructor
public class Graph {

    private final Set<Vertex> vertices = new LinkedHashSet<>();
    private final Set<Edge> edges = new LinkedHashSet<>();
    private final Constraints constraints;

    public <T extends Vertex> T vertex(T vertex) {
        if (constraints.canAddVertex(vertex)) {
            vertices.add(vertex);
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
