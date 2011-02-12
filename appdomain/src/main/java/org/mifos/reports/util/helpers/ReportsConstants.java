/*
 * Copyright Grameen Foundation USA
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public interface ReportsConstants {
    String GETPATH_REPORTS = "reportsAction";
    String GETREPORTPAGE = "getReportPage";
    String GETALLREPORTS = "getAllReports";
    String LISTOFREPORTS = "listOfReports";
    String LISTOFREPORTCATEGORIES = "listOfReportCategories";
    // /ADDED BY MFTECH
    String GETALLREPORTSPARAMS = "getAllReportParams";
    String GETALLREPORTSDATASOURCE = "getAllReportDataSource";
    String CREATE_FAILED_EXCEPTION = "";
    String ADDREPORTSPARAMS = "reportparamsadd_path";
    String LISTREPORTSPARAMS = "reportparamslist_path";
    String VIEWREPORTSPARAMS = "reportparamsview_path";
    String ADDREPORTSDATASOURCE = "reportdatasourceadd_path";
    String LISTREPORTSDATASOURCE = "reportdatasourcelist_path";
    String VIEWREPORTSDATASOURCE = "reportdatasourceview_path";
    String GETALLREPORTSPARAMSMAP = "getAllReportParamsMap";
    String FIND_PARAMS_OF_REPORTID = "findParamsOfReportId";
    String ADDLISTREPORTSPARAMSMAP = "reportparamsmapaddlist_path";
    String ADDLISTREPORTSUSERPARAMS = "reportuserparamslist_path";
    String PROCESSREPORTSUSERPARAMS = "reportsuserprocess_path";
    String FIND_JASPER_OF_REPORTID = "findJasperOfReportId";
    String FIND_IN_USE_PARAMETER = "findsInUseParam";
    String FIND_IN_USE_DATASOURCE = "findInUseDataSource";
    String VIEW_DATASOURCE = "viewDataSource";
    String VIEW_PARAMETER = "viewParameter";
    // /ENDED BY MFTECH
    String BIRTREPORTPATH = "birtreport_path";
    String ADMINDOCBIRTREPORTPATH = "admindocumentreport_path";
    SimpleDateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy",new Locale("en","GB"));

    String ERROR_TITLE = "error.titleName";
    String ERROR_CATEGORYID = "error.categoryId";
    String ERROR_FILE = "error.file";
    String ERROR_ACCOUNTTYPE = "error.accountType";
    String ERROR_STATUS = "error.status";
    String ERROR_STATUSLIST = "error.statusList";
    String ERROR_TITLEALREADYEXIST = "error.titleAlreadyExist";
    String HIDDEN_PASSWORD = "********";
    String ERROR_CATEGORYNAME = "error.categoryName";
    String ERROR_CATEGORYNAMEALREADYEXIST = "error.categoryNameAlreadyExist";
    String ERROR_CATEGORYNAMENOTEDIT = "error.categoryNameNotEdit";
    String ERROR_CATEGORYHASREPORTS = "error.categoryHasReports";
    String ERROR_REPORTINFONOTEDIT = "error.reportInfoNotEdit";
    String ERROR_REPORTALREADYEXIST = "error.reportAlreadyExist";
    String ERROR_REPORTACTIVITYIDISNULL = "error.reportActivityIdIsNull";
    String ERROR_NOMOREDYNAMICACTIVITYID = "error.noMoreDynamicActivityId";
    // added by collection sheet report module
    String SELECT_DISPLAY_NAME = "---Select---";
    String NOT_APPLICABLE_DISPLAY_NAME = "-Not Applicable-";
    String ALL_DISPLAY_NAME = "ALL";
    Integer NOT_APPLICABLE_ID = new Integer("-3");
    Integer SELECT_ID = new Integer("-2");
    Integer ALL_ID = new Integer("-1");
    Date NA_DATE = new Date(0l);
    // ended by collection sheet report module
}
