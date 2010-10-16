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

public class ViewInstallmentDetailsPage extends AbstractPage {
    String validateButton = "validateBtn";
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yyyy");

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

    public ViewInstallmentDetailsPage validateDateField(DateTime disbursalDate, int minGap, int maxGap, int noOfInstallments) {
        validateBlankDate(noOfInstallments);
        validateInvalidDateFormat(noOfInstallments,disbursalDate,"dd-MM-");
//        validateDateOrder(disbursalDate,minGap, noOfInstallments);
        validateGapForFirstDateAndDisbursalDate(disbursalDate);
        validateErrorForInstallmentGapsLessThanMinGap(minGap, noOfInstallments, disbursalDate);
        validateErrorForInstallmentGapsGraterThanMaxGap(maxGap, noOfInstallments, disbursalDate);
        return this;
    }

    private void validateErrorForInstallmentGapsGraterThanMaxGap(int maxGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstallment = getValidDate(disbursalDate, maxGap);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            selenium.type("installment.dueDate." + installment, dateTimeFormatter.print(nextInstallment));
            nextInstallment=getValidDate(nextInstallment, maxGap+1);
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Gap between the due dates of installment "+(installment+1)+" and the previous installment is more than allowed"));
        }
    }

//    private void validateDateOrder(DateTime cal, int minGap, int noOfInstallment) {
//        for (int installment = noOfInstallment -1; installment == 0 ; installment--) {
//            cal.add(Calendar.DATE, minGap+1);
//            selenium.type("installment.dueDate." + installment, getFormattedDate(cal.getTime()));
//        }
//        clickValidateAndWaitForPAgeToLoad();
//        for (int installment = 0; installment < noOfInstallment ; installment++) {
//            Assert.assertTrue(selenium.isTextPresent("Installment " + String.valueOf(installment+1) + " has invalid date. Installment due dates should be in ascending order"));
//        }
//    }

    private void validateBlankDate(double noOfInstallment) {
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            selenium.type("installment.dueDate." + installment, " ");
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 01-Apr-2010"));
        }
    }

    private void validateInvalidDateFormat(int noOfInstallments, DateTime disbursalDate, String dateFormat) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = nextInstallment.plusDays(1);
            selenium.type("installment.dueDate." + installment, DateTimeFormat.forPattern(dateFormat).print(nextInstallment));
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 01-Apr-2010"));
        }
    }

    private void validateErrorForInstallmentGapsLessThanMinGap(int minGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstalment = getValidDate(disbursalDate,minGap);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            selenium.type("installment.dueDate." + installment, dateTimeFormatter.print(nextInstalment));
            nextInstalment = getValidDate(nextInstalment,minGap-1);
            }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Gap between the due dates of installment "+ (installment+1)+" and the previous installment is less than allowed"));
        }
//        Assert.assertTrue(selenium.isTextPresent("Gap between disbursal date and due date of first installment is less than the allowable minimum gap"));
    }

    private DateTime getValidDate(DateTime disbursalDate, int minGap) {
        DateTime dateTime = disbursalDate.plusDays(minGap);
        if (dateTime.getDayOfWeek()==7) {
            dateTime = dateTime.plusDays(1);
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

    public void verifyInstallmentTotal(int noOfInstallments, int minInstalmentAmount) {
        verifyBlankTotalField(noOfInstallments);
        verifyErrorForTotalLessThanMinAmount(minInstalmentAmount,noOfInstallments);
        verifyErrorForInvalidTotal(noOfInstallments);
    }

    private void verifyErrorForInvalidTotal(int noOfInstallments) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type("installments["+installment+"].total","abc11" );
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has invalid total amount"));
        }
    }

    private void verifyErrorForTotalLessThanMinAmount(int minInstalmentAmount, int noOfInstallments) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type("installments["+installment+"].total",String.valueOf(minInstalmentAmount-1) );
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has total amount less than the allowed value"));
        }
    }

    private void verifyBlankTotalField(int noOfInstallments) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type("installments["+installment+"].total"," ");
        }
        clickValidateAndWaitForPAgeToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertTrue(selenium.isTextPresent("Installment "+(installment+1)+" has invalid total amount"));
        }

    }
}
