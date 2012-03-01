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
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

@SuppressWarnings("PMD")
public class RedoLoanAccountPreviewPage extends AbstractPage {
    String editScheduleButton = "//input[@id='createloanpreview.button.edit' and @name='editButton' and @value='Edit Loan Schedule Information']";
    String scheduleTable = "//table[@id='installments']";
    String futureInstallmentsTable = "//table[@id='futureInstallments']";
    String editScheduleInformation = "//input[@id='createloanpreview.button.edit' and @name='editButton' and @value='Edit Loan Schedule Information']";

    public RedoLoanAccountPreviewPage(Selenium selenium) {
        super(selenium);
    }

    public RedoLoanAccountPreviewPage verifyPage() {
        this.verifyPage("CreateLoanPreview");
        return this;
    }

    public void verifyEditScheduleDisabled() {
        Assert.assertTrue(!selenium.isElementPresent(editScheduleButton));
    }

    @SuppressWarnings("PMD")
    public RedoLoanAccountPreviewPage verifyRunningBalance(String[][] loanSchedule, String[][] futureInstallments, String[][] runningBalance) {
         for (int rowIndex = 0; rowIndex < loanSchedule.length; rowIndex++) {
            String[] installment = loanSchedule[rowIndex];
            for (int columnIndex = 0; columnIndex < installment.length; columnIndex++) {
                String value = installment[columnIndex];
                int row = rowIndex + 1;
                if (!"".equals(value)) {
                    int column = columnIndex + 1;
                    String actualCellValue = selenium.getText(scheduleTable + "//tr[" + row + "]/td[" + column + "]");
                    Assert.assertEquals(actualCellValue, value, "In Schedule Table for row " + row + " and column " + column + " expected value is " + value + " but the actual value is " + actualCellValue);
                }
            }
         }
         for (int rowIndex = 0; rowIndex < futureInstallments.length; rowIndex++) {
             String[] installment = futureInstallments[rowIndex];
             for (int columnIndex = 0; columnIndex < installment.length; columnIndex++) {
                 String value = installment[columnIndex];
                 int row = rowIndex + 1;
                 if (!"".equals(value)) {
                     int column = columnIndex + 1;
                     String actualCellValue = selenium.getText(futureInstallmentsTable + "//tr[" + row + "]/td[" + column + "]");
                     Assert.assertEquals(actualCellValue, value, "In Future Installments Table for row " + row + " and column " + column + " expected value is " + value + " but the actual value is " + actualCellValue);
                 }
             }
         }
         for (int rowIndex = 0; rowIndex < runningBalance.length; rowIndex++) {
            String[] installment = runningBalance[rowIndex];
            for (int columnIndex = 0; columnIndex < installment.length; columnIndex++) {
                String value = installment[columnIndex];
                int row = rowIndex + 1;
                if (!value.equals("")) {
                    int column = columnIndex + 9;
                    String actualCellValue = selenium.getText(scheduleTable + "//tr[" + row + "]/td[" + column + "]");
                    Assert.assertEquals(actualCellValue, value, "In Schedule Table for row " + row + " and column " + column + " expected value is " + value + " but the actual value is " + actualCellValue);
                }
            }
        }
        return this;

    }

    public RedoLoanDisbursalSchedulePreviewPage editSchedule() {
        selenium.click(editScheduleInformation);
        waitForPageToLoad();
        return new RedoLoanDisbursalSchedulePreviewPage(selenium);
    }
}
