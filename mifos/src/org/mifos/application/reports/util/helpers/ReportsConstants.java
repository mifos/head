/**

 * TestReportsAction.java    version: 1.0

 

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

public interface ReportsConstants {
	public String GETPATH_REPORTS="reportsAction";
	public String GETREPORTPAGE="getReportPage";
	public String GETALLREPORTS="getAllReports";
	public String LISTOFREPORTS="listOfReports";	
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
	
	public static final String ERROR_TITLE="error.titleName";
	public static final String ERROR_CATEGORYID="error.categoryId";
	public static final String ERROR_FILEISNULL="error.fileIsNull";
	public static final String ERROR_TITLEALREADYEXIST="error.titleAlreadyExist";
}
