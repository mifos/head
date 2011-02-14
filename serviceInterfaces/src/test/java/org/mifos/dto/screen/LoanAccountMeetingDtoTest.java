/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.dto.screen;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoanAccountMeetingDtoTest {
    @Test
    public void testLoanAccountMeetingDTOCreation() {
        String recurrenceId = "1";
        String weekDay = "2";
        String recurWeek = "3";
        String monthType = "4";
        String monthDay = "5";
        String dayRecurMonth = "6";
        String monthWeek = "7";
        String recurMonth = "8";
        String monthRank = "9";
        LoanAccountMeetingDto loanAccountMeetingDto = new LoanAccountMeetingDto(recurrenceId, weekDay, recurWeek,
                monthType, monthDay, dayRecurMonth, monthWeek, recurMonth, monthRank);
        assertEquals(loanAccountMeetingDto.getRecurrenceId().toString(), recurrenceId);
        assertEquals(loanAccountMeetingDto.getWeekDay().toString(), weekDay);
        assertEquals(loanAccountMeetingDto.getEveryWeek().toString(), recurWeek);
        assertEquals(loanAccountMeetingDto.getMonthType().toString(), monthType);
        assertEquals(loanAccountMeetingDto.getDayOfMonth().toString(), monthDay);
        assertEquals(loanAccountMeetingDto.getDayRecurMonth().toString(), dayRecurMonth);
        assertEquals(loanAccountMeetingDto.getWeekOfMonth().toString(), monthWeek);
        assertEquals(loanAccountMeetingDto.getEveryMonth().toString(), recurMonth);
        assertEquals(loanAccountMeetingDto.getMonthRank().toString(), monthRank);
    }

}
