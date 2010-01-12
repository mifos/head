/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.accounts.savings.util.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsHelperIntegrationTest extends MifosIntegrationTestCase {
    public SavingsHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final short EVERY_FOUR_MONTHS = 4;
    SavingsHelper helper = new SavingsHelper();

    public void testCalculateDays() throws Exception {
        Date fromDate = getDate("01/01/2006");
        Date toDate = getDate("08/01/2006");
        int days = helper.calculateDays(fromDate, toDate);
       Assert.assertEquals("7", String.valueOf(days));

        // check for month feb
        fromDate = getDate("25/02/2006");
        toDate = getDate("05/03/2006");
        days = helper.calculateDays(fromDate, toDate);
       Assert.assertEquals("8", String.valueOf(days));

        // check for month feb in leap year
        fromDate = getDate("25/02/2004");
        toDate = getDate("05/03/2004");
        days = helper.calculateDays(fromDate, toDate);
       Assert.assertEquals("9", String.valueOf(days));

        fromDate = getDate("25/01/2006");
        toDate = getDate("05/02/2006");
        days = helper.calculateDays(fromDate, toDate);
       Assert.assertEquals("11", String.valueOf(days));

        // long differnce in days
        fromDate = getDate("05/01/2006");
        toDate = getDate("06/07/2006");
        days = helper.calculateDays(fromDate, toDate);
       Assert.assertEquals("182", String.valueOf(days));
    }

    public void testGetNextInterestCalculationDateEveryMonth() throws Exception {
        Date date;
        Date resultDate;
        Date accountActivationDate = getDate("05/04/2006");
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_MONTH,
                SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

        resultDate = helper.getNextScheduleDate(accountActivationDate, null, meeting);
        date = getDate("30/04/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/05/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/06/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/07/2006");
       Assert.assertEquals(date, resultDate);

        date = getDate("31/08/2006");
        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);

        date = getDate("30/09/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/10/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/11/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/12/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/01/2007");
       Assert.assertEquals(date, resultDate);
    }

    public void testGetNextInterestCalculationDateThreeMonths() throws Exception {
        Date date;
        Date resultDate;
        Date accountActivationDate = getDate("25/01/2006");
        final short EVERY_THREE_MONTHS = 3;
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_THREE_MONTHS,
                SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

        resultDate = helper.getNextScheduleDate(accountActivationDate, null, meeting);
        date = getDate("31/03/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/06/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/09/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/12/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/03/2007");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/06/2007");
       Assert.assertEquals(date, resultDate);
    }

    public void testGetNextInterestPostingDateWithRecurOnEveryMonth() throws Exception {
        Date date;
        Date resultDate;
        Date accountActivationDate = getDate("05/07/2006");
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_MONTH, SAVINGS_INTEREST_POSTING);

        resultDate = helper.getNextScheduleDate(accountActivationDate, null, meeting);
        date = getDate("31/07/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/08/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/09/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/10/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/11/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/12/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/01/2007");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("28/02/2007");
       Assert.assertEquals(date, resultDate);
    }

    public void testGetNextInterestPostingDateWithRecurOnEveryFourMonths() throws Exception {
        Date date;
        Date resultDate;
        Date accountActivationDate = getDate("25/12/2007");
        final short EVERY_FOUR_MONTHS = 4;
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_FOUR_MONTHS,
                SAVINGS_INTEREST_POSTING);

        resultDate = helper.getNextScheduleDate(accountActivationDate, null, meeting);
        date = getDate("31/12/2007");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("30/04/2008");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/08/2008");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getNextScheduleDate(accountActivationDate, date, meeting);
        date = getDate("31/12/2008");
       Assert.assertEquals(date, resultDate);
    }

    public void testPrevIntCalcDateOnEveryMonth() throws Exception {
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_MONTH,
                SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

        /*
         * Date resultDate = helper.getPrevScheduleDate(getDate("01/04/2006"),
         * getDate("30/06/2006"), meeting); Date date = getDate("31/05/2006");
         *Assert.assertEquals(date, resultDate);
         * 
         * resultDate = helper.getPrevScheduleDate(getDate("01/04/2006"),
         * getDate("01/01/2007"), meeting); date = getDate("01/12/2006");
         *Assert.assertEquals(date, resultDate);
         */

        Date resultDate = helper.getPrevScheduleDate(getDate("01/04/2006"), getDate("31/07/2006"), meeting);
        Date date = getDate("30/06/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getPrevScheduleDate(getDate("01/04/2006"), getDate("01/01/2007"), meeting);
        date = getDate("31/12/2006");
       Assert.assertEquals(date, resultDate);

    }

    public void testPrevIntCalcDateOnEveryThreeMonths() throws Exception {
        Date date;
        Date resultDate;
        // Date accountActivationDate = getDate("25/01/2006");
        final short EVERY_THREE_MONTHS = 3;
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_THREE_MONTHS,
                SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

        resultDate = helper.getPrevScheduleDate(getDate("01/03/2006"), getDate("30/06/2006"), meeting);
        date = getDate("31/03/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getPrevScheduleDate(getDate("01/03/2006"), getDate("01/03/2006"), meeting);
        Assert.assertNull(resultDate);
    }

    public void testPrevInterestPostingDateWithRecurOnEveryMonth() throws Exception {
        Date date;
        Date resultDate;
        // Date accountActivationDate = getDate("05/07/2006");
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_MONTH, SAVINGS_INTEREST_POSTING);

        resultDate = helper.getPrevScheduleDate(getDate("01/07/2006"), getDate("31/01/2007"), meeting);
        date = getDate("31/12/2006");
       Assert.assertEquals(date, resultDate);

        resultDate = helper.getPrevScheduleDate(getDate("01/07/2006"), getDate("28/02/2007"), meeting);
        date = getDate("31/01/2007");
       Assert.assertEquals(date, resultDate);
    }

    public void testPrevInterestPostingDateWithRecurOnEveryFourMonths() throws Exception {
        // Date accountActivationDate = getDate("25/12/2007");
        MeetingBO meeting = TestObjectFactory.getNewMeetingForToday(MONTHLY, EVERY_FOUR_MONTHS,
                SAVINGS_INTEREST_POSTING);
        Map<Date, Date> currentAndPrevDates = new HashMap<Date, Date>();
        currentAndPrevDates.put(getDate("15/01/2009"), getDate("31/12/2008"));
        currentAndPrevDates.put(getDate("31/12/2008"), getDate("31/08/2008"));
        currentAndPrevDates.put(getDate("31/08/2008"), getDate("30/04/2008"));
        currentAndPrevDates.put(getDate("30/04/2008"), getDate("31/12/2007"));

        Date activationDate = getDate("01/07/2006");
        Set<Date> keys = currentAndPrevDates.keySet();
        for (Date currentDate : keys) {
           Assert.assertEquals(currentAndPrevDates.get(currentDate), helper.getPrevScheduleDate(activationDate, currentDate,
                    meeting));
        }
    }

    public void testInterestPostingDateEveryYear() throws Exception {
        Date interestPostingDate = helper.getNextScheduleDate(getDate("02/01/2008"), null, new MeetingBO(Short
                .valueOf("31"), Short.valueOf("6"), getDate("01/01/2006"), MeetingType.SAVINGS_INTEREST_POSTING,
                "somePlace"));
       Assert.assertEquals(getDate("30/06/2008"), interestPostingDate);
    }
}
