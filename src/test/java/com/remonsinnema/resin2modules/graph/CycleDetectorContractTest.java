package com.remonsinnema.resin2modules.graph;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;


abstract class CycleDetectorContractTest {

    private final Graph graph = new Graph(new TestConstraints(), newCycleDetector());

    protected abstract CycleDetector newCycleDetector();


    @Test
    void shouldNotDetectAnyCyclesInAcyclicGraph() {
        var v1 = graph.vertex(new TestVertex("v1"));
        var v2 = graph.vertex(new TestVertex("v2"));
        graph.edge(v1, v2);

        var cycles = graph.cycles();

        assertThat(cycles.isEmpty(), is(true));
    }

    @Test
    void shouldDetectSingleCycle() {
        var v1 = graph.vertex(new TestVertex("v1"));
        var v2 = graph.vertex(new TestVertex("v2"));
        graph.edges(v1, v2, v1);

        var cycles = graph.cycles();

        assertThat(cycles, contains(is(new Cycle(v1, v2, v1))));
    }

    @Test
    void shouldDetectMultipleCycles() {
        var v1 = graph.vertex(new TestVertex("v1"));
        var v2 = graph.vertex(new TestVertex("v2"));
        var v3 = graph.vertex(new TestVertex("v3"));
        var v4 = graph.vertex(new TestVertex("v4"));
        var v5 = graph.vertex(new TestVertex("v5"));
        graph.edges(v1, v2, v3, v4, v5);
        graph.edge(v2, v1);
        graph.edge(v5, v3);

        var cycles = graph.cycles();

        assertThat(cycles, contains(new Cycle(v1, v2, v1), new Cycle(v3, v4, v5, v3)));
    }

}
