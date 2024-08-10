package org.setms.resin.process;

import org.setms.resin.graph.Vertex;


/**
 * A human interacting with the system.
 * @param name the name or role of the person
 */
public record Person(String name) implements Vertex {

}
