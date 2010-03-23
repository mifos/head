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

package org.mifos.test.acceptance.framework.loan;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;


public class RedoLoanDisbursalEntryPage extends MifosPage {
    public RedoLoanDisbursalEntryPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("LoanCreationDetail");
    }

    public RedoLoanDisbursalSchedulePreviewPage submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(RedoLoanDisbursalParameters params) {
        this.typeTextIfNotEmpty("loancreationdetails.input.sumLoanAmount", params.getLoanAmount());
        this.typeTextIfNotEmpty("loancreationdetails.input.interestRate", params.getInterestRate());
        this.typeTextIfNotEmpty("loancreationdetails.input.numberOfInstallments", params.getNumberOfInstallments());
        selenium.type("disbursementDateDD", params.getDisbursalDateDD());
        selenium.type("disbursementDateMM", params.getDisbursalDateMM());
        selenium.type("disbursementDateYY", params.getDisbursalDateYYYY());

        selenium.fireEvent("disbursementDateYY", "blur");

        selenium.click("loancreationdetails.button.continue");
        waitForPageToLoad();

        return new RedoLoanDisbursalSchedulePreviewPage(selenium);
    }
}
