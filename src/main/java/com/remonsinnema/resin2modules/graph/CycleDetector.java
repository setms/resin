package com.remonsinnema.resin2modules.graph;

import java.util.Collection;


public interface CycleDetector {

    Collection<Cycle> findAllCyclesIn(Graph graph);

}
