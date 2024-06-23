package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.graph.Graph;


class SoftwareProcessDependencies extends Graph {

    public SoftwareProcessDependencies() {
        super(new DependencyConstraints());
    }

}
