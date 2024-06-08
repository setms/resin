package com.remonsinnema.resin2modules;

import com.remonsinnema.resin2modules.dependency.ProcessToDependencies;
import com.remonsinnema.resin2modules.graph.Graph;
import com.remonsinnema.resin2modules.mermaid.MermaidRepresentation;
import com.remonsinnema.resin2modules.graph.Representation;
import com.remonsinnema.resin2modules.module.DependenciesToModules;
import com.remonsinnema.resin2modules.process.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;


@Disabled
class WhenDiscoveringModules {

    private final Representation representation = new MermaidRepresentation();

    @Test
    void shouldDiscoverGdprMiddlewareModules() {
        var process = givenGdprProcess();
        var dependencies = new ProcessToDependencies().apply(process);

        var modules = new DependenciesToModules().apply(dependencies);

        log(modules);
    }

    private SoftwareProcess givenGdprProcess() {
        var process = new SoftwareProcess();

        var user = process.vertex(new Person("User"));
        var customerSupport = process.vertex(new Person("CustomerSupport"));
        var app = process.vertex(new ExternalSystem("App"));
        var service = process.vertex(new ExternalSystem("Service"));
        var deleteMyData = process.vertex(new Command("DeleteMyData"));
        var deleteData = process.vertex(new Command("DeleteData"));
        var remindService = process.vertex(new Command("RemindService"));
        var informUser = process.vertex(new Command("InformUser"));
        var dataDeletionRequestForm = process.vertex(new Aggregate("DataDeletionRequestForm", List.of("request")));
        var services = process.vertex(new Aggregate("Services", List.of("service")));
        var notifications = process.vertex(new Aggregate("Notifications", List.of("notification")));
        var deletionsInProgress = process.vertex(new ReadModel("DeletionsInProgress", List.of("deletion", "service")));
        var dataDeletionCompletion = process.vertex(new ReadModel("DataDeletionCompletion", List.of("completion", "notification")));
        var dataDeletionRequested = process.vertex(new Event("DataDeletionRequested"));
        var dataDeletionStarted = process.vertex(new Event("DataDeletionStarted"));
        var timePassed = process.vertex(new ClockEvent("TimePassed"));
        var dataDeletionRequestedInService = process.vertex(new Event("DataDeletionRequestedInService"));
        var dataDeletedInService = process.vertex(new Event("DataDeletedInService"));
        var dataDeleted = process.vertex(new Event("DataDeleted"));
        var checkUserIdentity = process.vertex(new ManualPolicy("CheckUserIdentity"));
        var checkUnresponsiveService = process.vertex(new AutomaticPolicy("CheckUnresponsiveService"));
        var checkRequestComplete = process.vertex(new AutomaticPolicy("CheckRequestComplete"));

        process.edges(user, app, deleteData, services, dataDeletionStarted, service, dataDeletedInService,
                checkRequestComplete, informUser, notifications, dataDeleted, dataDeletionCompletion, user);
        process.edges(user, deleteMyData, dataDeletionRequestForm, dataDeletionRequested, checkUserIdentity, deleteData);
        process.edge(customerSupport, checkUserIdentity);
        process.edges(dataDeletionStarted, deletionsInProgress, checkUnresponsiveService);
        process.edges(timePassed, checkUnresponsiveService, remindService, services, dataDeletionRequestedInService,
                service);
        process.edge(deletionsInProgress, checkRequestComplete);
        process.edge(dataDeleted, deletionsInProgress);

        return process;
    }

    private void log(Graph graph) {
        System.out.println(representation.apply(graph));
    }

}
