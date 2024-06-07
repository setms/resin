package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.dependency.SoftwareProcessDependencies;
import com.remonsinnema.resin2modules.graph.Transformation;


public class DependenciesToModules implements Transformation<SoftwareProcessDependencies, Modules> {

    @Override
    public Modules apply(SoftwareProcessDependencies dependencies) {
        var result = new Modules();

        // TODO: Implement

        return result;
    }

}
