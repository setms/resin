package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.graph.Constraints;
import com.remonsinnema.resin2domains.graph.Edge;
import com.remonsinnema.resin2domains.graph.Vertex;


public class DomainConstraints implements Constraints {

    @Override
    public boolean canAddVertex(Vertex vertex) {
        return vertex instanceof Domain;
    }

    @Override
    public boolean canAddEdge(Edge edge) {
        return true;
    }

}
