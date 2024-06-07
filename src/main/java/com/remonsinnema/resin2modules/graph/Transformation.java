package com.remonsinnema.resin2modules.graph;

import java.util.function.Function;


public interface Transformation<F extends Graph, T extends Graph> extends Function<F, T> {
}
