package com.remonsinnema.resin2modules.process;

import com.remonsinnema.resin2modules.graph.Graph;
import lombok.Getter;


@Getter
public class SoftwareProcess extends Graph {

    private final String name;

    public SoftwareProcess(String name) {
        super(new SoftwareProcessConstraints());
        this.name = name;
    }

}
