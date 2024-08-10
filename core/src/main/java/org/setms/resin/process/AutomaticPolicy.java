package org.setms.resin.process;

/**
 * A policy that executes automatically, without human input.
 * @param name the name of the policy
 */
public record AutomaticPolicy(String name) implements Policy {

}
