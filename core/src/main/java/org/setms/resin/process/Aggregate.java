package org.setms.resin.process;

import org.setms.resin.graph.Vertex;

import java.util.Collection;


/**
 * An aggregate groups related entities and value objects.
 * @param name the name of the aggregate
 * @param dataItems the data items that the aggregate contains
 */
public record Aggregate(String name, Collection<String> dataItems) implements Vertex, DataContainer {

}
