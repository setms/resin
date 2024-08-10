package org.setms.resin.process;

import java.util.Collection;


/**
 * A container for one or more types of data items.
 */
public interface DataContainer {

    Collection<String> dataItems();

    default boolean sharesDataItemsWith(DataContainer other) {
        return dataItems().stream().anyMatch(other.dataItems()::contains);
    }

}
