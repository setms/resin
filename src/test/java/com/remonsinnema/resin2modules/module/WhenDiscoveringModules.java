package com.remonsinnema.resin2modules.module;

import com.remonsinnema.resin2modules.graph.Edge;
import com.remonsinnema.resin2modules.process.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenDiscoveringModules {

    @Test
    void shouldOnlyAllowModulesAsVertices() {
        var modules = new Modules();
        modules.vertex(new Module("module", emptySet()));
        assertThrows(IllegalArgumentException.class, () ->
                modules.vertex(new AutomaticPolicy("apl")));
    }

    @Test
    void shouldOnlyAllowEdgesBetweenModules() {
        var modules = new Modules();
        modules.vertex(new Module("module", emptySet()));
        assertThrows(IllegalArgumentException.class, () ->
                modules.vertex(new AutomaticPolicy("apl")));
    }

    @Test
    void shouldAddEverythingInCycleToSameModule() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var rdm = process.vertex(new ReadModel("rdm", List.of("data")));
        process.edges(rdm, apl, cmd, agg, evt, rdm);

        var modules = new ProcessToModules().apply(process);

        assertThat(modules.vertices().toList(), contains(new Module(agg.name(), Set.of(cmd, agg, evt, apl, rdm))));
    }

    @Test
    void shouldAddPolicyToModuleContainingAggregate() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        process.edges(apl, cmd, agg);

        var modules = new ProcessToModules().apply(process);

        assertThat(modules.vertices().toList(), contains(new Module(agg.name(), Set.of(cmd, agg, apl))));
    }

    @Test
    void shouldAssignModuleDependenciesViaEvent() {
        var process = new SoftwareProcess();
        var usr = process.vertex(new Person("usr"));
        var cmd1 = process.vertex(new Command("cmd1"));
        var agg1 = process.vertex(new Aggregate("agg1", List.of("data1")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd2 = process.vertex(new Command("cmd2"));
        var agg2 = process.vertex(new Aggregate("agg2", List.of("data2")));
        process.edges(usr, cmd1, agg1, evt, apl, cmd2, agg2);

        var modules = new ProcessToModules().apply(process);

        assertThat(modules.vertices().count(), is(2L));
        var module1 = modules.find(agg1).orElseThrow();
        var module2 = modules.find(agg2).orElseThrow();
        assertThat(module1, not(module2));
        assertThat(modules.edges().count(), is(1L));
        var moduleDependency = modules.edges().findFirst().orElseThrow();
        assertThat(moduleDependency.from(), is(module2));
        assertThat(moduleDependency.to(), is(module1));
    }

    @Test
    void shouldAssignModuleDependenciesViaCommand() {
        var process = new SoftwareProcess();
        var usr = process.vertex(new Person("usr"));
        var cmd1 = process.vertex(new Command("cmd1"));
        var agg1 = process.vertex(new Aggregate("agg1", List.of("data1")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var rdm = process.vertex(new ReadModel("rdm", List.of("data1")));
        var cmd2 = process.vertex(new Command("cmd2"));
        var agg2 = process.vertex(new Aggregate("agg2", List.of("data2")));
        process.edges(usr, cmd1, agg1, evt, apl, cmd2, agg2);
        process.edge(evt, rdm);
        process.edge(rdm, apl);

        var modules = new ProcessToModules().apply(process);

        assertThat(modules.vertices().count(), is(2L));
        var module1 = modules.find(agg1).orElseThrow();
        var module2 = modules.find(agg2).orElseThrow();
        assertThat(module1, not(module2));
        assertThat(modules.edges().count(), is(1L));
        var moduleDependency = modules.edges().findFirst().orElseThrow();
        assertThat(moduleDependency.from(), is(module1));
        assertThat(moduleDependency.to(), is(module2));
    }

    @Test
    void shouldDiscoverGdprMiddlewareModules() {
        var process = new SoftwareProcess();

        var user = process.vertex(new Person("User"));
        var customerSupport = process.vertex(new Person("CustomerSupport"));
        var app = process.vertex(new ExternalSystem("App"));
        var service = process.vertex(new ExternalSystem("Service"));
        var deleteMyData = process.vertex(new Command("DeleteMyData"));
        var deleteData = process.vertex(new Command("DeleteData"));
        var remindService = process.vertex(new Command("RemindService"));
        var informUser = process.vertex(new Command("InformUser"));
        var dataDeletionRequestForm = process.vertex(
                new Aggregate("DataDeletionRequestForm", List.of("request")));
        var services = process.vertex(new Aggregate("Services", List.of("service")));
        var notifications = process.vertex(new Aggregate("Notifications", List.of("notification")));
        var deletionsInProgress = process.vertex(
                new ReadModel("DeletionsInProgress", List.of("deletion", "service")));
        var dataDeletionCompletion = process.vertex(
                new ReadModel("DataDeletionCompletion", List.of("completion", "notification")));
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

        var modules = new ProcessToModules().apply(process);

        var formModule = new Module("DataDeletionRequestForm", Set.of(dataDeletionRequestForm, deleteMyData,
                dataDeletionRequested));
        var servicesModule = new Module("Services", Set.of(services, checkUnresponsiveService,
                deletionsInProgress, checkRequestComplete, deleteData, remindService, timePassed, dataDeletionStarted,
                dataDeletionRequestedInService, dataDeletedInService));
        var notificationsModule = new Module("Notifications", Set.of(notifications, dataDeletionCompletion,
                informUser, dataDeleted));
        assertThat(modules.vertices().toList(), containsInAnyOrder(servicesModule, notificationsModule, formModule));
        assertThat(modules.edges().toList(), contains(
                new Edge(servicesModule, notificationsModule)));
    }

    @Test
    void shouldDiscoverOnlineClassifiedsModules() {
        var process = new SoftwareProcess();
        var seller = process.vertex(new Person("Seller"));
        var signUp = process.vertex(new Command("SignUp"));
        var users = process.vertex(new Aggregate("Users", List.of("users")));
        var userAdded = process.vertex(new Event("UserAdded"));
        var userPreferences = process.vertex(new ReadModel("UserPreferences",
                List.of("users", "preferences")));
        process.edges(seller, signUp, users, userAdded, userPreferences);
        var buyer = process.vertex(new Person("Buyer"));
        process.edge(buyer, signUp);
        var createAd = process.vertex(new Command("CreateAd"));
        var ads = process.vertex(new Aggregate("Ads", List.of("ads")));
        var adProposed = process.vertex(new Event("AdProposed"));
        var rules = process.vertex(new ReadModel("Rules", List.of("rules")));
        var checkViolations = process.vertex(new AutomaticPolicy("CheckViolations"));
        var acceptAd = process.vertex(new Command("AcceptAd"));
        var adAccepted = process.vertex(new Event("AdAccepted"));
        var myAds = process.vertex(new ReadModel("MyAds", List.of("ads")));
        var checkIndex = process.vertex(new AutomaticPolicy("CheckIndex"));
        var addAdToIndex = process.vertex(new Command("AddAdToIndex"));
        var index = process.vertex(new Aggregate("Index", List.of("searchable_ads")));
        var indexUpdated = process.vertex(new Event("IndexUpdated"));
        var searchableAds = process.vertex(new ReadModel("SearchableAds", List.of("searchable_ads")));
        process.edges(seller, createAd, ads, adProposed, checkViolations, acceptAd, ads, adAccepted, checkIndex,
                addAdToIndex, index, indexUpdated, searchableAds);
        process.edge(rules, checkViolations);
        process.edge(adAccepted, myAds);
        var rejectAd = process.vertex(new Command("RejectAd"));
        var adRejected = process.vertex(new Event("AdRejected"));
        var checkUserPreferences = process.vertex(new AutomaticPolicy("CheckUserPreferences"));
        var informUser = process.vertex(new Command("InformUser"));
        var notifications = process.vertex(new Aggregate("Notifications", List.of("notifications")));
        var notificationSent = process.vertex(new Event("NotificationSent"));
        var notification = process.vertex(new ReadModel("Notification", List.of("notifications")));
        process.edges(checkViolations, rejectAd, ads, adRejected, checkUserPreferences, informUser, notifications,
                notificationSent, notification, seller);
        process.edge(userPreferences, checkUserPreferences);
        var tnsManager = process.vertex(new Person("TnsManager"));
        var defineRule = process.vertex(new Command("DefineRule"));
        var moderation = process.vertex(new Aggregate("Moderation", List.of("rules")));
        var ruleDefined = process.vertex(new Event("RuleDefined"));
        process.edges(tnsManager, defineRule, moderation, ruleDefined, rules);
        var deleteAd = process.vertex(new Command("DeleteAd"));
        var adDeleted = process.vertex(new Event("AdDeleted"));
        var removeAdFromIndex = process.vertex(new Command("RemoveAdFromIndex"));
        process.edges(myAds, seller, deleteAd, ads, adDeleted, checkIndex, removeAdFromIndex, index);
        var changeAd = process.vertex(new Command("ChangeAd"));
        var adChanged = process.vertex(new Event("AdChanged"));
        var updateAdInIndex = process.vertex(new Command("UpdateAdInIndex"));
        process.edges(seller, changeAd, ads, adChanged, checkIndex, updateAdInIndex, index);
        var adAged = process.vertex(new ClockEvent("AdAged"));
        var decreaseAdRelevancy = process.vertex(new Command("DecreaseAdRelevancy"));
        process.edges(adAged, checkIndex, decreaseAdRelevancy, index);
        var bumpAd = process.vertex(new Command("BumpAd"));
        var adBumped = process.vertex(new Event("AdBumped"));
        var increaseAdRelevancy = process.vertex(new Command("IncreaseAdRelevancy"));
        process.edges(seller, bumpAd, ads, adBumped, checkIndex, increaseAdRelevancy, index);
        var makeOffer = process.vertex(new Command("MakeOffer"));
        var trades = process.vertex(new Aggregate("Trades", List.of("offers")));
        var offerMade = process.vertex(new Event("OfferMade"));
        var offers = process.vertex(new ReadModel("Offers", List.of("offers")));
        process.edges(searchableAds, buyer, makeOffer, trades, offerMade, offers);
        process.edge(offerMade, checkUserPreferences);
        var acceptOffer = process.vertex(new Command("AcceptOffer"));
        var offerAccepted = process.vertex(new Event("OfferAccepted"));
        var checkAcceptedOffer = process.vertex(new AutomaticPolicy("CheckAcceptedOffer"));
        var closeAd = process.vertex(new Command("CloseAd"));
        var adClosed = process.vertex(new Event("AdClosed"));
        process.edges(offers, seller, acceptOffer, trades, offerAccepted, checkAcceptedOffer, closeAd, ads, adClosed,
                checkIndex);
        process.edge(offerAccepted, checkUserPreferences);
        var rejectOffer = process.vertex(new Command("RejectOffer"));
        var offerRejected = process.vertex(new Event("OfferRejected"));
        process.edges(seller, rejectOffer, trades, offerRejected, checkUserPreferences);
        process.edge(notification, buyer);

        var modules = new ProcessToModules().apply(process);

        assertThat(modules.vertices().count(), is(6L));
    }

}
