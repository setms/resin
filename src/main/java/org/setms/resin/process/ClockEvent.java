package org.setms.resin.process;


/**
 * An event triggered by the passing of time.
 * @param name the name of the event
 */
public record ClockEvent(String name) implements DomainEvent {
}
