package org.setms.resin.process;

import org.setms.resin.graph.Vertex;


/**
 * A message that represents the intention to perform an action or trigger a state change in the system.
 * @param name the name of the command
 */
public record Command(String name) implements Vertex {

}
