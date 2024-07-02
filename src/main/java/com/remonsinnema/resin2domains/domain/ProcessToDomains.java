package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.graph.*;
import com.remonsinnema.resin2domains.process.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;


public class ProcessToDomains implements Transformation<SoftwareProcess, Domains> {

    @Override
    public Domains apply(SoftwareProcess process) {
        var result = new Domains();
        var dependencies = new ProcessToDependencies().apply(process);

        assignAggregatesToDomains(dependencies, result);
        assignReadModelsToDomains(dependencies, result);
        assignPoliciesToDomains(process, dependencies, result);
        addCommandsToDomainsContainingTheirAggregates(process, result);
        addEventsToDomainsContainingTheirAggregates(process, result);
        addUnassignedEventsToDomainsContainingTheirPolicies(process, result);

        addDependenciesBetweenDomains(process, result);

        return mergeDomains(result);
    }

    private void assignAggregatesToDomains(Graph graph, Domains domains) {
        graph.vertices(Aggregate.class)
                .map(Domain::from)
                .forEach(domains::add);
    }

    private void assignReadModelsToDomains(SoftwareProcessDependencies dependencies, Domains domains) {
        dependencies.vertices(ReadModel.class)
                .forEach(readModel -> addToDomain(readModel, dependencies, domains));
    }

    private void addToDomain(ReadModel readModel, SoftwareProcessDependencies dependencies, Domains domains) {
        var aggregateDomains = dependencies.edgesFrom(readModel)
                .map(domains::find)
                .flatMap(Optional::stream)
                .collect(toSet());
        if (aggregateDomains.size() == 1) {
            aggregateDomains.iterator().next().add(readModel);
            return;
        }
        domains.add(Domain.from(readModel));
    }

    private void assignPoliciesToDomains(SoftwareProcess process, SoftwareProcessDependencies dependencies,
            Domains domains) {
        dependencies.vertices(Policy.class)
                .forEach(policy -> addPolicyToDomain(policy, process, domains));

    }

    private void addPolicyToDomain(Policy policy, SoftwareProcess process, Domains domains) {
        var commandTargets = process.edgesFrom(policy)
                .filter(Command.class::isInstance)
                .flatMap(process::edgesFrom)
                .collect(toSet());
        if (commandTargets.size() > 1 || commandTargets.stream().anyMatch(ExternalSystem.class::isInstance)) {
            addPolicyToPrecedingAggregatesDomain(policy, process, domains);
            return;
        }
        var eventSources = process.edgesTo(policy)
                .filter(DomainEvent.class::isInstance)
                .flatMap(process::edgesTo)
                .collect(Collectors.toSet());
        switch (eventSources.size()) {
            case 0 -> addPolicyToFollowingAggregatesDomain(policy, process, domains);
            case 1 -> addPolicyToReadModelsDomain(policy, process, domains);
            default -> addPolicyToFollowingAggregatesDomain(policy, process, domains);
        }
    }

    private void addPolicyToPrecedingAggregatesDomain(Policy policy, SoftwareProcess process, Domains domains) {
        var precedingAggregatesDomains = process.edgesTo(policy)
                .filter(Event.class::isInstance)
                .flatMap(process::edgesTo)
                .filter(Aggregate.class::isInstance)
                .map(domains::find)
                .flatMap(Optional::stream)
                .collect(toSet());
        if (precedingAggregatesDomains.size() == 1) {
            precedingAggregatesDomains.iterator().next().add(policy);
            return;
        }
        domains.add(Domain.from(policy));
    }

    private void addPolicyToReadModelsDomain(Policy policy, SoftwareProcess process, Domains domains) {
        var readModelsDomains = process.edgesTo(policy)
                .filter(ReadModel.class::isInstance)
                .map(domains::find)
                .flatMap(Optional::stream)
                .collect(toSet());
        if (readModelsDomains.size() == 1) {
            readModelsDomains.iterator().next().add(policy);
            return;
        }
        if (process.edgesTo(policy)
                .filter(DomainEvent.class::isInstance)
                .count() > 1) {
            addPolicyToFollowingAggregatesDomain(policy, process, domains);
            return;
        }
        addPolicyToPrecedingAggregatesDomain(policy, process, domains);
    }

    private void addPolicyToFollowingAggregatesDomain(Policy policy, SoftwareProcess process, Domains domains) {
        var followingAggregatesDomains = process.edgesFrom(policy)
                .filter(Command.class::isInstance)
                .flatMap(process::edgesFrom)
                .filter(Aggregate.class::isInstance)
                .map(domains::find)
                .flatMap(Optional::stream)
                .collect(toSet());
        if (followingAggregatesDomains.size() == 1) {
            followingAggregatesDomains.iterator().next().add(policy);
            return;
        }
        domains.add(Domain.from(policy));
    }

