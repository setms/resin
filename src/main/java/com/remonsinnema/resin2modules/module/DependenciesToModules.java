package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Transformation;
import com.remonsinnema.resin2modules.graph.Vertex;
import com.remonsinnema.resin2modules.process.Aggregate;
import com.remonsinnema.resin2modules.process.AutomaticPolicy;
import com.remonsinnema.resin2modules.process.ReadModel;

import java.util.Optional;

import static java.util.function.Predicate.not;


class DependenciesToModules implements Transformation<SoftwareProcessDependencies, Modules> {

    @Override
    public Modules apply(SoftwareProcessDependencies dependencies) {
        var result = new Modules();

        addModuleForEachUnassignedAggregate(dependencies, result);
        assignReadModelToModuleContainingAggregate(dependencies, result);
        assignPolicyToModuleContainingReadModel(dependencies, result);

        return result;
    }

    private void addModuleForEachUnassignedAggregate(SoftwareProcessDependencies dependencies, Modules modules) {
        dependencies.vertices()
                .filter(Aggregate.class::isInstance)
                .filter(not(modules::contains))
                .map(Module::from)
                .forEach(modules::vertex);
    }

    private void assignReadModelToModuleContainingAggregate(SoftwareProcessDependencies dependencies, Modules modules) {
        dependencies.vertices()
                .filter(ReadModel.class::isInstance)
                .filter(not(modules::contains))
                .forEach(rm -> assignIfAllDependentAggregatesAreInSameModule(dependencies, modules, rm));
    }

    private void assignIfAllDependentAggregatesAreInSameModule(SoftwareProcessDependencies dependencies,
            Modules modules, Vertex rm) {
        var aggregateModules = dependencies.edgesFrom(rm)
                .filter(Aggregate.class::isInstance)
                .map(modules::find)
                .flatMap(Optional::stream)
                .toList();
        if (aggregateModules.size() == 1) {
            aggregateModules.getFirst().add(rm);
        }
    }

    private void assignPolicyToModuleContainingReadModel(SoftwareProcessDependencies dependencies, Modules modules) {
        dependencies.vertices()
                .filter(AutomaticPolicy.class::isInstance)
                .map(AutomaticPolicy.class::cast)
                .filter(not(modules::contains))
                .forEach(policy -> assignPolicyToModuleContainingReadModel(policy, dependencies, modules));
    }

    private void assignPolicyToModuleContainingReadModel(AutomaticPolicy policy,
            SoftwareProcessDependencies dependencies, Modules modules) {
        var readModelModules = dependencies.edgesFrom(policy)
                .filter(ReadModel.class::isInstance)
                .map(modules::find)
                .flatMap(Optional::stream)
                .toList();
        if (readModelModules.size() == 1) {
            readModelModules.getFirst().add(policy);
        }
    }

}
