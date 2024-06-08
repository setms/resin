package com.remonsinnema.resin2modules.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenUsingCycles {

    @Test
    void shouldValidateCycle() {
        var v1 = new TestVertex("v1");
        var v2 = new TestVertex("v2");

        assertThrows(IllegalArgumentException.class, Cycle::new);
        assertThrows(IllegalArgumentException.class, () -> new Cycle(v1, v2));
        assertThrows(IllegalArgumentException.class, () -> new Cycle(v1, v2, v2, v1));
    }

    @Test
    void shouldSerialize() {
        var v1 = new TestVertex("v1");
        var v2 = new TestVertex("v2");

        assertThat(new Cycle(v1, v2, v1).toString(), is(List.of(v1, v2, v1).toString()));
    }

    @Test
    @SuppressWarnings({"EqualsWithItself", "EqualsBetweenInconvertibleTypes"})
    void shouldCompareCycles() {
        var v1 = new TestVertex("v1");
        var v2 = new TestVertex("v2");
        var v3 = new TestVertex("v3");
        var v4 = new TestVertex("v4");
        var c12341 = new Cycle(v1, v2, v3, v4, v1);
        var c1231 = new Cycle(v1, v2, v3, v1);

        assertThat(c12341.compareTo(c12341), is(0));
        assertThat(c1231.compareTo(c12341), lessThan(0));
        assertThat(c12341.compareTo(c1231), greaterThan(0));

        assertThat(c12341.equals(c12341), is(true));
        assertThat(c12341.equals(c1231), is(false));
        assertThat(c12341.equals(v1), is(false));

        assertThat(c1231.hashCode(), not(is(0)));
    }

}
