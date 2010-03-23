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

package org.mifos.reports.business.validator;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.reports.business.BranchReportParameterForm;
import org.mifos.reports.business.service.BranchReportService;
import org.mifos.reports.business.service.IBranchReportService;
import org.mifos.reports.util.helpers.ReportValidationConstants;

public class BranchReportParameterValidatorTest extends TestCase {
    private static final String BRANCH_ID = "2";
    private static final String VALID_RUN_DATE = "01/01/2008";
    private static final String INVALID_BRANCH_ID = "-2";
    private IBranchReportService branchReportServiceMock;
    private BranchReportParamValidator validator;
    private Errors errors;
    private BranchReportParameterForm validForm;
    private BranchReportParameterForm invalidForm;

    public void testValidatorCallsServiceIfDataPresent() throws Exception {
        expect(branchReportServiceMock.isReportDataPresentForRundateAndBranchId(BRANCH_ID, VALID_RUN_DATE)).andReturn(
                Boolean.TRUE);
        replay(branchReportServiceMock);

        Errors errors = new Errors(null);
        validator.validate(new BranchReportParameterForm(BRANCH_ID, VALID_RUN_DATE), errors);
        verify(branchReportServiceMock);
        Assert.assertFalse(errors.hasErrors());
    }

    public void testValidatorAddsErrorIfServiceSaysNoDataFound() throws Exception {
        expect(branchReportServiceMock.isReportDataPresentForRundateAndBranchId(BRANCH_ID, VALID_RUN_DATE)).andReturn(
                Boolean.FALSE);
        replay(branchReportServiceMock);
        validator.validate(validForm, errors);
        verify(branchReportServiceMock);
       Assert.assertTrue(errors.hasErrors());
        ErrorEntry fieldError = errors.getFieldError(ReportValidationConstants.RUN_DATE_PARAM);
        Assert.assertNotNull(fieldError);
       Assert.assertEquals(ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG, fieldError.getErrorCode());
    }

    public void testValidatorDoesNotCheckDataPresenceIfErrorsExists() throws Exception {
        replay(branchReportServiceMock);
        validator.validate(invalidForm, errors);
        verify(branchReportServiceMock);
    }

    @Override
    protected void setUp() throws Exception {
        branchReportServiceMock = createMock(BranchReportService.class);
        validator = new BranchReportParamValidator(new ArrayList<String>(), branchReportServiceMock);
        errors = new Errors(null);
        validForm = new BranchReportParameterForm(BRANCH_ID, VALID_RUN_DATE);
        invalidForm = new BranchReportParameterForm(INVALID_BRANCH_ID, VALID_RUN_DATE);
    }
}
