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

package org.mifos.reports.util.helpers;

public class ReportValidationConstants {

    public static final String LOAN_OFFICER_ID_INVALID_MSG = "loanOfficerId.invalid";
    public static final String CENTER_ID_INVALID_MSG = "centerId.invalid";
    public static final String MEETING_DATE_INVALID_MSG = "meetingDate.invalid";
    public static final String BRANCH_ID_INVALID_MSG = "branchId.invalid";
    public static final String LOAN_PRODUCT_ID_INVALID_MSG = "loanProductId.invalid";
    public static final String RUN_DATE_INVALID_MSG = "runDate.invalid";
    public static final String BRANCH_REPORT_NO_DATA_FOUND_MSG = "runDate.notFound";

    public static final String BRANCH_ID_PARAM = "branchId";
    public static final String LOAN_OFFICER_ID_PARAM = "loanOfficerId";
    public static final String CENTER_ID_PARAM = "centerId";
    public static final String MEETING_DATE_PARAM = "meetingDate";
    public static final String LOAN_PRODUCT_ID_PARAM = "loanProductId";
    public static final String RUN_DATE_PARAM = "branchReportDate";
    public static final String RUN_DATE_PARAM_FOR_CASH_CONF_REPORT = "runDate";

    public static final String[] COLLECTION_SHEET_REPORT_PARAMS_ARRAY = { BRANCH_ID_PARAM, LOAN_OFFICER_ID_PARAM,
            CENTER_ID_PARAM, MEETING_DATE_PARAM };

    public static final String[] DETAILED_AGING_REPORT_PARAMS_ARRAY = { BRANCH_ID_PARAM, LOAN_OFFICER_ID_PARAM,
            LOAN_PRODUCT_ID_PARAM };

    public static final String[] BRANCH_REPORT_PARAMS_ARRAY = { BRANCH_ID_PARAM, RUN_DATE_PARAM };

    public static final String[] JDBC_CASH_CONFIRMATION_REPORT_PARAMS_ARRAY = { BRANCH_ID_PARAM,
            RUN_DATE_PARAM_FOR_CASH_CONF_REPORT };

}
