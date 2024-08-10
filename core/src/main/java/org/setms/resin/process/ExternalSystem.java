package org.setms.resin.process;

import org.setms.resin.graph.Vertex;


/**
 * System outside the bounded context that interacts with the system.
 * @param name the name of the external system
 */
public record ExternalSystem(String name) implements Vertex {

}
