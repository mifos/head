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

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;

import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

public class DetailedAgingPortfolioReportParametersTest extends TestCase {

    private Errors errorsMock;

    public void testValidatorNoErrorIfAllParamsValid() throws Exception {
        DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
                DetailedAgingPortfolioReportParameters.VALID_ID, DetailedAgingPortfolioReportParameters.VALID_ID,
                DetailedAgingPortfolioReportParameters.VALID_ID);
        replay(errorsMock);
        reportParameters.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsErrorIfBranchIdIsSelect() throws Exception {
        DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
                DetailedAgingPortfolioReportParameters.INVALID_ID, DetailedAgingPortfolioReportParameters.VALID_ID,
                DetailedAgingPortfolioReportParameters.VALID_ID);
        errorsMock.rejectValue(ReportValidationConstants.BRANCH_ID_PARAM,
                ReportValidationConstants.BRANCH_ID_INVALID_MSG);
        replay(errorsMock);
        reportParameters.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsErrorIfLoanOfficerIsSelect() throws Exception {
        DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
                DetailedAgingPortfolioReportParameters.VALID_ID, DetailedAgingPortfolioReportParameters.INVALID_ID,
                DetailedAgingPortfolioReportParameters.VALID_ID);
        errorsMock.rejectValue(ReportValidationConstants.LOAN_OFFICER_ID_PARAM,
                ReportValidationConstants.LOAN_OFFICER_ID_INVALID_MSG);
        replay(errorsMock);
        reportParameters.validate(errorsMock);
        verify(errorsMock);
    }

    public void testValidatorReturnsErrorIfLoanProductIsSelect() throws Exception {
        DetailedAgingPortfolioReportParameters reportParameters = new DetailedAgingPortfolioReportParameters(
                DetailedAgingPortfolioReportParameters.VALID_ID, DetailedAgingPortfolioReportParameters.VALID_ID,
                DetailedAgingPortfolioReportParameters.INVALID_ID);
        errorsMock.rejectValue(ReportValidationConstants.LOAN_PRODUCT_ID_PARAM,
                ReportValidationConstants.LOAN_PRODUCT_ID_INVALID_MSG);
        replay(errorsMock);
        reportParameters.validate(errorsMock);
        verify(errorsMock);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        errorsMock = createMock(Errors.class);
    }
}
