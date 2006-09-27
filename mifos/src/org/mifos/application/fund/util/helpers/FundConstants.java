/**

 * FundConstants.java    version: 1.0

 

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

package org.mifos.application.fund.util.helpers;

public interface FundConstants {	
	public static final String GLCODELIST="glCodeList";
	public final String FUNDSPATH = "funds";
	public String GL_CODES = "masterdata.GlCodes";
	public static final String FUNDVO = "fundVO";
	public static final String ALL_FUNDS_PAGE = "search_success";
	public static final String FUND_CREATE_PREVIEW_PAGE = "previewCreate_success";
	public static final String FUND_EDIT_PREVIEW_PAGE = "previewEdit_success";
	public static final String FUND_CREATE_PREVIEW__FAILUREPAGE = "previewCreate_failure";
	public static final String FUND_EDIT_PREVIEW_FAILURPAGE = "previewEdit_failure";
	public static final String FUND_CREATE_PAGE = "previousCreate_success";
	public static final String FUND_EDIT_PAGE = "previousEdit_success";
	public static final String DUPLICATE_FUNDNAME_EXCEPTION = "errors.DuplicateFundNameException";
	public static final String ALL_FUNDLIST = "allFunds";
	public static final String FUNDLIST = "fundList";
	public static final String MANDATORY = "errors.mandatory";
	public static final String GLCODE = "GL code";
	public static final String METHOD_GET_ALL_FUNDS = "getAllFunds";
	
	
	public String ERROR_CREATE = "errors.create";
	public String ERROR_SELECT = "errors.select";
	public String ERROR_MANDATORY="errors.mandatory";
	public String FUND_NAME="FUND_NAME";
	public String INVALID_FUND_NAME = "errors.Fund.invalidName";
	public String INVALID_FUND_CODE = "errors.Fund.invalidFundCode";
	public String FUND_ACTIONFORM = "fundActionForm";
	public String FUNDNAME = "fund Name";
	public String FUND_CODE = "fund Code";
	public String METHODCALLED="methodCalled";
}
