package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Transformation;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;


public class ProcessToModules implements Transformation<SoftwareProcess, Modules> {

    @Override
    public Modules apply(SoftwareProcess process) {
        var dependencies = new ProcessToDependencies().apply(process);

        var result = new DependenciesToModules().apply(dependencies);

        addCommandsToModulesContainingTheirAggregates(process, result);
        addEventsToModulesContainingTheirAggregates(process, result);
        addUnassignedEventsToModulesContainingTheirPolicies(process, result);

        addModuleDependencies(process, result);

        return result;
    }

    private void addCommandsToModulesContainingTheirAggregates(SoftwareProcess process, Modules modules) {
        process.vertices()
                .filter(Command.class::isInstance)
                .forEach(command -> addToModuleContaining(command, process::edgesFrom, Aggregate.class, modules));
    }

    private void addToModuleContaining(Vertex vertex, Function<Vertex, Stream<Vertex>> edgesBy,
            Class<? extends Vertex> type, Modules modules) {
        var containingModules = edgesBy.apply(vertex)
                .filter(type::isInstance)
                .map(modules::find)
                .flatMap(Optional::stream)
                .toList();
        if (containingModules.size() == 1) {
            containingModules.getFirst().add(vertex);
        }
    }

    private void addEventsToModulesContainingTheirAggregates(SoftwareProcess process, Modules modules) {
        process.vertices()
                .filter(DomainEvent.class::isInstance)
                .forEach(event -> addToModuleContaining(event, process::edgesTo, Aggregate.class, modules));
    }

    private void addUnassignedEventsToModulesContainingTheirPolicies(SoftwareProcess process, Modules modules) {
        process.vertices()
                .filter(DomainEvent.class::isInstance)
                .filter(not(modules::contains))
                .forEach(event -> addToModuleContainingItsPolicy(event, process, modules));
    }

    private void addToModuleContainingItsPolicy(Vertex event, SoftwareProcess process, Modules modules) {
        addToModuleContaining(event, process::edgesFrom, Policy.class, modules);
    }

    private void addModuleDependencies(SoftwareProcess process, Modules modules) {
        addModuleDependencies(process, Command.class, process::edgesTo, process::edgesFrom, modules);
        addModuleDependencies(process, Event.class, process::edgesFrom, process::edgesTo, modules);
    }

    private void addModuleDependencies(SoftwareProcess process, Class<? extends Vertex> type,
            Function<Vertex, Stream<Vertex>> dependees, Function<Vertex, Stream<Vertex>> dependentsOn, Modules modules) {
        process.vertices()
                .filter(type::isInstance)
                .forEach(v -> addModuleDependencies(v, dependees, dependentsOn, modules));
    }

    private void addModuleDependencies(Vertex vertex, Function<Vertex, Stream<Vertex>> dependendees,
            Function<Vertex, Stream<Vertex>> dependentsOn, Modules modules) {
        dependendees.apply(vertex)
                .map(modules::find)
                .flatMap(Optional::stream)
                .forEach(dependee -> dependentsOn.apply(vertex)
                        .map(modules::find)
                        .flatMap(Optional::stream)
                        .filter(not(dependee::equals))
                        .forEach(dependentOn -> modules.edge(dependee, dependentOn)));
    }

}
