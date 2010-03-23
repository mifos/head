package org.mifos.calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Test;

public class WorkingDayTest {

    @Test
    public void shouldIndicateIsWorkingDay() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay());

        boolean result = WorkingDay.isWorkingDay(monday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsOneOfTheWorkingDays() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isWorkingDay(monday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsNotAWorkingDay() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isWorkingDay(sunday, workingDays);

        Assert.assertFalse("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsNotAWorkingDayUsingNegationMethod() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isNotWorkingDay(sunday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test(expected = IllegalStateException.class)
    public void throwExceptionIfWorkingDaysAreEmpty() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList();

        WorkingDay.nextWorkingDay(sunday, workingDays);
    }

    @Test
    public void shouldRollForwardDateToNextWorkingDay() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        DateTime nextWorkingDay = WorkingDay.nextWorkingDay(sunday, workingDays);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.monday()));
    }

    @Test
    public void shouldRollForwardDateWhenAlreadyOnWorkingDay() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay());

        DateTime nextWorkingDay = WorkingDay.nextWorkingDay(monday, workingDays);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.tuesday()));
    }
}
