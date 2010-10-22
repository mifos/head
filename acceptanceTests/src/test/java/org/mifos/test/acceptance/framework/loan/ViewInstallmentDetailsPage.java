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

import com.thoughtworks.selenium.Selenium;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.testng.Assert;

import java.util.Locale;

public class ViewInstallmentDetailsPage extends AbstractPage {
    String validateButton = "validateBtn";
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH);

    public ViewInstallmentDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("NextPaymentLoanAccount");
    }


    public void verifyInstallmentAmount(int row, int column, String amount) {
        Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), amount);
    }

    public HomePage navigateToHomePage() {
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public LoanAccountPage waiveFee() {
        selenium.click("nextPayment_loanAccount.link.waiveFeeDue");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public LoanAccountPage waivePenalty() {
        selenium.click("nextPayment_loanAccount.link.waivePenaltyDue");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public void waiveCurrentInstallmentFee() {
        selenium.click("id=nextPayment_loanAccount.link.waiveFeeDue");
        waitForPageToLoad();
    }
    public void waiveOverdueInstallmentFee() {
        selenium.click("id=nextPayment_loanAccount.link.waiveFeeOverDue");
        waitForPageToLoad();
    }

    public ViewInstallmentDetailsPage verifyVariableInstalmetDisabled() {
        selenium.isEditable ("");
        Assert.assertTrue(!selenium.isElementPresent("validateBtn"));
        return this;
    }

    public ViewInstallmentDetailsPage validateRepaymentScheduleFieldDefault(int defInstallments) {
        for (int instalment = 0; instalment < defInstallments-1  ; instalment++) {
            Assert.assertTrue(selenium.isEditable("installment.dueDate." + instalment));
        }
        for (int instalment = 0; instalment < defInstallments-2  ; instalment++) {
            Assert.assertTrue(selenium.isEditable("installments[" + instalment + "].total"));
        }
        Assert.assertTrue(!selenium.isElementPresent("installments[" + (defInstallments-1) + "].total"));

        return this;
    }

    public ViewInstallmentDetailsPage validateDateFieldValidations(DateTime disbursalDate, int minGap, int maxGap, int noOfInstallments) {
        validateBlankDate(noOfInstallments);
        validateInvalidDateFormat(noOfInstallments,disbursalDate,"dd-MM-yyyy", minGap);
        validateDateOrder(disbursalDate,minGap,noOfInstallments);
        validateErrorForSameDate(disbursalDate,minGap,noOfInstallments);
        validateGapForFirstDateAndDisbursalDate(disbursalDate);
        validateErrorForInstallmentGapsLessThanMinGap(minGap, noOfInstallments, disbursalDate);
        validateErrorForInstallmentGapsGraterThanMaxGap(maxGap, noOfInstallments, disbursalDate);
        return this;
    }

    private void validateErrorForSameDate(DateTime disbursalDate, int minGap, int noOfInstallments) {
        DateTime nextInstallmentDate = getValidDate(disbursalDate, minGap, true);
        for (int iterator = 0; iterator < noOfInstallments-1 ; iterator++) {
            fillDate(disbursalDate, minGap, noOfInstallments, true);
            DateTime currentInstallmentDate = nextInstallmentDate;
            nextInstallmentDate = getValidDate(currentInstallmentDate, minGap, true);
            selenium.type("installment.dueDate." + iterator, dateTimeFormatter.print(currentInstallmentDate));
            selenium.type("installment.dueDate." + (iterator+1), dateTimeFormatter.print(currentInstallmentDate));
            clickValidateAndWaitForPAgeToLoad();
            Assert.assertTrue(selenium.isTextPresent("Installments [" + (iterator+1) +", " + (iterator+2) +"] have the same due date"));
        }

        DateTime validDate = getValidDate(disbursalDate, minGap, true);
        selenium.type("installment.dueDate.0", dateTimeFormatter.print(validDate));
        StringBuffer stringBuffer = new StringBuffer("1");
        for (int iterator = 1; iterator < noOfInstallments ; iterator++) {
            selenium.type("installment.dueDate." + iterator, dateTimeFormatter.print(validDate));
            stringBuffer = stringBuffer.append(", ").append(iterator+1);
        }
        clickValidateAndWaitForPAgeToLoad();
        Assert.assertTrue(selenium.isTextPresent("Installments [" + stringBuffer.toString() .trim() +"] have the same due date"));

    }

    private void validateErrorForInstallmentGapsGraterThanMaxGap(int maxGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstallment = getValidDate(disbursalDate, maxGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            selenium.type("installment.dueDate." + installment, dateTimeFormatter.print(nextInstallment));
            nextInstallment=getValidDate(nextInstallment, maxGap+1, true);
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Gap between the due dates of installment "+(installment+1)+" and the previous installment is more than allowed"));
        }
    }

    private void validateDateOrder(DateTime disbursalDate, int minGap, int noOfInstallments) {
        DateTime nextInstallmentDate = getValidDate(disbursalDate, minGap, true);
        for (int iterator = 0; iterator < noOfInstallments-1 ; iterator++) {
            fillDate(disbursalDate, minGap, noOfInstallments, true);
            DateTime currentInstallmentDate = nextInstallmentDate;
            nextInstallmentDate = getValidDate(currentInstallmentDate, minGap, true);
            selenium.type("installment.dueDate." + iterator, dateTimeFormatter.print(nextInstallmentDate));
            selenium.type("installment.dueDate." + (iterator+1), dateTimeFormatter.print(currentInstallmentDate));
            clickValidateAndWaitForPAgeToLoad();
            Assert.assertTrue(selenium.isTextPresent("Installment " + (iterator+2) + " has an invalid due date. Installment due dates should be in ascending order"));
        }
    }

    private void fillDate(DateTime disbursalDate, int gap, int noOfInstallments, boolean IsGapMinimumGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = getValidDate(nextInstallment,gap, IsGapMinimumGap);
            selenium.type("installment.dueDate." + installment, dateTimeFormatter.print(nextInstallment));
        }
    }

    private void validateBlankDate(double noOfInstallment) {
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            selenium.type("installment.dueDate." + installment, "");
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 23-Apr-2010"));
        }
    }

    private void validateInvalidDateFormat(int noOfInstallments, DateTime disbursalDate, String dateFormat, int minGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = nextInstallment.plusDays(minGap);
            selenium.type("installment.dueDate." + installment, DateTimeFormat.forPattern(dateFormat).print(nextInstallment));
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments; installment++) {
           // Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 23-Apr-2010"));
        }
    }

    private void validateErrorForInstallmentGapsLessThanMinGap(int minGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstalment = getValidDate(disbursalDate,minGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            selenium.type("installment.dueDate." + installment, dateTimeFormatter.print(nextInstalment));
            nextInstalment = getValidDate(nextInstalment,minGap-1, true);
            }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Gap between the due dates of installment "+ (installment+1)+" and the previous installment is less than allowed"));
        }
