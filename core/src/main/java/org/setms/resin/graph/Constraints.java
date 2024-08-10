package org.setms.resin.graph;


/**
 * Constraints for adding vertices and edges to a graph.
 */
public interface Constraints {

    boolean canAddVertex(Vertex vertex);

    boolean canAddEdge(Edge edge);

}
