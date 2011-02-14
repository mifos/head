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

package org.mifos.test.acceptance.framework.loanproduct.multicurrrency;

import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;

import com.thoughtworks.selenium.Selenium;

public class DefineNewDifferentCurrencyLoanProductPage extends DefineNewLoanProductPage {

    public DefineNewDifferentCurrencyLoanProductPage() {
        super();
    }

    public DefineNewDifferentCurrencyLoanProductPage(Selenium selenium) {
        super(selenium);
    }

    @Override
    public void verifyPage() {
          this.verifyPage("CreateLoanProduct");

    }

    @Override
    public DefineNewLoanProductPage submitPage() {
        return this;
    }

    public static class SubmitMultiCurrencyFormParameters extends SubmitFormParameters{
        private Short currencyId;

        public void setCurrencyId(Short currencyId) {
            this.currencyId = currencyId;
        }

        public Short getCurrencyId() {
            return currencyId;
        }
    }

    public void fillLoanParameters(SubmitMultiCurrencyFormParameters parameters) {
        super.fillLoanParameters(parameters);
        selenium.select("currencyId", "value=" + parameters.getCurrencyId());
    }
}
