package com.remonsinnema.resin2domains.domain;

import com.remonsinnema.resin2domains.graph.Edge;
import com.remonsinnema.resin2domains.mermaid.MermaidRepresentation;
import com.remonsinnema.resin2domains.process.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WhenDiscoveringDomains {

    private final MermaidRepresentation representation = new MermaidRepresentation();

    @Test
    void shouldOnlyAllowDomainsAsVertices() {
        var domains = new Domains();
        domains.add(new Domain("Domain", emptySet()));
        assertThrows(IllegalArgumentException.class, () ->
                domains.vertex(new AutomaticPolicy("apl")));
    }

    @Test
    void shouldOnlyAllowEdgesBetweenDomains() {
        var domains = new Domains();
        domains.add(new Domain("Domain", emptySet()));
        assertThrows(IllegalArgumentException.class, () ->
                domains.vertex(new AutomaticPolicy("apl")));
    }

    @Test
    void shouldAddEverythingInCycleToSameDomain() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var rdm = process.vertex(new ReadModel("rdm", List.of("data")));
        process.edges(rdm, apl, cmd, agg, evt, rdm);

        var domains = new ProcessToDomains().apply(process);

        assertThat(domains.vertices().toList(), contains(new Domain(agg.name(), Set.of(cmd, agg, evt, apl, rdm))));
    }

    @Test
    void shouldAddPolicyToDomainContainingAggregate() {
        var process = new SoftwareProcess();
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        process.edges(apl, cmd, agg);

        var domains = new ProcessToDomains().apply(process);

        assertThat(domains.vertices().toList(), contains(new Domain(agg.name(), Set.of(cmd, agg, apl))));
    }

    @Test
    void shouldAssignDomainDependenciesViaEvent() {
        var process = new SoftwareProcess();
        var usr = process.vertex(new Person("usr"));
        var cmd1 = process.vertex(new Command("cmd1"));
        var agg1 = process.vertex(new Aggregate("agg1", List.of("data1")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd2 = process.vertex(new Command("cmd2"));
        var agg2 = process.vertex(new Aggregate("agg2", List.of("data2")));
        process.edges(usr, cmd1, agg1, evt, apl, cmd2, agg2);

        var domains = new ProcessToDomains().apply(process);
        log(domains);

        assertThat(domains.vertices().count(), is(2L));
        var domain1 = domains.find(agg1).orElseThrow();
        var domain2 = domains.find(agg2).orElseThrow();
        assertThat(domain1, not(domain2));
        assertThat(domains.edges().count(), is(1L));
        var domainDependency = domains.edges().findFirst().orElseThrow();
        assertThat(domainDependency.from(), is(domain1));
        assertThat(domainDependency.to(), is(domain2));
    }

    @Test
    void shouldAssignDomainDependenciesViaCommand() {
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

        var domains = new ProcessToDomains().apply(process);

        assertThat(domains.vertices().count(), is(2L));
        var domain1 = domains.find(agg1).orElseThrow();
        var domain2 = domains.find(agg2).orElseThrow();
        assertThat(domain1, not(domain2));
        assertThat(domains.edges().count(), is(1L));
        var domainDependency = domains.edges().findFirst().orElseThrow();
        assertThat(domainDependency.from(), is(domain1));
        assertThat(domainDependency.to(), is(domain2));
    }

    @Test
    void shouldAssignReadModelToOwnDomainWhenNotFedBySingleAggregate() {
        var process = new SoftwareProcess();
        var exs = process.vertex(new ExternalSystem("exs"));
        var evt = process.vertex(new Event("evt"));
        var rdm = process.vertex(new ReadModel("rdm", List.of("data")));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        process.edges(exs, evt, rdm, apl);
        process.edges(evt, apl, cmd, agg);

        var domains = new ProcessToDomains().apply(process);

        assertThat(domains.vertices().count(), is(2L));
        var readModelDomain = domains.find(rdm);
        assertThat(readModelDomain.isPresent(), is(true));
        assertThat(readModelDomain, not(is(domains.find(agg))));
    }

    @Test
    void shouldAssignPolicyToDomainOfPrecedingAggregateIfItsCommandIsAcceptedByAnExternalSystem() {
        var process = new SoftwareProcess();
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        var evt = process.vertex(new Event("evt"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd = process.vertex(new Command("cmd"));
        var exs = process.vertex(new ExternalSystem("exs"));
        process.edges(agg, evt, apl, cmd, exs);

        var domains = new ProcessToDomains().apply(process);

        var policyDomain = domains.find(apl);
        assertThat(policyDomain.isPresent(), is(true));
        assertThat(policyDomain, is(domains.find(agg)));
    }

    @Test
    void shouldAssignPolicyToDomainOfFollowingAggregateIfItHandlesEventsFromMultipleDomains() {
        var process = new SoftwareProcess();
        var agg1 = process.vertex(new Aggregate("agg1", List.of("data1")));
        var evt1 = process.vertex(new Event("evt1"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var agg2 = process.vertex(new Aggregate("agg2", List.of("data2")));
        var evt2 = process.vertex(new Event("evt2"));
        var cmd = process.vertex(new Command("cmd"));
        var agg = process.vertex(new Aggregate("agg", List.of("data")));
        process.edges(agg1, evt1, apl, cmd, agg);
        process.edges(agg2, evt2, apl);

        var domains = new ProcessToDomains().apply(process);

        var policyDomain = domains.find(apl);
        assertThat(policyDomain.isPresent(), is(true));
        assertThat(policyDomain, is(domains.find(agg)));
    }

    @Test
    void shouldAssignPolicyToOwnDomainIfItMediatesExternalSystems() {
        var process = new SoftwareProcess();
        var exs1 = process.vertex(new ExternalSystem("exs1"));
        var evt1 = process.vertex(new Event("evt1"));
        var exs2 = process.vertex(new ExternalSystem("exs2"));
        var evt2 = process.vertex(new Event("evt2"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd = process.vertex(new Command("cmd"));
        var exs3 = process.vertex(new ExternalSystem("exs3"));
        process.edges(exs1, evt1, apl, cmd, exs3);
        process.edges(exs2, evt2, apl);

        var domains = new ProcessToDomains().apply(process);

        var policyDomain = domains.find(apl);
        assertThat(policyDomain.isPresent(), is(true));
        assertThat(policyDomain.get().contents(), containsInAnyOrder(evt1, evt2, apl));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void shouldAddDependenciesBetweenDomainsBasedOnEvents() {
        var process = new SoftwareProcess();
        var agg1 = process.vertex(new Aggregate("agg1", List.of("data1")));
        var evt1 = process.vertex(new Event("evt1"));
        var agg2 = process.vertex(new Aggregate("agg2", List.of("data2")));
        var evt2 = process.vertex(new Event("evt2"));
        var apl = process.vertex(new AutomaticPolicy("apl"));
        var cmd = process.vertex((new Command("cmd")));
        var exs = process.vertex(new ExternalSystem("exs"));
        process.edges(agg1, evt1, apl, cmd, exs);
        process.edges(agg2, evt2, apl);

        var domains = new ProcessToDomains().apply(process);

        var aplDomain = domains.find(apl).get();
        var agg1Domain = domains.find(agg1).get();
        var agg2Domain = domains.find(agg2).get();
        assertThat(domains.edges().anyMatch(e -> e.from().equals(aplDomain) && e.to().equals(agg1Domain)), is(true));
        assertThat(domains.edges().anyMatch(e -> e.from().equals(aplDomain) && e.to().equals(agg2Domain)), is(true));
    }

    @Test
    void shouldMergeDomainsOnDependencyCycleIntoOne() {
        var process = new SoftwareProcess();
        var admin = process.vertex(new Person("admin"));
        var createCustomer = process.vertex(new Command("createCustomer"));
        var customers = process.vertex(new Aggregate("Customers", List.of("customer")));
        var customerCreated = process.vertex(new Event("customerCreated"));
        var checkBaseLocation = process.vertex(new AutomaticPolicy("checkBaseLocation"));
        var createLocation = process.vertex(new Command("createLocation"));
        var locations = process.vertex(new Aggregate("Locations", List.of("location")));
        var locationCreated = process.vertex(new Event("locationCreated"));
        var checkCustomerBaseLocation = process.vertex(new AutomaticPolicy("checkCustomerBaseLocation"));
        var setBaseLocation = process.vertex(new Command("setBaseLocation"));
        process.edges(admin, createCustomer, customers, customerCreated, checkBaseLocation, createLocation, locations,
                locationCreated, checkCustomerBaseLocation, setBaseLocation, customers);

        var domains = new ProcessToDomains().apply(process);

        assertThat(domains.vertices().count(), is(1L));
    }

    @Test
    void shouldDiscoverGdprMiddlewareDomains() {
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

        var domains = new ProcessToDomains().apply(process);
        log(domains);

        var formDomain = new Domain("DataDeletionRequestForm", Set.of(dataDeletionRequestForm, deleteMyData,
                dataDeletionRequested));
        var servicesDomain = new Domain("Services", Set.of(services, checkUnresponsiveService,
                deletionsInProgress, checkRequestComplete, deleteData, remindService, timePassed, dataDeletionStarted,
                dataDeletionRequestedInService, dataDeletedInService));
        var notificationsDomain = new Domain("Notifications", Set.of(notifications, dataDeletionCompletion,
                informUser, dataDeleted));
        assertThat(domains.vertices().toList(), containsInAnyOrder(servicesDomain, notificationsDomain, formDomain));
        assertThat(domains.edges().toList(), contains(
                new Edge(servicesDomain, notificationsDomain)));
    }

    private void log(Domains domains) {
        System.out.println(representation.apply(domains));
    }

    @Test
    void shouldDiscoverOnlineClassifiedsDomains() {
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

        var domains = new ProcessToDomains().apply(process);
        log(domains);

        assertThat(domains.vertices().count(), is(6L));
        assertThat(domains.edges().count(), is(6L));
    }

}