//        Assert.assertTrue(selenium.isTextPresent("Gap between disbursal date and due date of first installment is less than the allowable minimum gap"));
    }

    private DateTime getValidDate(DateTime disbursalDate, int minGap, boolean isGapIsMinimumGap) {
        DateTime dateTime = disbursalDate.plusDays(minGap);
        if (dateTime.getDayOfWeek()==7) {
            if (isGapIsMinimumGap) {
                dateTime = dateTime.plusDays(1);
            } else {
                dateTime = dateTime.minusDays(1);
            }
        }
        return dateTime;
    }

    private void validateGapForFirstDateAndDisbursalDate(DateTime disbursalDate) {
        selenium.type("installment.dueDate.0", dateTimeFormatter.print(disbursalDate));
        clickValidateAndWaitForPAgeToLoad();
        Assert.assertTrue(selenium.isTextPresent("Installment 1 has due date which falls on the disbursal date"));

        selenium.type("installment.dueDate.0", dateTimeFormatter.print(disbursalDate.minusDays(1)));
        clickValidateAndWaitForPAgeToLoad();
        Assert.assertTrue(selenium.isTextPresent("Installment 1 has due date which falls before the disbursal date"));
    }

    private void clickValidateAndWaitForPAgeToLoad() {
        selenium.click(validateButton);
        selenium.waitForPageToLoad("3000");
    }

    public ViewInstallmentDetailsPage verifyInstallmentTotalValidations(int noOfInstallments, int minInstalmentAmount) {
        verifyBlankTotalField(noOfInstallments);
        verifyErrorForTotalLessThanMinAmount(minInstalmentAmount,noOfInstallments);
        verifyErrorForInvalidTotal(noOfInstallments);
        return this;
    }

    private void verifyErrorForInvalidTotal(int noOfInstallments) {
        fillAllTotalFields(noOfInstallments, "abcd123");
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has invalid total amount"));
        }
    }

    private void verifyErrorForTotalLessThanMinAmount(int minInstalmentAmount, int noOfInstallments) {
        fillAllTotalFields(noOfInstallments, String.valueOf(minInstalmentAmount-1));
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has total amount less than the allowed value"));
        }
    }

    private void verifyBlankTotalField(int noOfInstallments) {
        fillAllTotalFields(noOfInstallments, "");
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has invalid total amount"));
        }

    }

    private void fillAllTotalFields(int noOfInstallments, String installmentAmount) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type("installments["+installment+"].total", installmentAmount);
        }
    }

    public ViewInstallmentDetailsPage verifyValidData(int noOfInstallments, int minGap, int minInstalmentAmount, DateTime disbursalDate, int maxGap) {
        fillAllTotalFields(noOfInstallments, String.valueOf(minInstalmentAmount));
        fillDate(disbursalDate,minGap,noOfInstallments, true);
        clickValidateAndWaitForPAgeToLoad();
        verifyNoErrorMessageIsThrown();
        fillDate(disbursalDate,maxGap,noOfInstallments, false);
        verifyNoErrorMessageIsThrown();
        return this;
    }

    private void verifyNoErrorMessageIsThrown() {
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li"));
    }

    public void verifyCashFlow(int cashFlowIncremental) {
        String tableXpath = "//form/table//table[2]/tbody/tr[5]/td/table/tbody/tr[2]/td/table";
        int noOfMonths = selenium.getXpathCount(tableXpath + "//tr").intValue() - 1;
        int  cashFlow = cashFlowIncremental;
        for (int rowIndex = 1; rowIndex <= noOfMonths ; rowIndex++) {
            Assert.assertEquals(selenium.getText(tableXpath + "//tr[" + (rowIndex+1) + "]/td[2]"),String.valueOf(cashFlow));
            Assert.assertEquals(selenium.getText(tableXpath + "//tr[" + (rowIndex+1) + "]/td[5]"), "notes" + rowIndex);
            cashFlow = cashFlow + cashFlowIncremental;
        }

    }
}
