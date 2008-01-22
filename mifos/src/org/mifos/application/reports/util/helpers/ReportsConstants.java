/**

 * ReportsContants.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.reports.util.helpers;

import java.util.Date;

public interface ReportsConstants {
	public String GETPATH_REPORTS="reportsAction";
	public String GETREPORTPAGE="getReportPage";
	public String GETALLREPORTS="getAllReports";
	public String LISTOFREPORTS="listOfReports";
	public String LISTOFREPORTCATEGORIES="listOfReportCategories";
///ADDED BY MFTECH
	public String GETALLREPORTSPARAMS = "getAllReportParams";
	public String GETALLREPORTSDATASOURCE = "getAllReportDataSource";
	public String CREATE_FAILED_EXCEPTION="";
	public String ADDREPORTSPARAMS="reportparamsadd_path";
	public String LISTREPORTSPARAMS="reportparamslist_path";
	public String VIEWREPORTSPARAMS="reportparamsview_path";
	public String ADDREPORTSDATASOURCE="reportdatasourceadd_path";
	public String LISTREPORTSDATASOURCE="reportdatasourcelist_path";
	public String VIEWREPORTSDATASOURCE="reportdatasourceview_path";
	public String GETALLREPORTSPARAMSMAP = "getAllReportParamsMap";
	public String FIND_PARAMS_OF_REPORTID="findParamsOfReportId";
	public String ADDLISTREPORTSPARAMSMAP = "reportparamsmapaddlist_path";
	public String ADDLISTREPORTSUSERPARAMS="reportuserparamslist_path";
	public String PROCESSREPORTSUSERPARAMS = "reportsuserprocess_path";
	public String FIND_JASPER_OF_REPORTID = "findJasperOfReportId";
	public String FIND_IN_USE_PARAMETER="findsInUseParam";
	public String FIND_IN_USE_DATASOURCE="findInUseDataSource";
	public String VIEW_DATASOURCE="viewDataSource";
	public String VIEW_PARAMETER="viewParameter";
///ENDED BY MFTECH
	public String BIRTREPORTPATH = "birtreport_path";
	public String ADMINDOCBIRTREPORTPATH = "admindocumentreport_path";
	
	public static final String ERROR_TITLE="error.titleName";
	public static final String ERROR_CATEGORYID="error.categoryId";
	public static final String ERROR_FILE="error.file";
	public static final String ERROR_ACCOUNTTYPE="error.accountType";
	public static final String ERROR_STATUS = "error.status";
	public static final String ERROR_TITLEALREADYEXIST="error.titleAlreadyExist";
	public static final String HIDDEN_PASSWORD = "********";
	public static final String ERROR_CATEGORYNAME="error.categoryName";
	public static final String ERROR_CATEGORYNAMEALREADYEXIST="error.categoryNameAlreadyExist";
	public static final String ERROR_CATEGORYNAMENOTEDIT = "error.categoryNameNotEdit";
	public static final String ERROR_CATEGORYHASREPORTS="error.categoryHasReports";
	public static final String ERROR_REPORTINFONOTEDIT="error.reportInfoNotEdit";
	public static final String ERROR_REPORTALREADYEXIST="error.reportAlreadyExist";
	public static final String ERROR_REPORTACTIVITYIDISNULL="error.reportActivityIdIsNull";
	public static final String ERROR_NOMOREDYNAMICACTIVITYID="error.noMoreDynamicActivityId";
//added by collection sheet report module
	public static final String SELECT_DISPLAY_NAME = "---Select---";
	public static final String NOT_APPLICABLE_DISPLAY_NAME = "---N/A---";
	public static final String ALL_DISPLAY_NAME = "ALL";
	public static final Short NOT_APPLICABLE_ID = new Short("-3");
	public static final Short SELECT_ID = new Short("-2");
	public static final Short ALL_ID = new Short("-1");
	public static final Date NA_DATE = new Date(0l);
//ended by collection sheet report module
}
