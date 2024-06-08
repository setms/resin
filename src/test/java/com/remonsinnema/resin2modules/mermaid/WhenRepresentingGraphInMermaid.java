package com.remonsinnema.resin2modules.mermaid;

import com.remonsinnema.resin2modules.graph.Graph;
import com.remonsinnema.resin2modules.graph.TestConstraints;
import com.remonsinnema.resin2modules.graph.TestVertex;
import com.remonsinnema.resin2modules.process.*;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


class WhenRepresentingGraphInMermaid {

    private final MermaidRepresentation representation = new MermaidRepresentation();

    @Test
    void shouldRenderEmptyGraph() {
        var graph = new Graph(new TestConstraints());

        var mermaid = representation.apply(graph);

        assertThat(mermaid, is("graph\n"));
    }

    @Test
    @SuppressWarnings("TrailingWhitespacesInTextBlock")
    void shouldRenderGenericGraph() {
        var graph = new Graph(new TestConstraints());
        var v1 = graph.vertex(new TestVertex("v1"));
        var v2 = graph.vertex(new TestVertex("v2"));
        var v3 = graph.vertex(new TestVertex("v3"));
        graph.edge(v1, v3);
        graph.edge(v2, v3);

        var mermaid = representation.apply(graph);

        assertThat(mermaid, is("""
                graph
                  v1TestVertex[v1]
                  v2TestVertex[v2]
                  v3TestVertex[v3]
                  
                  v1TestVertex --> v3TestVertex
                  v2TestVertex --> v3TestVertex
                """));
    }

    @Test
    void shouldRenderSpecialShapesForResinSymbols() {
        var graph = new SoftwareProcess();
        var person = graph.vertex(new Person("usr"));
        var cmd = graph.vertex(new Command("cmd"));
        var agg = graph.vertex(new Aggregate("agg", emptyList()));
        var evt = graph.vertex(new Event("evt"));
        var cle = graph.vertex(new ClockEvent("cle"));
        var apl = graph.vertex(new AutomaticPolicy("apl"));
        var mpl = graph.vertex(new ManualPolicy("mpl"));
        var rdm = graph.vertex(new ReadModel("rdm", emptyList()));
        var exs = graph.vertex(new ExternalSystem("exs"));
        graph.edges(person, cmd, agg, evt, apl);
        graph.edge(evt, rdm);
        graph.edges(person, exs, cmd);
        graph.edges(cle, mpl, cmd);

        var mermaid = representation.apply(graph);

        assertThat(mermaid, containsString("(agg)"));
        assertThat(mermaid, containsString("[[rdm]]"));
        assertThat(mermaid, containsString("[/apl/]"));
        assertThat(mermaid, containsString("[/mpl/]"));
        assertThat(mermaid, containsString("{{cmd}}"));
        assertThat(mermaid, containsString(">evt]"));
        assertThat(mermaid, containsString(">cle]"));
        assertThat(mermaid, containsString("[exs]"));
        assertThat(mermaid, containsString("usrPerson --> cmdCommand"));
    }

}
