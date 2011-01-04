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

package org.mifos.application.admin.servicefacade;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.dto.domain.OfficeHoliday;
import org.springframework.security.access.prepost.PreAuthorize;

public interface HolidayServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    void createHoliday(HolidayDetails holidayDetails, List<Short> branchIds);

    @PreAuthorize("isFullyAuthenticated()")
    Map<String, List<OfficeHoliday>> holidaysByYear();

    OfficeHoliday retrieveHolidayDetailsForPreview(HolidayDetails holidayDetail, List<Short> officeIds);

    List<String> retrieveOtherHolidayNamesWithTheSameDate(HolidayDetails holidayDetail, List<Short> branchIds);

    boolean isWorkingDay(Calendar day, Short officeId);

    Calendar getNextWorkingDay(Calendar day, Short officeId);

    Date getNextWorkingDay(Date day, Short officeId);

    void validateDisbursementDateForNewLoan(Short officeId, DateTime disbursementDate);

    boolean isFutureRepaymentHoliday(Short officeId, Calendar dueDate);
}