    private void addCommandsToDomainsContainingTheirAggregates(SoftwareProcess process, Domains domains) {
        process.vertices()
                .filter(Command.class::isInstance)
                .forEach(command -> addToDomainContaining(command, process::edgesFrom, Aggregate.class, domains));
    }

    private void addToDomainContaining(Vertex vertex, Function<Vertex, Stream<Vertex>> edgesBy,
            Class<? extends Vertex> type, Domains domains) {
        var containingDomains = edgesBy.apply(vertex)
                .filter(type::isInstance)
                .map(domains::find)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        if (containingDomains.size() == 1) {
            containingDomains.iterator().next().add(vertex);
        }
    }

    private void addEventsToDomainsContainingTheirAggregates(SoftwareProcess process, Domains domains) {
        process.vertices()
                .filter(DomainEvent.class::isInstance)
                .forEach(event -> addToDomainContaining(event, process::edgesTo, Aggregate.class, domains));
    }

    private void addUnassignedEventsToDomainsContainingTheirPolicies(SoftwareProcess process, Domains domains) {
        process.vertices()
                .filter(DomainEvent.class::isInstance)
                .filter(not(domains::contains))
                .forEach(event -> addToDomainContainingItsPolicy(event, process, domains));
    }

    private void addToDomainContainingItsPolicy(Vertex event, SoftwareProcess process, Domains domains) {
        addToDomainContaining(event, process::edgesFrom, Policy.class, domains);
    }

    private void addDependenciesBetweenDomains(SoftwareProcess process, Domains domains) {
        addDependenciesBetweenDomains(process, Command.class, process::edgesTo, process::edgesFrom, domains);
        addDependenciesBetweenDomains(process, Event.class, process::edgesFrom, process::edgesTo, domains);
        addDependenciesBetweenPoliciesAndReadModels(process, domains);
    }

    private void addDependenciesBetweenDomains(SoftwareProcess process, Class<? extends Vertex> type,
            Function<Vertex, Stream<Vertex>> dependees, Function<Vertex, Stream<Vertex>> dependentsOn,
            Domains domains) {
        process.vertices()
                .filter(type::isInstance)
                .forEach(v -> addDependenciesBetweenDomains(v, dependees, dependentsOn, domains));
    }

    private void addDependenciesBetweenDomains(Vertex vertex, Function<Vertex, Stream<Vertex>> dependendees,
            Function<Vertex, Stream<Vertex>> dependentsOn, Domains domains) {
        dependendees.apply(vertex)
                .map(domains::find)
                .flatMap(Optional::stream)
                .forEach(dependee -> dependentsOn.apply(vertex)
                        .map(domains::find)
                        .flatMap(Optional::stream)
                        .filter(not(dependee::equals))
                        .forEach(dependentOn -> domains.edge(dependee, dependentOn)));
    }

    private void addDependenciesBetweenPoliciesAndReadModels(SoftwareProcess process, Domains domains) {
        domains.vertices(Domain.class).forEach(domain ->
                addDependenciesBetweenPoliciesAndReadModels(process, domain, domains));
    }

    private void addDependenciesBetweenPoliciesAndReadModels(SoftwareProcess process, Domain domain, Domains domains) {
        domain.contents(Policy.class)
                .flatMap(process::edgesTo)
                .map(domains::find)
                .flatMap(Optional::stream)
                .filter(not(domain::equals))
                .forEach(dependsOn -> domains.edge(domain, dependsOn));
    }

    private Domains mergeDomains(Domains domains) {
        return domains.cycles().stream()
                .sorted()
                .findFirst()
                .map(cycle -> merge(domains, cycle))
                .map(this::mergeDomains)
                .orElse(domains);
    }

    private Domains merge(Domains source, Cycle cycle) {
        var result = new Domains();
        source.vertices()
                .filter(not(cycle::contains))
                .toList().forEach(result::vertex);
        var mergedDomain = merge(cycle);
        result.vertex(mergedDomain);
        source.edges()
                .map(edge -> merge(edge, cycle, mergedDomain))
                .filter(Objects::nonNull)
                .forEach(edge -> result.edge(edge.from(), edge.to()));
        return result;
    }

    private Domain merge(Cycle cycle) {
        var name = cycle.vertices().stream()
                .map(Domain.class::cast)
                .map(Domain::contents)
                .flatMap(Collection::stream)
                .filter(Aggregate.class::isInstance)
                .map(Vertex::name)
                .sorted()
                .collect(joining("And"));
        return new Domain(name, new HashSet<>(cycle.vertices()));
    }

    private Edge merge(Edge source, Cycle cycle, Domain cycleDomain) {
        var from = merge(source.from(), cycle, cycleDomain);
        var to = merge(source.to(), cycle, cycleDomain);
        return from == to ? null : new Edge(from, to);
    }

    private Vertex merge(Vertex vertex, Cycle cycle, Domain cycleDomain) {
        return cycle.contains(vertex) ? cycleDomain : vertex;
    }

}
