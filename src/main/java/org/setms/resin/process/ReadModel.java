package org.setms.resin.process;

import org.setms.resin.graph.Vertex;

import java.util.Collection;


public record ReadModel(String name, Collection<String> dataItems) implements Vertex, DataContainer {

}
