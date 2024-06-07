package com.remonsinnema.resin2modules.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenUsingGraphs {

    private final Graph graph = new Graph(new TestConstraints());

    @Test
    void shouldRejectInvalidVertex() {
        assertThrows(IllegalArgumentException.class, () ->
            graph.vertex(new InvalidVertex()));
    }

    @Test
    void shouldFindAddedVertex() {
        assertThat(graph.vertices().count(), is(0L));
        var v = new TestVertex();

        graph.vertex(v);

        assertThat(graph.vertices().count(), is(1L));
        assertThat(graph.vertices().anyMatch(v::equals), is(true));
    }

    @Test
    void shouldRejectInvalidFrom() {
        var v1 = new TestVertex();
        var v2 = graph.vertex(new TestVertex());

        assertThrows(IllegalArgumentException.class, () ->
                graph.edge(v1, v2));
    }

    @Test
    void shouldRejectInvalidTo() {
        var v1 = graph.vertex(new TestVertex());
        var v2 = new TestVertex();

        assertThrows(IllegalArgumentException.class, () ->
                graph.edge(v1, v2));
    }

    @Test
    void shouldRejectInvalidEdge() {
        var v1 = graph.vertex(new TestVertex());

        assertThrows(IllegalArgumentException.class, () ->
                graph.edge(v1, v1));
    }

    @Test
    void shouldFindAddedEdge() {
        assertThat(graph.edges().count(), is(0L));
        var v1 = graph.vertex(new TestVertex());
        var v2 = graph.vertex(new TestVertex());

        graph.edge(v1, v2);

        assertThat(graph.edges().count(), is(1L));
        assertThat(graph.edges().anyMatch(e -> e.equals(v1, v2)), is(true));
    }

    @Test
    void shouldCreateMultipleEdgesFromSuccessiveVertices() {
        var v1 = graph.vertex(new TestVertex());
        var v2 = graph.vertex(new TestVertex());
        var v3 = graph.vertex(new TestVertex());

        graph.edges(v1, v2, v3);

        assertThat(graph.edges().anyMatch(e -> e.equals(v1, v2)), is(true));
        assertThat(graph.edges().anyMatch(e -> e.equals(v2, v3)), is(true));
    }

    @Test
    void shouldFindEdgesFromAndToVertex() {
        var v1 = graph.vertex(new TestVertex("v1"));
        var v2 = graph.vertex(new TestVertex("v2"));
        var v3 = graph.vertex(new TestVertex("v3"));

        graph.edges(v1, v2, v3);
        graph.edge(v1, v3);

        assertThat(graph.edgesFrom(v1).sorted().toList(), is(List.of(v2, v3)));
        assertThat(graph.edgesFrom(v2).sorted().toList(), is(List.of(v3)));
        assertThat(graph.edgesFrom(v3).sorted().toList(), is(List.of()));
        assertThat(graph.edgesTo(v1).toList(), is(List.of()));
        assertThat(graph.edgesTo(v2).sorted().toList(), is(List.of(v1)));
        assertThat(graph.edgesTo(v3).sorted().toList(), is(List.of(v1, v2)));
    }

}
