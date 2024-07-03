package com.remonsinnema.resin2domains.process;

import com.remonsinnema.resin2domains.graph.TestVertex;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenDocumentingSoftwareProcesses {

    @Test
    void shouldOnlyAcceptEventStormingVertices() {
        var process = new SoftwareProcess();
        process.element(new Aggregate("agg", emptyList()));
        process.element(new Command("cmd"));
        process.element(new Event("evt"));
        process.element(new ClockEvent("cle"));
        process.element(new ExternalSystem("exs"));
        process.element(new Person("usr"));
        process.element(new AutomaticPolicy("apl"));
        process.element(new ManualPolicy("mpl"));
        process.element(new ReadModel("rdm", emptyList()));

        assertThrows(IllegalArgumentException.class, () ->
                process.element(new TestVertex()));
    }

    @Test
    void shouldOnlyAcceptEdgesFollowingEventStormingGrammar() {
        var process = new SoftwareProcess();
        var usr = process.element(new Person("usr"));
        var cmd = process.element(new Command("cmd"));
        var agg = process.element(new Aggregate("agg", emptyList()));
        var evt = process.element(new Event("evt"));
        var cle = process.element(new ClockEvent("cle"));
        var apl = process.element(new AutomaticPolicy("apl"));
        var mpl = process.element(new ManualPolicy("mpl"));
        var rdm = process.element(new ReadModel("rdm", emptyList()));
        var exs = process.element(new ExternalSystem("exs"));

        process.connect(usr, cmd, agg, evt, apl, cmd);
        process.connect(rdm, apl);
        process.connect(usr, exs, cmd);
        process.connect(exs, evt, rdm);
        process.connect(evt, exs);
        process.connect(evt, mpl, cmd);
        process.connect(cle, mpl);
        process.connect(rdm, usr, mpl);

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(usr, rdm));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cmd, rdm));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(agg, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(evt, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(evt, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(evt, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(evt, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(evt, cle));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cle, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cle, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cle, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cle, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(cle, evt));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(apl, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(mpl, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, cmd));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, evt));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, cle));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(rdm, exs));

        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, exs));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, usr));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, agg));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, apl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, mpl));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, rdm));
        assertThrows(IllegalArgumentException.class, () ->
                process.connect(exs, cle));
    }

}
