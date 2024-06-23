package com.remonsinnema.resin2domains.process;

import com.remonsinnema.resin2domains.graph.Constraints;
import com.remonsinnema.resin2domains.graph.Edge;
import com.remonsinnema.resin2domains.graph.Vertex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


class SoftwareProcessConstraints implements Constraints {

    private final Map<Class<? extends Vertex>, List<Class<? extends Vertex>>> allowedPredecessors = Map.of(
        Aggregate.class, List.of(Command.class),
        Command.class, List.of(Person.class, Policy.class, ExternalSystem.class),
        Event.class, List.of(Aggregate.class, ExternalSystem.class),
        ClockEvent.class, List.of(),
        ExternalSystem.class, List.of(Person.class, Event.class),
        Person.class, List.of(ReadModel.class),
        AutomaticPolicy.class, List.of(DomainEvent.class, ReadModel.class),
        ManualPolicy.class, List.of(DomainEvent.class, Person.class),
        ReadModel.class, List.of(Event.class)
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
                .flatMap(Collection::stream)
                .anyMatch(isInstance(edge.from()));
    }

}
