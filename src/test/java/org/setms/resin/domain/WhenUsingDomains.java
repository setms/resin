package org.setms.resin.domain;

import org.setms.resin.process.Aggregate;
import org.setms.resin.process.Event;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class WhenUsingDomains {

    @Test
    void domainShouldContainVertices() {
        var agg = new Aggregate("agg", List.of("data"));
        var evt = new Event("evt");
        var sub = new Domain("sub", Set.of(evt));
        var not = new Aggregate("not", List.of("not"));

        var domain = new Domain("dom", Set.of(agg, sub));

        assertThat(domain.contains(agg), is(true));
        assertThat(domain.contains(evt), is(true));
        assertThat(domain.contains(not), is(false));
    }

    @Test
    void domainsShouldContainVerices() {
        var agg = new Aggregate("agg", List.of("data"));
        var evt = new Event("evt");
        var sub = new Domain("sub", Set.of(evt));
        var not = new Aggregate("not", List.of("not"));
        var dom = new Domain("dom", Set.of(agg, sub));

        var domains = new Domains();
        domains.add(dom);

        assertThat(domains.contains(agg), is(true));
        assertThat(domains.contains(evt), is(true));
        assertThat(domains.contains(not), is(false));
    }
}
