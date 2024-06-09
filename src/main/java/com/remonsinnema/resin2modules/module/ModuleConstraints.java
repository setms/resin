package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Constraints;
import com.remonsinnema.resin2modules.graph.Edge;
import com.remonsinnema.resin2modules.graph.Vertex;


public class ModuleConstraints implements Constraints {

    @Override
    public boolean canAddVertex(Vertex vertex) {
        return vertex instanceof Module;
    }

    @Override
    public boolean canAddEdge(Edge edge) {
        return true;
    }

}
