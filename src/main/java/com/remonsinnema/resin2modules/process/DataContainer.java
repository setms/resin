package com.remonsinnema.resin2modules.process;

import java.util.Collection;


public interface DataContainer {

    Collection<String> dataItems();

    default boolean sharesDataItemsWith(DataContainer other) {
        return dataItems().stream().anyMatch(other.dataItems()::contains);
    }

}
