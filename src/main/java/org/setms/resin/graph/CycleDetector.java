package org.setms.resin.graph;

import java.util.Collection;


public interface CycleDetector {

    Collection<Cycle> findAllCyclesIn(Graph graph);

}
