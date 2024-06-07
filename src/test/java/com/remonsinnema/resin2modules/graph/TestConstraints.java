package com.remonsinnema.resin2modules.graph;


public class TestConstraints implements Constraints {

    @Override
    public boolean canAddVertex(Vertex vertex) {
        return vertex instanceof TestVertex;
    }

    @Override
    public boolean canAddEdge(Edge edge) {
        return canAddVertex(edge.from())
                && canAddVertex(edge.to())
                && !edge.from().equals(edge.to());
    }

}
