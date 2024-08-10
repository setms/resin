package org.setms.resin.domain;

import org.setms.resin.graph.Graph;


class SoftwareProcessDependencies extends Graph {

    public SoftwareProcessDependencies() {
        super(new DependencyConstraints());
    }

}
