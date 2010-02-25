package org.mifos.calendar;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.schedule.ScheduledEvent;

public class MoratoriumStrategy implements ListOfDatesAdjustmentStrategy {

    /**
     * should be ordered by date ascending to avoid problems with overlapping holidays
     */
    private final List<Holiday> upcomingHolidays;
    private final List<Days> workingDays;
    private final ScheduledEvent scheduledEvent;
    private DateAdjustmentStrategy nonMoratoriumAdjustmentStrategy;

    public MoratoriumStrategy(final List<Holiday> upcomingHolidays, final List<Days> workingDays,
            final ScheduledEvent scheduledEvent) {
        
        this.upcomingHolidays = upcomingHolidays;
        this.workingDays = workingDays;
        this.scheduledEvent = scheduledEvent;
    }
    
    @Override
    public List<DateTime> adjust (List<DateTime> dates) {

        assert dates != null;
        
        List<DateTime> adjustedDates = null;
        
        nonMoratoriumAdjustmentStrategy 
            = new BasicHolidayStrategy(getNonMoratoriumHolidays(), workingDays, scheduledEvent);
        
        if (dates.isEmpty()) {
            adjustedDates = dates;
        } else {
           DateTime firstDate = dates.get(0);
           if ( isEnclosedByAHolidayWithRepaymentRule(firstDate, RepaymentRuleTypes.REPAYMENT_MORATORIUM) ) {
               adjustedDates = adjust (shiftSchedulePastMoratorium(dates));
          /*
           } else if ( isEnclosedByAHolidayWithRepaymentRule(firstDate, RepaymentRuleTypes.SAME_DAY)) {
               int countDatesEnclosedBySameDayHoliday = countDatesEnclosedByHoliday(dates, getHolidayEnclosing(firstDate));
               adjustedDates = joinLists (first(countDatesEnclosedBySameDayHoliday, dates),
                                          adjust(allButFirst(countDatesEnclosedBySameDayHoliday, dates)));
           } else if (isEnclosedByAHoliday(firstDate)){ //a rule that shifts the date forward
               List<DateTime> datesShiftedPastHoliday = shiftDatesInNonMoratoriumHoliday(dates);
               adjustedDates = adjust (joinLists (datesShiftedPastHoliday,
                                                  allButFirst (datesShiftedPastHoliday.size(), dates)));
           */
           } else if (isEnclosedByAHoliday(firstDate)) { //enclosed by a non-moratorium holiday
               adjustedDates = makeList(shiftDatePastNonMoratoriumHoliday(firstDate), adjust (rest(dates)));
           } else {
               adjustedDates = makeList (firstDate, adjust (rest (dates)));
           }
        }
        return adjustedDates;
    }
     
