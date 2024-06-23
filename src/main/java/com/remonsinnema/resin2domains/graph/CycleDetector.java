package com.remonsinnema.resin2domains.graph;

import java.util.Collection;


public interface CycleDetector {

    Collection<Cycle> findAllCyclesIn(Graph graph);

}
