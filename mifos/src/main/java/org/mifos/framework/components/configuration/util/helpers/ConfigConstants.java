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

package org.mifos.framework.components.configuration.util.helpers;

/**
 * This interface will hold the constants related to configurable items.
 */
public interface ConfigConstants {
    public static final String LATENESS_DAYS = "latenessDays";
    public static final String DORMANCY_DAYS = "dormancyDays";

    // constants for Meeting configuration
    public static final String FISCAL_START_OF_WEEK = "fiscalStartOfWeek";
    public static final String WEEK_OFF_LIST = "weekOffList";

    // other constants
    public static final Short OPTIONAL_FLAG = 0;
    public static final Short DORMANCY_DAYS_DEFAULT = 30;
    public static final Short LATENESS_DAYS_DEFAULT = 30;
    public static final Short INTEREST_DAYS_DEFAULT = 365;
    public static final Short FISCAL_START_OF_WEEK_DEFAULT = 2;
    public static final String MEETING_SCHEDULE_TYPE_DEFAULT = "same_day";

    public static final String OFFICE_CONFIG_CONSTANTS = "org.mifos.framework.components.configuration.util.helpers.OfficeConfigConstants";

    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String SECOND_LAST_NAME = "second_last_name";
    public static final String LAST_NAME = "last_name";
    public static final String JASPER_REPORT_IS_HIDDEN = "jasperReportIsHidden";
    public static final String COLLECTION_SHEET_DAYS_IN_ADVANCE = "CollectionSheet.DaysInAdvance";
    public static final String BACK_DATED_TRANSACTIONS_ALLOWED = "BackDatedTransactionsAllowed";
}
