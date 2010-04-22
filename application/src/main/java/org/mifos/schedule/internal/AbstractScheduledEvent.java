package org.mifos.schedule.internal;

import org.mifos.schedule.ScheduledEvent;

public abstract class AbstractScheduledEvent implements ScheduledEvent {

    /**
     * Count the number of installments of the dependent event that would roll up to the given installment of this event.
     * It is assumed that the two events have like recurrences (e.g both meet on Tuesdays) but may differ in
     * the number of periods skipped. It is also assumed that both schedules have the same starting date.
     *
     * <p> See {@liink FeeInstallment#createFeeInstallments()} in which this method is used to determine how
     * many fee installments roll up to meeting or loan installments.</p>
     *
     * <p>For example, if this event's schedule is every Tuesday but the dependent schedule is every other Tuesday,
     * then two dependent events roll up to every even-numbered installment, and none roll up to an odd-numbered
     * schedule:
     *
     *<pre>
     * -----------------------------------------------------
     *|installment | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
     * -----------------------------------------------------
     *|returns     | 0 | 2 | 0 | 2 | 0 | 2 | 0 | 2 | 0 | 2  |
     * -----------------------------------------------------
     *</pre>
     *
     *<p>A more complex example: This event is scheduled for every other week and the dependent event is scheduled
     *for every third week:
     *
     *<pre>
     * ----------------------------------------------------------------------------------
     * | week                |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 |
     * | this installments   |    |  1 |    |  2 |    |  3 |    |  4 |    |  5 |    |  6 |
     * | dependent install.  |    |    |  1 |    |    |  2 |    |    |  3 |    |    |  4 |
     * | returns             |    |  0 |    |  1 |    |  1 |    |  0 |    |  1 |    |  1 |
     * ----------------------------------------------------------------------------------
     *</pre>
     *
     *<p>Switching around: This event is scheduled for every third week and the dependent event is scheduled
     *for every second week:
     *
     *<pre>
     * ----------------------------------------------------------------------------------
     * | week                |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 |
     * | this installments   |    |    |  1 |    |    |  2 |    |    |  3 |    |    |  4 |
     * | dependent install   |    |  1 |    |  2 |    |  3 |    |  4 |    |  5 |    |  6 |
     * | returns             |    |    |  1 |    |    |  2 |    |    |  1 |    |    |  2 |
     * ----------------------------------------------------------------------------------
     *</pre>
     *
     * @param dependentEvent the ScheduledEvent whose dates roll up to dates of this event
     * @param installment the index of the date of this event that dependent events roll up to
     * @return the number of installments of the dependent event falling between the given installment of this event
     *  and the previous installment, not including the previous but including the given installment.
     * @throws IllegalArgumentException if installment is not positive
     * @throws IllegalArgumentException if the two events' recurrences do not match
     */
    public int numberOfEventsRollingUpToThis(ScheduledEvent dependentEvent, int installment) {
        if (installment <=0) {
            throw new IllegalArgumentException("Installment number must be positive.");
        }
        //TODO KRP: add check that events have similar recurrences (e.g. both weekly)

        int numberOfPeriodsForThisInstallment = installment * this.getEvery();
        int numberOfPeriodsForPreviousInstallment = numberOfPeriodsForThisInstallment - this.getEvery();
        int closestDependentInstallmentUpToThisInstallment = numberOfPeriodsForThisInstallment / dependentEvent.getEvery();
        int numberOfPeriodsForDependentEvent = closestDependentInstallmentUpToThisInstallment * dependentEvent.getEvery();
        int countRollups = 0;
        while ((numberOfPeriodsForPreviousInstallment < numberOfPeriodsForDependentEvent)
                && (numberOfPeriodsForDependentEvent <= numberOfPeriodsForThisInstallment)) {
            countRollups++;
            numberOfPeriodsForDependentEvent = numberOfPeriodsForDependentEvent - dependentEvent.getEvery();
        }
        return countRollups;
    }

}
