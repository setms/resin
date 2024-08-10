package org.setms.resin.graph;

import java.util.Collection;


interface CycleDetector {

    Collection<Cycle> findAllCyclesIn(Graph graph);

}
