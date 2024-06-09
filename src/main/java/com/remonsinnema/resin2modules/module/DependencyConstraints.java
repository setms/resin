package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Constraints;
import com.remonsinnema.resin2modules.graph.Edge;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.Aggregate;
import com.remonsinnema.resin2modules.process.Policy;
import com.remonsinnema.resin2modules.process.ReadModel;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


class DependencyConstraints implements Constraints {

    private final Map<Class<? extends Vertex>, Class<? extends Vertex>> allowedPredecessors = Map.of(
            Aggregate.class, ReadModel.class,
            Policy.class, Aggregate.class,
            ReadModel.class, Policy.class
    );

    @Override
    public boolean canAddVertex(Vertex vertex) {
        return recognizedTypeOf(vertex).isPresent();
    }

    private Optional<Class<? extends Vertex>> recognizedTypeOf(Vertex vertex) {
        return allowedPredecessors.keySet()
                .stream()
                .filter(isInstance(vertex))
                .findAny();
    }

    private Predicate<Class<? extends Vertex>> isInstance(Vertex vertex) {
        return type -> type.isInstance(vertex);
    }

    @Override
    public boolean canAddEdge(Edge edge) {
        return recognizedTypeOf(edge.to())
                .map(allowedPredecessors::get)
                .stream()
                .anyMatch(isInstance(edge.from()));
    }

}
