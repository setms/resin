package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Edge;
import com.remonsinnema.resin2modules.graph.Transformation;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.*;

import java.util.stream.Stream;


class ProcessToDependencies implements Transformation<SoftwareProcess, SoftwareProcessDependencies> {

    @Override
    public SoftwareProcessDependencies apply(SoftwareProcess process) {
        var result = new SoftwareProcessDependencies();

        process.vertices()
                .filter(this::isDependencyNode)
                .forEach(result::vertex);
        result.vertices()
                .flatMap(vertex -> edgesFor(vertex, process))
                .forEach(e -> result.edge(e.from(), e.to()));

        return result;
    }

    private boolean isDependencyNode(Vertex vertex) {
        return Stream.of(Aggregate.class, ReadModel.class, AutomaticPolicy.class)
                .anyMatch(type -> type.isInstance(vertex));
    }

    private Stream<Edge> edgesFor(Vertex vertex, SoftwareProcess process) {
        return switch (vertex) {
            case Aggregate aggregate ->
                    edgesForAggregate(aggregate, process);
            case AutomaticPolicy policy ->
                edgesForPolicy(policy, process);
            default ->
                edgesForReadModel((ReadModel) vertex, process);
        };
    }

    private Stream<Edge> edgesForAggregate(Aggregate aggregate, SoftwareProcess process) {
        return process.edgesTo(aggregate)
                .filter(Command.class::isInstance)
                .flatMap(process::edgesTo)
                .filter(AutomaticPolicy.class::isInstance)
                .map(Policy.class::cast).map(policy -> new Edge(aggregate, policy));
    }

    private Stream<Edge> edgesForPolicy(Policy policy, SoftwareProcess process) {
        return process.edgesTo(policy)
                .filter(ReadModel.class::isInstance)
                .map(readModel -> new Edge(policy, readModel));
    }

    private Stream<Edge> edgesForReadModel(ReadModel readModel, SoftwareProcess process) {
        return process.edgesTo(readModel)
                .filter(DomainEvent.class::isInstance)
                .flatMap(process::edgesTo)
                .filter(Aggregate.class::isInstance)
                .map(Aggregate.class::cast)
                .filter(readModel::sharesDataItemsWith)
                .map(aggregate -> new Edge(readModel, aggregate));
    }

}
