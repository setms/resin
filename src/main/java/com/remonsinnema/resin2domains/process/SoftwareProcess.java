package com.remonsinnema.resin2domains.process;

import com.remonsinnema.resin2domains.graph.Graph;
import com.remonsinnema.resin2domains.graph.Vertex;


public class SoftwareProcess extends Graph {

    public SoftwareProcess() {
        super(new SoftwareProcessConstraints());
    }

    public <T extends Vertex> T element(T vertex) {
        return vertex(vertex);
    }

    public void connect(Vertex... steps) {
        edges(steps);
    }

}
