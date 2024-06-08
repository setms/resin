package com.remonsinnema.resin2modules.graph;


class WhenDetectingCyclesViaExhaustiveSearch extends CycleDetectorContractTest {

    @Override
    protected CycleDetector newCycleDetector() {
        return new DetectCyclesViaExhaustiveSearch();
    }

}
