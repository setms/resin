package org.setms.resin.process;


/**
 * Something interesting that happened in the system.
 * @param name the name of the event
 */
public record Event(String name) implements DomainEvent {

}
