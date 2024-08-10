package org.setms.resin.domain;

import org.setms.resin.graph.Constraints;
import org.setms.resin.graph.Edge;
import org.setms.resin.graph.Vertex;


class DomainConstraints implements Constraints {

    @Override
    public boolean canAddVertex(Vertex vertex) {
        return vertex instanceof Domain;
    }

    @Override
    public boolean canAddEdge(Edge edge) {
        return true;
    }

}
