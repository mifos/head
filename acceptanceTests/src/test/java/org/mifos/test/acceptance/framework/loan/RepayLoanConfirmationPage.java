/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.lang.StringUtils;
import org.mifos.test.acceptance.framework.MifosPage;

public class RepayLoanConfirmationPage extends MifosPage {

    public RepayLoanConfirmationPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("ReviewRepayLoan");
    }

    public LoanAccountPage submitAndNavigateToLoanAccountDetailsPage() {
        selenium.click("Review_RepayLoan.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }
    
    public RepayLoanConfirmationPage submitWithError(){
        selenium.click("Review_RepayLoan.button.submit");
        waitForPageToLoad();
        return new RepayLoanConfirmationPage(selenium);
    }

    public String getSelectedValueForInterestWaiver() {
        String result = null;
        if (!StringUtils.equals("null", selenium.getEval("window.document.getElementById('waiverInterest')"))) {
            result = selenium.getEval("window.document.getElementById('waiverInterest').innerHTML").trim();
        }
        return result;
    }

    public RepayLoanPage edit() {
        selenium.click("Review_RepayLoan.button.edit");
        waitForPageToLoad();
        return new RepayLoanPage(selenium);
    }
}
