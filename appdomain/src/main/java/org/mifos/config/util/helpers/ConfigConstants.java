/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.config.util.helpers;

/**
 * This public interface will hold the constants related to configurable items.
 */
public interface ConfigConstants {
    String LATENESS_DAYS = "latenessDays";
    String DORMANCY_DAYS = "dormancyDays";

    // constants for Meeting configuration
    String FISCAL_START_OF_WEEK = "fiscalStartOfWeek";
    String WEEK_OFF_LIST = "weekOffList";

    // other constants
    Short OPTIONAL_FLAG = 0;
    Short DORMANCY_DAYS_DEFAULT = 30;
    Short LATENESS_DAYS_DEFAULT = 30;
    Short INTEREST_DAYS_DEFAULT = 365;
    Short FISCAL_START_OF_WEEK_DEFAULT = 2;
    String MEETING_SCHEDULE_TYPE_DEFAULT = "same_day";

    String OFFICE_CONFIG_CONSTANTS = "org.mifos.config.util.helpers.OfficeConfigConstants";

    String FIRST_NAME = "first_name";
    String MIDDLE_NAME = "middle_name";
    String SECOND_LAST_NAME = "second_last_name";
    String LAST_NAME = "last_name";
    String JASPER_REPORT_IS_HIDDEN = "jasperReportIsHidden";
    String COLLECTION_SHEET_DAYS_IN_ADVANCE = "CollectionSheet.DaysInAdvance";
}
