package org.setms.resin.process;

import org.setms.resin.graph.Graph;
import org.setms.resin.graph.Vertex;


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
