package org.setms.resin.graph;

import java.util.function.Function;


public interface Transformation<F extends Graph, T extends Graph> extends Function<F, T> {
}
