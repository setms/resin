package org.setms.resin.graph;


class WhenDetectingCyclesViaExhaustiveSearch extends CycleDetectorContractTest {

    @Override
    protected CycleDetector newCycleDetector() {
        return new DetectCyclesViaExhaustiveSearch();
    }

}
