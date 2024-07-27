package org.setms.resin.graph;


public interface Constraints {

    boolean canAddVertex(Vertex vertex);

    boolean canAddEdge(Edge edge);

}
