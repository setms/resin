package com.remonsinnema.resin2modules.process;

import com.remonsinnema.resin2modules.graph.TestVertex;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenDocumentingSoftwareProcesses {

    @Test
    void shouldOnlyAcceptEventStormingVertices() {
        var process = new SoftwareProcess();
        process.vertex(new Aggregate("agg", emptyList()));
        process.vertex(new Command("cmd"));
        process.vertex(new Event("evt"));
        process.vertex(new ClockEvent("cle"));
        process.vertex(new ExternalSystem("exs"));
        process.vertex(new Person("usr"));
        process.vertex(new AutomaticPolicy("apl"));
        process.vertex(new ManualPolicy("mpl"));
        process.vertex(new ReadModel("rdm", emptyList()));

        assertThrows(IllegalArgumentException.class, () ->
                process.vertex(new TestVertex()));
    }

    @Test
    void shouldOnlyAcceptEdgesFollowingEventStormingGrammar() {
        var process = new SoftwareProcess();
        var usr = process.vertex(new Person("usr"));
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", emptyList()));
        var evt = process.vertex(new Event("evt"));
        var cle = process.vertex(new ClockEvent("cle"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var mpl = process.vertex(new ManualPolicy("mpl"));
        var rdm = process.vertex(new ReadModel("rdm", emptyList()));
        var exs = process.vertex(new ExternalSystem("exs"));

        process.edges(usr, cmd, agg, evt, apl, cmd);
        process.edge(rdm, apl);
        process.edges(usr, exs, cmd);
        process.edges(exs, evt, rdm);
        process.edges(evt, exs);
        process.edges(evt, mpl, cmd);
        process.edge(cle, mpl);
        process.edges(rdm, usr, mpl);

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(usr, rdm));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cmd, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(agg, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(evt, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(evt, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(evt, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(evt, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(evt, cle));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cle, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cle, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cle, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cle, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(cle, evt));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(apl, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(mpl, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(rdm, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, exs));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.edge(exs, cle));
    }

}
