package org.setms.resin.process;

import org.setms.resin.graph.Vertex;

import java.util.Collection;


/**
 * A representation of data specifically structured and optimized for querying and reading by a {@linkplain Person} or
 * {@linkplain Policy}.
 */
public record ReadModel(String name, Collection<String> dataItems) implements Vertex, DataContainer {

}
