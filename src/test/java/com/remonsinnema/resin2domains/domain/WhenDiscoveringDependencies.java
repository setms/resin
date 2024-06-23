package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.process.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenDiscoveringDependencies {

    private final ProcessToDependencies processToDependencies = new ProcessToDependencies();

    @Test
    void shouldAddOnlyAggregatesReadModelsAndAutomaticPoliciesAsVertices() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", emptyList()));
        var evt = process.vertex(new Event("evt"));
        var cle = process.vertex(new ClockEvent("cle"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var mpl = process.vertex(new ManualPolicy("mpl"));
        var rdm = process.vertex(new ReadModel("rdm", emptyList()));
        var exs = process.vertex(new ExternalSystem("exs"));

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.vertices().anyMatch(agg::equals), is(true));
        assertThat(dependencies.vertices().anyMatch(rdm::equals), is(true));
        assertThat(dependencies.vertices().anyMatch(apl::equals), is(true));
        assertThat(dependencies.vertices().anyMatch(cmd::equals), is(false));
        assertThat(dependencies.vertices().anyMatch(evt::equals), is(false));
        assertThat(dependencies.vertices().anyMatch(cle::equals), is(false));
        assertThat(dependencies.vertices().anyMatch(mpl::equals), is(false));
        assertThat(dependencies.vertices().anyMatch(exs::equals), is(false));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.vertex(new Command("cmd")));
    }

    @Test
    void shouldRestrictEdges() {
        var dependencies = new SoftwareProcessDependencies();
        var agg = dependencies.vertex(new Aggregate("agg", emptyList()));
        var rdm = dependencies.vertex(new ReadModel("rdm", emptyList()));
        var apl = dependencies.vertex(new AutomaticPolicy("apl"));

        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(agg, agg));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(agg, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(apl, apl));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(apl, agg));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(rdm, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                dependencies.edge(rdm, apl));
    }

    @Test
    void shouldAddEdgeFromAggregateToPolicyIfPolicyIssuesCommandThatAggregateAccepts() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", emptyList()));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        process.edges(apl, cmd, agg);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(agg, apl)), is(true));
    }

    @Test
    void shouldNotAddEdgeFromAggregateToPolicyIfPolicyDoesNotIssuesCommandThatAggregateAccepts() {
        var process = new SoftwareProcess();
        var cmd1 = process.vertex(new Command("cmd1"));
        var cmd2 = process.vertex(new Command("cmd2"));
        var agg = process.vertex(new Aggregate("agg", emptyList()));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        process.edge(apl, cmd1);
        process.edge(cmd2, agg);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(agg, apl)), is(false));
    }

    @Test
    void shouldAddEdgeFromPolicyToReadModelIfPolicyUsesReadModel() {
        var process = new SoftwareProcess();
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var rdm = process.vertex(new ReadModel("rdm", emptyList()));
        process.edge(rdm, apl);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(apl, rdm)), is(true));
    }

    @Test
    void shouldNotAddEdgeFromPolicyToReadModelIfPolicyDoesNotUseReadModel() {
        var process = new SoftwareProcess();
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var rdm = process.vertex(new ReadModel("rdm", emptyList()));

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(apl, rdm)), is(false));
    }

    @Test
    void shouldAddEdgeFRomReadModelToAggregateIfReadModelUpdatesFromEventEmittedByAggregateAndSharesEntities() {
        var process = new SoftwareProcess();
        var agg = process.vertex(new Aggregate("agg", List.of("entity", "entity1")));
        var rdm = process.vertex(new ReadModel("rdm", List.of("entity2", "entity")));
        var evt = process.vertex(new Event("evt"));
        process.edges(agg, evt, rdm);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(rdm, agg)), is(true));
    }

    @Test
    void shouldNotAddEdgeFRomReadModelToAggregateIfReadModelUpdatesFromEventEmittedByAggregateAndDontShareEntities() {
        var process = new SoftwareProcess();
        var agg = process.vertex(new Aggregate("agg", List.of("entity1")));
        var rdm = process.vertex(new ReadModel("rdm", List.of("entity2")));
        var evt = process.vertex(new Event("evt"));
        process.edges(agg, evt, rdm);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().anyMatch(e -> e.equals(rdm, agg)), is(false));
    }

    @Test
    void shouldNotAddEdgeFRomReadModelToAggregateIfReadModelDoesNotUpdatesFromEventEmittedByAggregate() {
        var process = new SoftwareProcess();
        var agg = process.vertex(new Aggregate("agg", List.of("entity")));
        var rdm = process.vertex(new ReadModel("rdm", List.of("entity")));
        var rdm2 = process.vertex(new ReadModel("rdm2", List.of("entity")));
        var evt1 = process.vertex(new Event("evt1"));
        var evt2 = process.vertex(new Event("evt2"));
        process.edges(agg, evt1, rdm2);
        process.edge(evt2, rdm);

        var dependencies = processToDependencies.apply(process);

        assertThat(dependencies.edges().noneMatch(e -> e.equals(rdm, agg)), is(true));
    }

}