    private List<DateTime> shiftSchedulePastMoratorium (List<DateTime> dates) {
   
        assert (dates != null) && isEnclosedByAHolidayWithRepaymentRule(dates.get(0), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        
        List<DateTime> shiftedDates = dates;
        do {
            shiftedDates = shiftByOneScheduledEventRecurrence(shiftedDates);
        } while (isEnclosedByAHolidayWithRepaymentRule(shiftedDates.get(0), RepaymentRuleTypes.REPAYMENT_MORATORIUM));
        return shiftedDates;
            
    }
    
    private List<DateTime> shiftByOneWeek(List<DateTime>dates) {
        
        List<DateTime> pushedOutSchedule = new ArrayList<DateTime>();
        for (DateTime date: dates) {
            pushedOutSchedule.add(date.plusWeeks(1));
        }
        return pushedOutSchedule;
        
    }
    
    private List<DateTime> shiftByOneScheduledEventRecurrence (List<DateTime> dates) {
        
        assert dates != null;
        
        List<DateTime> pushedOutSchedule = new ArrayList<DateTime>();
        for (DateTime date: dates) {
            pushedOutSchedule.add(scheduledEvent.nextEventDateAfter(date));
        }
        return pushedOutSchedule;
    }
    
    /**
     * Given that the first date in the list falls in a non-Moratorium holiday,
     * return the list of dates that fall in the holiday, but shifted out of the holiday
     * using the holiday's repayment rule.
     * TODO keithp: once dates are shifted past all non-moratorium holidays, then check whether
     * any fall in a moratorium period. If they do, push them past the moratorium, but do not
     * push out future dates.
     * TODO keithp: if shifting a date does not change the date (e.g. same day holiday), then
     * we're done with it, so move to the next date.
     */
    private List<DateTime> shiftDatesInNonMoratoriumHoliday (List<DateTime> dates) {
        
        assert dates != null;
        assert ! dates.isEmpty();
        assert isEnclosedByAHoliday(dates.get(0));
        assert ! isEnclosedByAHolidayWithRepaymentRule(dates.get(0), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        
        Holiday enclosingHoliday = getHolidayEnclosing(dates.get(0));
        List<DateTime> shiftedDatesInHoliday = new ArrayList<DateTime>();
        for (int i = 0; i < dates.size(); i++) {
            if (enclosingHoliday.encloses(dates.get(i).toDate())) {
                shiftedDatesInHoliday.add(nonMoratoriumAdjustmentStrategy.adjust(dates.get(i)));          
            }
        }
        return shiftedDatesInHoliday;
    }
    
    /**
     * Given that the date is in a non-moratorium holiday, shift it past the holiday until either it is no longer
     * in a holiday or moratorium, or until it no longer moves (e.g., lands in a same-day holiday).
     * 
     * <p> If the date shifts into a moratorium period, then shift it out using the RepaymentRuleType of
     * the most recent non-moratorium holiday that the date was shifted out of. For example, if shifting
     * the date out of a next-working-day holiday lands it in a moratorium period, then use the
     * next-working-day repayment rule to shift it past the moratorium period.</p>
     * 
     * @param date the DateTime to be shifted
     * @return the shifted date
     */
    private DateTime shiftDatePastNonMoratoriumHoliday (DateTime date) {

        assert date != null;
        assert isEnclosedByAHoliday(date);
        assert ! isEnclosedByAHolidayWithRepaymentRule(date, RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        
        Holiday currentlyEnclosingHoliday = getHolidayEnclosing(date);
        RepaymentRuleTypes mostRecentNonMoratoriumRepaymentRule 
                                = currentlyEnclosingHoliday.getRepaymentRuleType(); //never REPAYMENT_MORATORIUM
        DateTime previousDate = null;
        DateTime adjustedDate = date;
        
        do {
            previousDate = adjustedDate;
            if (currentlyEnclosingHoliday.getRepaymentRuleType() == RepaymentRuleTypes.REPAYMENT_MORATORIUM) {
                adjustedDate = buildHolidayFromCurrentHolidayWithRepaymentRule (currentlyEnclosingHoliday, 
                                                              mostRecentNonMoratoriumRepaymentRule)
                                    .adjust(previousDate, workingDays, scheduledEvent);
            } else {
                adjustedDate = currentlyEnclosingHoliday.adjust(previousDate, workingDays, scheduledEvent);
                mostRecentNonMoratoriumRepaymentRule = currentlyEnclosingHoliday.getRepaymentRuleType();
            }
            if (isEnclosedByAHoliday(adjustedDate)) {
                currentlyEnclosingHoliday = getHolidayEnclosing(adjustedDate);
            }
        } while (isEnclosedByAHoliday(adjustedDate) && (! adjustedDate.equals(previousDate)));

        return adjustedDate;
    }
    
    private Holiday buildHolidayFromCurrentHolidayWithRepaymentRule (Holiday originalHoliday, RepaymentRuleTypes rule) {
        HolidayPK holidayPK = new HolidayPK((short)1, originalHoliday.getFromDate().toDate());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity(rule.getValue(), "lookup.value.key");
        try {
            return new HolidayBO(holidayPK, originalHoliday.getThruDate().toDate(), "temporaryHoliday", repaymentRuleEntity);
        } catch (ApplicationException e) {
            throw new IllegalStateException("Could not create temporary holiday", e);
        }
    }
    
    private List<Holiday> getNonMoratoriumHolidays () {
        List<Holiday> nonMoratoriumHolidays = new ArrayList<Holiday>();
        for (Holiday holiday : this.upcomingHolidays) {
            if ( ! (holiday.getRepaymentRuleType() == RepaymentRuleTypes.REPAYMENT_MORATORIUM) ) {
                nonMoratoriumHolidays.add(holiday);
            }
        }
        return nonMoratoriumHolidays;
    }
    
    private boolean isEnclosedByAHolidayWithRepaymentRule (DateTime date, RepaymentRuleTypes rule) {
        for (Holiday holiday : this.upcomingHolidays) {
            if (holiday.encloses(date.toDate()) && (holiday.getRepaymentRuleType() == rule)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isEnclosedByAHoliday (DateTime date) {
        for (Holiday holiday : this.upcomingHolidays) {
            if (holiday.encloses(date.toDate())) {
                return true;
            }
        }
        return false;
    }
    
    private int countDatesEnclosedByHoliday (List<DateTime> dates, Holiday holiday) {
        int countEnclosedDates = 0;    
        for (int i = 0; i < dates.size(); i++) {
            if (holiday.encloses(dates.get(i).toDate())) {
                countEnclosedDates++;
            }
        }
        return countEnclosedDates;
    }
    
    private Holiday getHolidayEnclosing (DateTime date) {
        
        assert isEnclosedByAHoliday(date);
        
        Holiday holidayEnclosingDate = null;
        for (Holiday holiday : upcomingHolidays) {
            if (holiday.encloses(date.toDate())) {
                holidayEnclosingDate = holiday;
            }
        }
        return holidayEnclosingDate;
    }
    
    /*
     * TODO KeithP: These methods should be generic and moved to a ListUtils class
     */
    private List<DateTime> rest (List<DateTime> dates) {
        
        assert dates != null;
        
        List<DateTime> rest = new ArrayList<DateTime>();
        for (int i = 1; i < dates.size(); i++) {
            rest.add(dates.get(i));
        }
        return rest;
    }
    
    /**
     * return list of first count items in dates
     * @param count the number of dates to select
     * @param dates the list to get the dates from
     * @return dates(get(0)), .. dates.get(count-1) as a list
     */
    private List<DateTime> first (int count, List<DateTime> dates) {
        
        assert dates != null;
        assert count >= 0;
        assert count < dates.size();
        
        List<DateTime> firstPartOfList = new ArrayList<DateTime>();
        for (int i = 0; i < count; i++) {
            firstPartOfList.add(dates.get(i));
        }
        return firstPartOfList;
    }
    
    private List<DateTime> allButFirst (int count, List<DateTime> dates) {
        
        assert dates != null;
        assert count >= 0;
        assert count <= dates.size();
        
        List<DateTime> lastPartOfList = new ArrayList<DateTime>();
        for (int i = count; i < dates.size(); i++) {
            lastPartOfList.add(dates.get(i));
        }
        return lastPartOfList;
        
    }
    
    private List<DateTime> makeList (DateTime first, List<DateTime> rest) {
        
        assert first != null;
        assert rest != null;
        
        List<DateTime> newList = rest;
        newList.add(0, first);
        return newList;
    }
    
    private List<DateTime> joinLists (List<DateTime> first, List<DateTime> rest) {
        
        assert first != null;
        assert rest != null;
        
        List<DateTime> newList = first;
        newList.addAll(rest);
        return newList;
    }
}
