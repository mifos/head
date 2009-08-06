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

import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.mifos.application.reports.business.validator.Errors;
import org.mifos.application.reports.util.helpers.ReportValidationConstants;

@Ignore
public class JdbcBranchCashConfirmationReportParameterFormTest extends TestCase {

    public void testShouldAcceptValidBranchIdAndDate() {
        JdbcBranchCashConfirmationReportParameterForm form = new JdbcBranchCashConfirmationReportParameterForm("1",
                "26/06/2008");
        Errors errorsMock = createMock(Errors.class);
        replay(errorsMock);
        form.validate(errorsMock);
        verify(errorsMock);

    }

    public void testShouldReportErrorIfBranchIdAndDateAreInvalid() throws Exception {

        JdbcBranchCashConfirmationReportParameterForm form = new JdbcBranchCashConfirmationReportParameterForm("-2",
                "262008");
        Errors errors = new Errors(Locale.ENGLISH);
        String branchInvalidErrorCode = getErrorCode(errors, ReportValidationConstants.BRANCH_ID_PARAM);
        String dateInvalidErrorCode = getErrorCode(errors,
                ReportValidationConstants.RUN_DATE_PARAM_FOR_CASH_CONF_REPORT);

        form.validate(errors);

        assertTrue(errors.hasErrors());
        assertEquals(ReportValidationConstants.BRANCH_ID_INVALID_MSG, branchInvalidErrorCode);
        assertEquals(ReportValidationConstants.RUN_DATE_INVALID_MSG, dateInvalidErrorCode);

    }

    private String getErrorCode(Errors errors, String fieldName) {
        return errors.getFieldError(fieldName).getErrorCode();
    }
    
}
