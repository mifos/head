package org.mifos.calendar;

import java.util.List;

import org.joda.time.DateTime;

public interface ListOfDatesAdjustmentStrategy {

    /**
     * 
     * returns list of dates adjusted according to the implementor's adjustment rules.
     * Use this method when the entire schedule must be considered when adjusting,
     * for example, when adjusting for a moratorium period.
     * 
     * @return the list of adjusted dates
     */
    List<DateTime> adjust(List<DateTime> dates);

}
