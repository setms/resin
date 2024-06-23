package com.remonsinnema.resin2domains.process;

import com.remonsinnema.resin2domains.graph.Vertex;

import java.util.Collection;


public record Aggregate(String name, Collection<String> dataItems) implements Vertex, DataContainer {

}
