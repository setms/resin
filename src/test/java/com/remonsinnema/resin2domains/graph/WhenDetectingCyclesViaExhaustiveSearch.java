package com.remonsinnema.resin2domains.graph;


class WhenDetectingCyclesViaExhaustiveSearch extends CycleDetectorContractTest {

    @Override
    protected CycleDetector newCycleDetector() {
        return new DetectCyclesViaExhaustiveSearch();
    }

}
