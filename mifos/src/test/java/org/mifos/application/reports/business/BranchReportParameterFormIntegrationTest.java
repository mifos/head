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
 
package org.mifos.application.reports.business;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.application.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;

import org.mifos.application.reports.util.helpers.ReportValidationConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class BranchReportParameterFormIntegrationTest extends AbstractReportParametersIntegrationTest {

	public BranchReportParameterFormIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final String VALID_BRANCH_REPORT_DATE = "21/01/2007";
	private static final String INVALID_BRANCH_REPORT_DATE = "01/21/2007";

	public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
		reportParams = new BranchReportParameterForm(String
				.valueOf(SELECT_BRANCH_OFFICE_SELECTION_ITEM.getId()),
				VALID_BRANCH_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.BRANCH_ID_PARAM,
				ReportValidationConstants.BRANCH_ID_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

	public void testValidatorReturnsErrorIfReportDateIsEmpty() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, "");
		errorsMock.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
				ReportValidationConstants.RUN_DATE_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}
	
	public void testReportDateFormatIsInvalid() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, INVALID_BRANCH_REPORT_DATE);
		errorsMock.rejectValue(ReportValidationConstants.RUN_DATE_PARAM,
				ReportValidationConstants.RUN_DATE_INVALID_MSG);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);		
	}
	
	public void testReportDateFormatIsValid() throws Exception {
		reportParams = new BranchReportParameterForm(VALID_ID, VALID_BRANCH_REPORT_DATE);
		replay(errorsMock);
		reportParams.validate(errorsMock);
		verify(errorsMock);
	}

}
