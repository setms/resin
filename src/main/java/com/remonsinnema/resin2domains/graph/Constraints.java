package com.remonsinnema.resin2domains.graph;


public interface Constraints {

    boolean canAddVertex(Vertex vertex);

    boolean canAddEdge(Edge edge);

}
