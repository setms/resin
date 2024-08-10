package org.setms.resin.process;

/**
 * A policy executed by a human.
 * @param name the name of the policy
 */
public record ManualPolicy(String name) implements Policy {
}
