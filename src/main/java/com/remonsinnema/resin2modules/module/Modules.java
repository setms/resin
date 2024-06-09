package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Graph;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.Aggregate;

import java.util.Optional;
import java.util.stream.Stream;


public class Modules extends Graph {

    public Modules() {
        super(new ModuleConstraints());
    }

    public boolean contains(Vertex vertex) {
        return modules()
                .anyMatch(m -> m.contains(vertex));
    }

    private Stream<Module> modules() {
        return vertices().map(Module.class::cast);
    }

    public Optional<Module> find(Vertex vertex) {
        return modules()
                .filter(m -> m.contains(vertex))
                .findAny();
    }
}
