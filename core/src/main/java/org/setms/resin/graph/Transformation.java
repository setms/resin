package org.setms.resin.graph;

import java.util.function.Function;


/**
 * A transformation is a function that takes a graph as input and returns a graph as output.
 * @param <F> the type of the input graph
 * @param <T> the type of the output graph
 */
public interface Transformation<F extends Graph, T extends Graph> extends Function<F, T> {
}
