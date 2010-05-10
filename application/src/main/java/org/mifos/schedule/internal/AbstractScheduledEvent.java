/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.schedule.internal;

import org.mifos.schedule.ScheduledEvent;

public abstract class AbstractScheduledEvent implements ScheduledEvent {

    /**
     * Count the number of occurrences of the dependent event that would roll up to the given occurrence of this event.
     * It is assumed that the two events have like recurrences (e.g both meet on Tuesdays) but may differ in
     * the number of periods skipped. It is also assumed that the dependent schedule starts on startingOccurrence
     * of this event.
     *
     * <p> See {@liink FeeInstallment#createMergedFeeInstallments()} in which this method is used to determine how
     * many fee installments roll up to meeting or loan installments.</p>
     *
     * <p>For example, if this event's schedule is every Tuesday but the dependent schedule is every other Tuesday,
     * and the dependent schedule starts on this event's third occurrence:
     *
     *<pre>
     * ---------------------------------------------
     *|installment | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
     * --------------------------------------------
     *|returns     | 1 | 0 | 1 | 0 | 1 | 0 | 1 |  0 |
     * ---------------------------------------------
     *</pre>
     *
     *<p>A more complex example: This event is scheduled for every third week and the dependent event is scheduled
     *for every other week. Assume the dependent schedule starts with this event's 5th occurrence (table has
     *one column for each week)
     *
     *<pre>
     * -------------------------------------------------------------------------------------------
     * | this occurrences        |  5 |    |    |  6 |    |    |  7 |    |    |  8 |    |    |  9 |
     * | dependent occurrences.  |  X |    |  X |    |  X |    |  X |    |  X |    |    |  X |    |
     * | returns                 |  1 |    |    |  1 |    |    |  2 |    |    |  1 |    |    |  2 |
     * -------------------------------------------------------------------------------------------
     *</pre>
     *
     *<p>Switching around: This event is scheduled for every third week and the dependent event is scheduled
     *for every second week:
     *
     *
     * @param dependentEvent the ScheduledEvent whose dates roll up to dates of this event
     * @param occurrence the index of the date of this event that dependent events roll up to
     * @param startingOccurrence the dependent event's occurrences start with this occurrence of this event
     * @return the number of installments of the dependent event falling between the given installment of this event
     *  and the previous installment, not including the previous but including the given installment.
     * @throws IllegalArgumentException if occurrence or startingOccurrence is not positive
     * @throws IllegalArgumentException if the two events' recurrences do not match
     */
    public int numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith
                    (ScheduledEvent dependentEvent, int occurrence, int startingOccurrence) {

        if ((occurrence <=0) || (startingOccurrence <= 0)) {
            throw new IllegalArgumentException("Occurrences must not be negative.");
        }

        if (occurrence < startingOccurrence) {
            throw new IllegalArgumentException("Occurrence must be on or after starting occurrence.");
        }
       //TODO KRP: add check that events have similar recurrences (e.g. both weekly)

        if (occurrence == startingOccurrence) {
            return 1;
        }
        return numberOfEventsOnOrBefore (dependentEvent, occurrence, startingOccurrence)
        - numberOfEventsOnOrBefore (dependentEvent, occurrence - 1, startingOccurrence);
    }

    /**
     * Assume that this event's occurrences are numbered consecutively.
     * Assume that this event and the dependent event have the same recurrence period (e.g., both are weekly events).
     * Also assume this and the dependent event's first occurrence coincide.
     * Then return the number of dependent event's occurrence that occur before or on
     * this event's occurrence but after this event's previous occurrence.
     */

    public int numberOfEventsRollingUpToThis(ScheduledEvent dependentEvent, int occurrence) {
        return numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(dependentEvent, occurrence, 1);
    }

    /**
     * Same as above, but assume that an occurrence of the dependent event coincides with this event's startingEventNumber.
     */
//    private int numberOfDependentOccurrencesRollingUpToThisOccurrenceWithStartingOccurrence
//                    (ScheduledEvent dependentEvent, int occurrence, int startingOccurrence) {
//
//        if ((occurrence <=0) || (startingOccurrence <= 0)) {
//            throw new IllegalArgumentException("Installment number must be positive.");
//        }
//        //TODO KRP: add check that events have similar recurrences (e.g. both weekly)
//
//        if (occurrence == startingOccurrence) {
//            return 1;
//        }
//        return numberOfEventsOnOrBefore (dependentEvent, occurrence, startingOccurrence)
//            - numberOfEventsOnOrBefore (dependentEvent, occurrence - 1, startingOccurrence);
//    }

    /**
     * Assume that this event's occurrences are numbered consecutively.
     * Assume that this event and the dependent event have the same recurrence period (e.g., both are weekly events).
     * Also assume that an occurrence of the dependent event coincides with this event's startingOccurrence.
     * Then return the number of dependent event's occurrence that occur before or on
     * this event's occurrence, starting with startingOdduccence.
     */
    private int numberOfEventsOnOrBefore (ScheduledEvent dependentEvent, int occurrence, int startingOccurrence) {

        return ((occurrence - startingOccurrence) * this.getEvery()) / dependentEvent.getEvery() + 1;
    }

}
