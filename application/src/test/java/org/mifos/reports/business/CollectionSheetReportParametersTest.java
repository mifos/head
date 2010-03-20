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

package org.mifos.reports.business;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;

import java.text.ParseException;

import junit.framework.Assert;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.reports.util.helpers.ReportValidationConstants;

public class CollectionSheetReportParametersTest extends AbstractReportParametersTest {

    public CollectionSheetReportParametersTest() throws SystemException, ApplicationException {
        super();
    }

    private static final String VALID_REPORT_DATE = "07/03/2007 12:00:00 AM";

    public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(String.valueOf(SELECT_BRANCH_OFFICE_SELECTION_ITEM
                .getId()), AbstractReportParametersTest.VALID_ID,
                AbstractReportParametersTest.VALID_ID, VALID_REPORT_DATE);
        errorsMock.rejectValue(ReportValidationConstants.BRANCH_ID_PARAM,
                ReportValidationConstants.BRANCH_ID_INVALID_MSG);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsSelect() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, String
                .valueOf(SELECT_LOAN_OFFICER_SELECTION_ITEM.getId()), AbstractReportParametersTest.VALID_ID,
                VALID_REPORT_DATE);
        errorsMock.rejectValue(ReportValidationConstants.LOAN_OFFICER_ID_PARAM,
                ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsLoanOfficerInvalidIfLoanOfficerIsNA() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID, String
                .valueOf(NA_LOAN_OFFICER_SELECTION_ITEM.getId()), AbstractReportParametersTest.VALID_ID,
                VALID_REPORT_DATE);
        errorsMock.rejectValue(ReportValidationConstants.LOAN_OFFICER_ID_PARAM,
                ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsCenterInvalidIfCenterIsSelect() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID,
                AbstractReportParametersTest.VALID_ID, String.valueOf(SELECT_CENTER_SELECTION_ITEM.getId()),
                VALID_REPORT_DATE);
        errorsMock.rejectValue(ReportValidationConstants.CENTER_ID_PARAM,
                ReportValidationConstants.CENTER_ID_INVALID_MSG);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsCenterInvalidIfCenterIsNA() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID,
                AbstractReportParametersTest.VALID_ID, String.valueOf(NA_CENTER_SELECTION_ITEM.getId()),
                VALID_REPORT_DATE);
        errorsMock.rejectValue(ReportValidationConstants.CENTER_ID_PARAM,
                ReportValidationConstants.CENTER_ID_INVALID_MSG);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsMeetingDateInvalidIfMeetingDateIsSelect() throws Exception {
        reportParams = new CollectionSheetReportParameterForm(AbstractReportParametersTest.VALID_ID,
                AbstractReportParametersTest.VALID_ID, AbstractReportParametersTest.VALID_ID,
                VALID_REPORT_DATE);
        replay(errorsMock);
        reportParams.validate(errorsMock);
        verify(errorsMock);
    }

    public void testReportDateFormat() throws Exception {
        try {
            AbstractReportParameterForm.REPORT_DATE_PARAM_FORMAT.parse("01/01/1970 05:30:00 AM");
        } catch (ParseException e) {
            Assert.fail("Should be parsing given date");
        }
    }
}
