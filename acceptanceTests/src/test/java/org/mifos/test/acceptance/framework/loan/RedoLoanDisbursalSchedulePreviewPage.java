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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.MifosPage;
import com.thoughtworks.selenium.Selenium;
import java.util.Locale;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class RedoLoanDisbursalSchedulePreviewPage extends MifosPage {

    DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH);
    String previewButton = "previewBtn";
    String totalField = "paymentDataBeans[%s].total";
    String dateField = "paymentDataBeans[%s].dueDate";
    String paidDateField = "actualPaymentDates[%s]";
    String paidAmountField = "actualPaymentAmounts[%s]";
    String dateFieldDatePicker = "//input[@name='paymentDataBeans[%s].dueDate']/following-sibling::img[@class='ui-datepicker-trigger']";
    String actualDateFieldDatePicker = "//input[@name='paymentDataBeans[%s].date']/following-sibling::img[@class='ui-datepicker-trigger']";
    String editScheduleInformation = "//input[@id='createloanpreview.button.edit' and @name='editButton' and @value='Edit Loan Schedule Information']";

    public RedoLoanDisbursalSchedulePreviewPage(Selenium selenium) {
        super(selenium);
        verifyPage("SchedulePreview");
    }

    public RedoLoanDisbursalPreviewPage submitAndNavigateToRedoLoanDisbursalPreviewPage() {
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        return new RedoLoanDisbursalPreviewPage(selenium);
    }

    public void typeAmountPaid(int amountPaid, int row) {
        selenium.type("name="+String.format(paidAmountField, row), String.valueOf(amountPaid));
    }

    public RedoLoanDisbursalSchedulePreviewPage validateRepaymentScheduleFieldDefault(int defInstallments) {
        for (int instalment = 0; instalment < defInstallments-1  ; instalment++) {
            assertTrue(selenium.isEditable(String.format(dateField,instalment)));
            assertTrue(selenium.isVisible(String.format(dateFieldDatePicker, instalment)));
            assertTrue(selenium.isEditable(String.format(paidDateField,instalment)));
            assertTrue(selenium.isVisible(String.format(actualDateFieldDatePicker, instalment)));
            assertTrue(selenium.isEditable(String.format(paidAmountField,instalment)));
        }
        for (int instalment = 0; instalment < defInstallments-2  ; instalment++) {
            assertTrue(selenium.isEditable(String.format(totalField,instalment)));
        }
        assertTrue(!selenium.isElementPresent(String.format(totalField,defInstallments-1)));
        return this;
    }

    public RedoLoanDisbursalSchedulePreviewPage validateDateFieldValidations(DateTime disbursalDate, String minGap, String maxGap, int noOfInstallments) {
        validateInvalidDateFormat(noOfInstallments, disbursalDate, "dd-MM-yyyy", Integer.parseInt(minGap));
        validateBlankDate(noOfInstallments);
        validateDateOrder(disbursalDate,Integer.parseInt(minGap),noOfInstallments);
        validateGapForFirstDateAndDisbursalDate(disbursalDate);
        validateErrorForSameDate(disbursalDate, Integer.parseInt(minGap), noOfInstallments);
        validateErrorForInstallmentsGapGraterThanMaxGap(Integer.parseInt(maxGap), noOfInstallments, disbursalDate);
        validateErrorForInstallmentsGapLessThanMinGap(Integer.parseInt(minGap), noOfInstallments, disbursalDate);
        return this;
    }

    private void validateErrorForSameDate(DateTime disbursalDate, int minGap, int noOfInstallments) {
        DateTime nextInstallmentDate = getValidDate(disbursalDate, minGap, true);
        for (int iterator = 0; iterator < noOfInstallments-1 ; iterator++) {
            fillDate(disbursalDate, minGap, noOfInstallments, true);
            DateTime currentInstallmentDate = nextInstallmentDate;
            nextInstallmentDate = getValidDate(currentInstallmentDate, minGap, true);
            setInstallmentDate(String.valueOf(iterator), dateFormatter.print(currentInstallmentDate));
            setInstallmentDate(String.valueOf(iterator+1), dateFormatter.print(currentInstallmentDate));
            clickPreviewButtonAndWaitForPageToLoad();
            String s = "Installments [" + (iterator + 1) + ", " + (iterator + 2) + "] have the same due date";
            isTextPresentInPage(s);
        }
        DateTime validDate = getValidDate(disbursalDate, minGap, true);
        setInstallmentDate("0", dateFormatter.print(validDate));
        StringBuffer stringBuffer = new StringBuffer("1");
        for (int index = 1; index < noOfInstallments ; index++) {
            setInstallmentDate(String.valueOf(index), dateFormatter.print(validDate));
            stringBuffer = stringBuffer.append(", ").append(index+1);
        }
        clickPreviewButtonAndWaitForPageToLoad();
        isTextPresentInPage("Installments [" + stringBuffer.toString() .trim() +"] have the same due date");
    }

    private void isTextPresentInPage(String validationMessage) {
        assertTrue(selenium.isTextPresent(validationMessage), validationMessage);
        assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/ul/li[text()='']"), "Blank Error message is thrown");
        assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/ul/li[text()=' ']"), "Blank Error message is thrown");
    }

    private void validateErrorForInstallmentsGapGraterThanMaxGap(int maxGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstallment = getValidDate(disbursalDate, maxGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            setInstallmentDate(String.valueOf(installment), dateFormatter.print(nextInstallment));
            nextInstallment=getValidDate(nextInstallment, maxGap+1, true);
        }
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Gap between the due dates of installment "+(installment+1)+" and the previous installment is more than allowed");
        }
    }

    private void validateDateOrder(DateTime disbursalDate, int minGap, int noOfInstallments) {
        DateTime nextInstallmentDate = getValidDate(disbursalDate, minGap, true);
        for (int index = 0; index < noOfInstallments-1 ; index++) {
            fillDate(disbursalDate, minGap, noOfInstallments, true);
            DateTime currentInstallmentDate = nextInstallmentDate;
            nextInstallmentDate = getValidDate(currentInstallmentDate, minGap, true);
            setInstallmentDate(String.valueOf(index), dateFormatter.print(nextInstallmentDate));
            setInstallmentDate(String.valueOf(index+1), dateFormatter.print(currentInstallmentDate));
            clickPreviewButtonAndWaitForPageToLoad();
            isTextPresentInPage("Installment " + (index+2) + " has an invalid due date. Installment due dates should be in ascending order");
        }
    }

    private void fillDate(DateTime disbursalDate, int gap, int noOfInstallments, boolean IsGapMinimumGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = getValidDate(nextInstallment,gap, IsGapMinimumGap);
            setInstallmentDate(String.valueOf(installment), dateFormatter.print(nextInstallment));
        }
    }

    private void validateBlankDate(double noOfInstallment) {
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            setInstallmentDate(String.valueOf(installment), "");
        }
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            isTextPresentInPage("Installment " + (installment+1) +" has an invalid due date. An example due date is 23-Apr-2010");
        }
    }

    private void validateInvalidDateFormat(int noOfInstallments, DateTime disbursalDate, String dateFormat, int minGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = nextInstallment.plusDays(minGap);
            setInstallmentDate(String.valueOf(installment), DateTimeFormat.forPattern(dateFormat).print(nextInstallment));
        }
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments; installment++) {
           // Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 23-Apr-2010"));
        }
    }

    private void validateErrorForInstallmentsGapLessThanMinGap(int minGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstalment = getValidDate(disbursalDate,minGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            setInstallmentDate(String.valueOf(installment), dateFormatter.print(nextInstalment));
            nextInstalment = getValidDate(nextInstalment,minGap-1, true);
            }
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Gap between the due dates of installment "+ (installment+1)+" and the previous installment is less than allowed");
        }
    }

    private DateTime getValidDate(DateTime date, int minimumGap, boolean isGapIsMinimumGap) {
        DateTime dateTime = date.plusDays(minimumGap);
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
        setInstallmentDate("0", dateFormatter.print(disbursalDate));
        clickPreviewButtonAndWaitForPageToLoad();
        isTextPresentInPage("Installment 1 has due date which falls on the disbursal date");

        setInstallmentDate("0", dateFormatter.print(disbursalDate.minusDays(1)));
        clickPreviewButtonAndWaitForPageToLoad();
        isTextPresentInPage("Installment 1 has due date which falls before the disbursal date");
    }

    private void clickPreviewButtonAndWaitForPageToLoad() {
        selenium.click(previewButton);
        selenium.waitForPageToLoad("3000");
    }

    public RedoLoanDisbursalSchedulePreviewPage verifyInstallmentTotalValidations(int noOfInstallments, String minInstalmentAmount, DateTime disbursalDate, String gap) {
        verifyBlankTotalField(noOfInstallments);
        verifyErrorForTotalLessThanMinAmount(Integer.parseInt(minInstalmentAmount),noOfInstallments, disbursalDate, Integer.parseInt(gap));
        verifyErrorForInvalidTotal(noOfInstallments);
        return this;
    }

    private void verifyErrorForInvalidTotal(int noOfInstallments) {
        fillAllFields(noOfInstallments, totalField, "abcd123");
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Installment "+(installment+1)+" has invalid total amount");
        }
    }

    private void verifyErrorForTotalLessThanMinAmount(int minInstalmentAmount, int noOfInstallments, DateTime disbursalDate, int gap) {
        fillDate(disbursalDate, gap,noOfInstallments, true);
        fillAllFields(noOfInstallments, totalField, String.valueOf(minInstalmentAmount - 1));
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Installment "+(installment+1)+" has total amount less than the allowed value");
        }
    }

    private void verifyBlankTotalField(int noOfInstallments) {
        fillAllFields(noOfInstallments, totalField, "");
        fillAllFields(noOfInstallments, paidAmountField, "");
        clickPreviewButtonAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Installment "+(installment+1)+" has invalid total amount");
        }

    }

    private void fillAllFields(int noOfInstallments, String totalField, String installmentAmount) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type(String.format(totalField,installment), installmentAmount);
        }
    }

    public RedoLoanDisbursalSchedulePreviewPage verifyValidData(int noOfInstallments, String minGap, String minInstalmentAmount, DateTime disbursalDate, String maxGap) {
        fillAllFields(noOfInstallments, totalField, String.valueOf(minInstalmentAmount));
        fillDate(disbursalDate, Integer.parseInt(minGap), noOfInstallments, true);
        clickPreviewButtonAndWaitForPageToLoad();
        verifyPage("CreateLoanPreview");
        selenium.click(editScheduleInformation);
        waitForPageToLoad();
        fillDate(disbursalDate, Integer.parseInt(maxGap), noOfInstallments, false);
        clickPreviewButtonAndWaitForPageToLoad();
        verifyPage("CreateLoanPreview");
        selenium.click(editScheduleInformation);
        waitForPageToLoad();
        return this;
    }

    private void setInstallmentDate(String installment, String date) {
        selenium.type(String.format(dateField,installment), date);
    }

    public RedoLoanDisbursalSchedulePreviewPage verifyRecalculationWhenDateAndTotalChange() {
        setInstallmentTotal(1,"200");
        setInstallmentTotal(2,"150");
        setInstallmentTotal(3,"170");
        setInstallmentTotal(4,"210");
        setInstallmentDate("0", "13-Oct-2010");
        setInstallmentDate("1", "20-Oct-2010");
        setInstallmentDate("2", "27-Oct-2010");
        setInstallmentDate("3","01-Nov-2010");
        setInstallmentDate("4","10-Nov-2010");
        clickPreviewButtonAndWaitForPageToLoad();
        selenium.click(editScheduleInformation);
        waitForPageToLoad();
        verifyCellValueOfInstallments(5,8, "281.3");
        verifyCellValueOfInstallments(1,6, "1.3");
        verifyCellValueOfInstallments(2,6, "3.7");
        verifyCellValueOfInstallments(3,6, "3.0");
        verifyCellValueOfInstallments(4,6, "1.6");
        verifyCellValueOfInstallments(5,6, "1.7");
        return this;
    }

    private void verifyCellValueOfInstallments(int row, int column, String value) {
        assertEquals(selenium.getText("//table[@id='scheduleTable']//tr[" + (row + 2) + "]/td[" + column + "]"), value);
    }

    private void setInstallmentTotal(int installment, String total) {
        selenium.type(String.format(totalField,installment-1),total);
    }

    public RedoLoanAccountPreviewPage clickPreviewAndGoToReviewLoanAccountPage() {
        selenium.click("schedulePreview.button.preview");
        selenium.waitForPageToLoad("3000");
        return new RedoLoanAccountPreviewPage(selenium);
    }

    public RedoLoanDisbursalSchedulePreviewPage setPaidField(String[][] payment) {
        for (int rowIndex = 0; rowIndex < payment.length; rowIndex++) {
            selenium.type(String.format(paidDateField,rowIndex),payment[rowIndex][0]);
            selenium.type(String.format(paidAmountField,rowIndex),payment[rowIndex][1]);
        }
        return this;
    }
}
