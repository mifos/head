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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class CreateLoanAccountReviewInstallmentPage extends AbstractPage {
    String validateButton = "_eventId_validate";
    // TODO - English locale hard-coded
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.ENGLISH);
    DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern("dd/MM/yy").withLocale(Locale.ENGLISH);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    String tableXpath = "//table[@id='cashflow']";
    String previewButton = "_eventId_preview";
    Map<String,Integer> monthsMap = new HashMap<String,Integer>();

    public CreateLoanAccountReviewInstallmentPage(Selenium selenium) {
        super(selenium);
        monthsMap.put("January", 0);
        monthsMap.put("February", 1);
        monthsMap.put("March", 2);
        monthsMap.put("April", 3);
        monthsMap.put("May", 4);
        monthsMap.put("June", 5);
        monthsMap.put("July", 6);
        monthsMap.put("August", 7);
        monthsMap.put("September", 8);
        monthsMap.put("October", 9);
        monthsMap.put("November", 10);
        monthsMap.put("December", 11);
        this.verifyPage("SchedulePreview");
    }
    public CreateLoanAccountReviewInstallmentPage submitWithErrors(String error) {
        selenium.click(previewButton);
        selenium.waitForPageToLoad("3000");
        selenium.isTextPresent(error);
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage validate(){
    	selenium.click("schedulePreview.button.validate");
    	selenium.waitForPageToLoad("3000");
    	return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyPage() {
        this.verifyPage("SchedulePreview");
        return this;
    }

    public void verifyErrorsOnPage(String... errors) {
        for (String error : errors) {
            Assert.assertTrue(selenium.isTextPresent(error));
        }
    }

    public HomePage navigateToHomePage() {
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public CreateLoanAccountReviewInstallmentPage validateRepaymentScheduleFieldDefault(int defInstallments) {
        for (int instalment = 0; instalment < defInstallments-1  ; instalment++) {
            Assert.assertTrue(selenium.isEditable("installment.dueDate." + instalment));
        }
        for (int instalment = 0; instalment < defInstallments-2  ; instalment++) {
            Assert.assertTrue(selenium.isEditable("installmentAmounts[" + instalment + "]"));
        }
        Assert.assertTrue(!selenium.isElementPresent("installments[" + (defInstallments - 1) + "].total"));

        return this;
    }

    public CreateLoanAccountReviewInstallmentPage validateDateFieldValidations(DateTime disbursalDate, int minGap, int maxGap, int noOfInstallments) {
        validateBlankDate(noOfInstallments);
        validateInvalidDateFormat(noOfInstallments,disbursalDate,"dd/MM/yy", minGap);
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
            setInstallmentDate(String.valueOf(iterator), dateTimeFormatter.print(currentInstallmentDate));
            setInstallmentDate(String.valueOf(iterator+1), dateTimeFormatter.print(currentInstallmentDate));
            clickValidateAndWaitForPageToLoad();
            String s = "Installments [" + (iterator + 1) + ", " + (iterator + 2) + "] have the same due date";
            isTextPresentInPage(s);
        }

        DateTime validDate = getValidDate(disbursalDate, minGap, true);
        setInstallmentDate("0", dateTimeFormatter.print(validDate));
        StringBuffer stringBuffer = new StringBuffer("1");
        for (int iterator = 1; iterator < noOfInstallments ; iterator++) {
            setInstallmentDate(String.valueOf(iterator), dateTimeFormatter.print(validDate));
            stringBuffer = stringBuffer.append(", ").append(iterator+1);
        }
        clickValidateAndWaitForPageToLoad();
        isTextPresentInPage("Installments [" + stringBuffer.toString() .trim() +"] have the same due date");
    }

    private void isTextPresentInPage(String validationMessage) {
        if (!selenium.isTextPresent(validationMessage)) {
            Assert.fail(validationMessage + " was expected but not found on: " + selenium.getLocation() + " source code <br/> " + selenium.getHtmlSource());
        }
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()='']"), "Blank Error message is thrown");
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()=' ']"), "Blank Error message is thrown");
    }

    private void validateErrorForInstallmentGapsGraterThanMaxGap(int maxGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstallment = getValidDate(disbursalDate, maxGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            setInstallmentDate(String.valueOf(installment), dateTimeFormatter.print(nextInstallment));
            nextInstallment=getValidDate(nextInstallment, maxGap+1, true);
        }
        clickValidateAndWaitForPageToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Gap between the due dates of installment "+(installment+1)+" and the previous installment is more than allowed");
        }
    }

    private void validateDateOrder(DateTime disbursalDate, int minGap, int noOfInstallments) {
        DateTime nextInstallmentDate = getValidDate(disbursalDate, minGap, true);
        for (int iterator = 0; iterator < noOfInstallments-1 ; iterator++) {
            fillDate(disbursalDate, minGap, noOfInstallments, true);
            DateTime currentInstallmentDate = nextInstallmentDate;
            nextInstallmentDate = getValidDate(currentInstallmentDate, minGap, true);
            setInstallmentDate(String.valueOf(iterator), dateTimeFormatter.print(nextInstallmentDate));
            setInstallmentDate(String.valueOf(iterator+1), dateTimeFormatter.print(currentInstallmentDate));
            clickValidateAndWaitForPageToLoad();
            isTextPresentInPage("Installment " + (iterator+2) + " has an invalid due date. Installment due dates should be in ascending order");
        }
    }

    private void fillDate(DateTime disbursalDate, int gap, int noOfInstallments, boolean IsGapMinimumGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = getValidDate(nextInstallment,gap, IsGapMinimumGap);
            setInstallmentDate(String.valueOf(installment), dateTimeFormatter.print(nextInstallment));
        }
    }

    private void verifyAllDatesFields(DateTime disbursalDate, int gap, int noOfInstallments, boolean IsGapMinimumGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = getValidDate(nextInstallment,gap, IsGapMinimumGap);
            Assert.assertEquals(selenium.getValue("installment.dueDate." + installment), dateTimeFormatter2.print(nextInstallment));
        }
    }


    private void validateBlankDate(double noOfInstallment) {
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            setInstallmentDate(String.valueOf(installment), "");
        }
        clickValidateAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallment ; installment++) {
            isTextPresentInPage("The due date field for installment " + (installment+1) +" is blank.");
        }
    }

    private void validateInvalidDateFormat(int noOfInstallments, DateTime disbursalDate, String dateFormat, int minGap) {
        DateTime nextInstallment = disbursalDate;
        for (int installment = 0; installment < noOfInstallments; installment++) {
            nextInstallment = nextInstallment.plusDays(minGap);
            setInstallmentDate(String.valueOf(installment), DateTimeFormat.forPattern(dateFormat).print(nextInstallment));
        }
        clickValidateAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments; installment++) {
           // Assert.Assert.assertTrue(selenium.isTextPresent("Installment " + (installment+1) +" has an invalid due date. An example due date is 23-Apr-2010"));
        }
    }

    private void validateErrorForInstallmentGapsLessThanMinGap(int minGap, int noOfInstallments, DateTime disbursalDate) {
        DateTime nextInstalment = getValidDate(disbursalDate,minGap, true);
        for (int installment = 0; installment < noOfInstallments; installment++) {
            setInstallmentDate(String.valueOf(installment), dateTimeFormatter.print(nextInstalment));
            nextInstalment = getValidDate(nextInstalment,minGap-1, true);
            }
        clickValidateAndWaitForPageToLoad();
        for (int installment = 1; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Gap between the due dates of installment "+ (installment+1)+" and the previous installment is less than allowed");
        }
//        Assert.Assert.assertTrue(selenium.isTextPresent("Gap between disbursal date and due date of first installment is less than the allowable minimum gap"));
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
        setInstallmentDate("0", dateTimeFormatter.print(disbursalDate));
        clickValidateAndWaitForPageToLoad();
        isTextPresentInPage("Installment 1 has due date which falls on the disbursal date");

        setInstallmentDate("0", dateTimeFormatter.print(disbursalDate.minusDays(1)));
        clickValidateAndWaitForPageToLoad();
        isTextPresentInPage("Installment 1 has due date which falls before the disbursal date");
    }

    private void clickValidateAndWaitForPageToLoad() {
        selenium.click(validateButton);
        selenium.waitForPageToLoad("3000");
    }

    public CreateLoanAccountReviewInstallmentPage verifyInstallmentTotalValidations(int noOfInstallments, int minInstalmentAmount, DateTime disbursalDate, int gap) {
        verifyBlankTotalField(noOfInstallments);
        verifyErrorForTotalLessThanMinAmount(minInstalmentAmount,noOfInstallments, disbursalDate, gap);
        verifyErrorForInvalidTotal(noOfInstallments);
        return this;
    }

    private void verifyErrorForInvalidTotal(int noOfInstallments) {
        fillAllTotalFields(noOfInstallments, "abcd123");
        clickValidateAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Invalid data");
        }
    }

    private void verifyErrorForTotalLessThanMinAmount(int minInstalmentAmount, int noOfInstallments, DateTime disbursalDate, int gap) {
        fillDate(disbursalDate, gap,noOfInstallments, true);
        fillAllTotalFields(noOfInstallments, String.valueOf(minInstalmentAmount-1));
        clickValidateAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("Installment "+(installment+1)+" has total amount less than the allowed value");
        }
    }

    private void verifyBlankTotalField(int noOfInstallments) {
        fillAllTotalFields(noOfInstallments, "");
        clickValidateAndWaitForPageToLoad();
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            isTextPresentInPage("The total amount field for installment "+(installment+1)+" was blank and has been defaulted to zero.");
        }

    }

    private void fillAllTotalFields(int noOfInstallments, String installmentAmount) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            selenium.type("installmentAmounts["+installment+"]", installmentAmount);
        }
    }

    public CreateLoanAccountReviewInstallmentPage verifyValidData(int noOfInstallments, int minGap, int minInstalmentAmount, DateTime disbursalDate, int maxGap) {
        fillAllTotalFields(noOfInstallments, String.valueOf(minInstalmentAmount));
        fillDate(disbursalDate,minGap,noOfInstallments, true);
        clickValidateAndWaitForPageToLoad();
        verifyNoErrorMessageIsThrown();
        fillDate(disbursalDate,maxGap,noOfInstallments, false);
        verifyNoErrorMessageIsThrown();
        return this;
    }

    private void verifyNoErrorMessageIsThrown() {
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li"));
    }

    public CreateLoanAccountReviewInstallmentPage verifyCashFlow(double cashFlowIncremental, double loanAmount) {
        int noOfMonths = selenium.getXpathCount(tableXpath + "//tr").intValue() - 1;
        double  cashFlow = cashFlowIncremental;
        boolean cashflowAdded = false;
        // TODO - English locale hard-coded
        //DecimalFormat df = new DecimalFormat("#.0", new DecimalFormatSymbols(Locale.ENGLISH));
        for (int rowIndex = 0; rowIndex < noOfMonths - 1; rowIndex++) {
            String cashFlowDisplayed = selenium.getText(tableXpath + "//tr[" + (rowIndex + 1) + "]/td[2]");
            Assert.assertEquals(StringUtil.formatNumber(String.valueOf(cashFlowDisplayed)), StringUtil.formatNumber(String.valueOf(cashFlow)));
            Assert.assertEquals(selenium.getText(tableXpath + "//tr[" + (rowIndex + 1) + "]/td[5]"), "notes" + (rowIndex+1));
            if(!cashflowAdded){
                cashFlow += loanAmount;
                cashflowAdded = true;
            }
            cashFlow = cashFlow + cashFlowIncremental;
        }
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyCashFlow(double cashFlowIncremental, double loanAmount, String[] cumulativeCashFlowTIAPM, String[] totalInstallmentAmount) {
        int rowIndex = 1;
        for(String cash : cumulativeCashFlowTIAPM) {
            Assert.assertEquals(selenium.getText(tableXpath + "//tr[" + rowIndex + "]/td[3]"), cash);
            rowIndex++;
        }

        rowIndex = 1;
        for(String cash : totalInstallmentAmount) {
            Assert.assertEquals(selenium.getText(tableXpath + "//tr[" + rowIndex + "]/td[4]"), cash);
            rowIndex++;
        }
        return verifyCashFlow(cashFlowIncremental, loanAmount);
    }

    public void verifyCashFlowCalcualted(int cashFlowIncremental) {
        Assert.assertTrue(true);
    }

    public CreateLoanAccountReviewInstallmentPage verifyCashFlowDefaultValues() {
//        verifyCellValueOfCashFlow(3,1,"Cumulative cash flow-Total installment amount per month");
        verifyCellValueOfCashFlow(3,1,"1");
        verifyCellValueOfCashFlow(3,2,"330");
        verifyCellValueOfCashFlow(3,3,"667.4");
        verifyCellValueOfCashFlow(3,4,"1004");
//        verifyCellValueOfCashFlow(4,1,"Total installment amount per month as % of cash flow");
        verifyCellValueOfCashFlow(4,1,"0");
//        verifyCellValueOfCashFlow(4,2,"33600.00");
//        verifyCellValueOfCashFlow(4,3,"11189.33");
        verifyCellValueOfCashFlow(4,4,"0");
        return this;
    }

    private void verifyCellValueOfCashFlow(int column, int row, String value) {
        Assert.assertEquals(selenium.getText(tableXpath + "//tbody//tr[" + (row) + "]/td[" + (column) + "]"), value);
    }

    public CreateLoanAccountReviewInstallmentPage verifyRecalculationOfCashFlow() {
        verifyRecalculation(validateButton);
        verifyRecalculationForForSameMonth(validateButton);
        verifyRecalculation(previewButton);
        verifyRecalculationForForSameMonth(previewButton);
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyWarningThresholdMessageOnReviewSchedulePage(double warningThreshold) {
        verifyWarningThresholdMessageOnReviewSchedulePage(validateButton,warningThreshold);
        verifyWarningThresholdMessageOnReviewSchedulePage(previewButton, warningThreshold);
        return this;
    }

    public void verifyInstallmentDatesOutOfCashFlowCaptured() {
        verifyErrorMessageOnDatesOutOfCashFlow(validateButton);
        verifyErrorMessageOnDatesOutOfCashFlow(previewButton);
    }

    private void verifyRecalculation(String button) {
        setInstallmentDate("0", "02-Sep-2010");
        setInstallmentDate("1", "02-Oct-2010");
        setInstallmentDate("2", "02-Nov-2010");
        setFirstAndSecondInstallmentTotal("1");
        selenium.click(button);
        selenium.waitForPageToLoad("3000");
        verifyCellValueOfCashFlow(3,2,"0");
        verifyCellValueOfCashFlow(3,3,"1");
        verifyCellValueOfCashFlow(4,2,"100");
        verifyCellValueOfCashFlow(4,3,"50");
    }

    private void verifyRecalculationForForSameMonth(String button) {
        setInstallmentDate("0", "02-Sep-2010");
        setInstallmentDate("1", "02-Sep-2010");
        setInstallmentDate("2", "02-Nov-2010");
        setFirstAndSecondInstallmentTotal("1");
        selenium.click(button);
        selenium.waitForPageToLoad("3000");
        verifyCellValueOfCashFlow(3,2,"-1");
        verifyCellValueOfCashFlow(3,3,"2");
        verifyCellValueOfCashFlow(4,2,"200");
        verifyCellValueOfCashFlow(4,3,"0");
    }

    private void setInstallmentDate(String installment, String date) {
        selenium.type("installments["+installment+"]", date);
    }

    private void setFirstAndSecondInstallmentTotal(String total) {
        selenium.type("installmentAmounts[0]", total);
        selenium.type("installmentAmounts[1]", total);
    }

    private void verifyWarningThresholdMessageOnReviewSchedulePage(String button, double warningThreshold) {
        setInstallmentDate("0","19-Oct-2010");
        setInstallmentDate("1","26-Oct-2010");
        setInstallmentDate("2","02-Nov-2010");
        setFirstAndSecondInstallmentTotal("336.0");
        selenium.click(button);
        selenium.waitForPageToLoad("3000");
//        Assert.Assert.assertTrue(selenium.isTextPresent("Installment amount for September 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%"));
        isTextPresentInPage("Installment amount for October 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
        isTextPresentInPage("Installment amount for November 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
    }

    private void verifyErrorMessageOnDatesOutOfCashFlow(String button) {
        setInstallmentDate("0","02-Aug-2010");
        setInstallmentDate("1","26-Oct-2010");
        setInstallmentDate("2","02-Jan-2011");
        setFirstAndSecondInstallmentTotal("336.0");
        selenium.click(button);
        selenium.waitForPageToLoad("3000");
        isTextPresentInPage("Cash flow is not available for August 2010. Due date should be entered for a month for which cash flow is available");
        isTextPresentInPage("Cash flow is not available for January 2011. Due date should be entered for a month for which cash flow is available");
    }

    public void verifyRecalculationWhenDateAndTotalChange() {
        setInstallmentTotal(1,"200");
        setInstallmentTotal(2,"150");
        setInstallmentTotal(3,"250");
        setInstallmentDate("0","18-Oct-2010");
        setInstallmentDate("1","27-Oct-2010");
        setInstallmentDate("2","04-Nov-2010");
        setInstallmentDate("3","10-Nov-2010");
        clickValidateAndWaitForPageToLoad();
        // principal
        verifyCellValueOfInstallments(1,3, "248.2");
        verifyCellValueOfInstallments(2,3, "249.1");
        verifyCellValueOfInstallments(3,3, "250.1");
        verifyCellValueOfInstallments(4,3, "252.6");
        // interest values
        verifyCellValueOfInstallments(1,4, "3.8");
        verifyCellValueOfInstallments(2,4, "2.9");
        verifyCellValueOfInstallments(3,4, "1.9");
        verifyCellValueOfInstallments(4,4, "0.9");
    }

    private void verifyCellValueOfInstallments(int row, int column, String value) {
        String result = selenium.getText("//table[@id='installments']//tbody//tr[" + (row ) + "]/td[" + column + "]");
        Assert.assertEquals(result, value, "expected was: " + result + " but was: " + value);
    }

    private void setInstallmentTotal(int installment, String total) {
        selenium.type("installmentAmounts["+ (installment-1) +"]",total);
    }

    public CreateLoanAccountReviewInstallmentPage verifyLoanScheduleForDecliningPrincipal() {
        verifyCellValueOfInstallments(1,3,"332.2");
        verifyCellValueOfInstallments(2,3,"333.4");
        verifyCellValueOfInstallments(3,3,"334.4");
        verifyCellValueOfInstallments(1,4,"3.8");
        verifyCellValueOfInstallments(2,4,"2.6");
        verifyCellValueOfInstallments(3,4,"1.3");
        verifyCellValueOfInstallments(1,6,"336.0");
        verifyCellValueOfInstallments(2,6,"336.0");
        verifyCellValueOfInstallments(3,6,"335.7");
        return this;
    }

    public CreateLoanAccountPreviewPage clickPreviewAndGoToReviewLoanAccountPage() {
        selenium.click(previewButton);
        selenium.waitForPageToLoad("3000");
        return new CreateLoanAccountPreviewPage(selenium);
    }

    public void verifySchedulePersistOnEdit(int noOfInstallments, int minGap, int minInstalmentAmount, DateTime disbursalDate, int maxGap) {
        verifyAllTotalFields(noOfInstallments,String.valueOf(minInstalmentAmount));
        verifyAllDatesFields(disbursalDate,maxGap,noOfInstallments, false);
        verifyCellValueOfInstallments(1,3,"94.5");
        verifyCellValueOfInstallments(2,3,"95.1");
        verifyCellValueOfInstallments(3,3,"95.6");
        verifyCellValueOfInstallments(4,3,"96.5");
        verifyCellValueOfInstallments(5,3,"618.2");
        verifyCellValueOfInstallments(1,4,"5.5");
        verifyCellValueOfInstallments(2,4,"4.9");
        verifyCellValueOfInstallments(3,4,"4.4");
        verifyCellValueOfInstallments(4,4,"3.5");
        verifyCellValueOfInstallments(5,4,"3.3");
    }

    private void verifyAllTotalFields(int noOfInstallments, String installmentAmount) {
        for (int installment = 0; installment < noOfInstallments-1; installment++) {
            Assert.assertEquals(selenium.getValue("installmentAmounts[" + installment + "]"), installmentAmount);
        }
    }

    public CreateLoanAccountReviewInstallmentPage verifyRecalculationOfCashFlowOnValidate() {
        verifyRecalculation(validateButton);
        verifyRecalculationForForSameMonth(validateButton);
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyWarningThresholdMessageOnValidate(double warningThreshold) {
        verifyWarningThresholdMessageOnReviewSchedulePage(validateButton,warningThreshold);
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyRecalculationOfCashFlowOnPreview() {
        verifyRecalculation(previewButton);
        verifyRecalculationForForSameMonth(previewButton);
        return this;

    }

    public CreateLoanAccountReviewInstallmentPage verifyWarningThresholdMessageOnPreview(double warningThreshold) {
        verifyWarningThresholdMessageOnReviewSchedulePage(previewButton,warningThreshold);
        this.verifyPage("CreateLoanPreview");
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyInstallmentDatesOutOfCashFlowCapturedOnValidate() {
        verifyErrorMessageOnDatesOutOfCashFlow(validateButton);
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyInstallmentDatesOutOfCashFlowCapturedOnPreview() {
        verifyErrorMessageOnDatesOutOfCashFlow(previewButton);
        return this;
    }

    public CreateLoanAccountPreviewPage verifyRepaymentCapacityOnPreview(String expectedRc, String minRc) {
        CreateLoanAccountPreviewPage previewPage = clickPreviewAndNavigateToPreviewPage();
        //isTextPresentInPage("Repayment Capacity of the client is " + expectedRc + " % which should be greater than the required value of " + minRc + " %");
        return previewPage;
    }

    public CreateLoanAccountReviewInstallmentPage clickPreview() {
        selenium.click(previewButton);
        waitForPageToLoad();
        return this;
    }

    public CreateLoanAccountPreviewPage clickPreviewAndNavigateToPreviewPage() {
        selenium.click(previewButton);
        waitForPageToLoad();
        return new CreateLoanAccountPreviewPage(this.selenium);
    }

    public CreateLoanAccountReviewInstallmentPage verifyRepaymentCapacityOnValidate(String expectedRc, String minRc) {
        clickValidateAndWaitForPageToLoad();
        verifyPage("SchedulePreview");
        isTextPresentInPage("Repayment Capacity of the client is " + expectedRc + " % which should be greater than the required value of " + minRc + " %");
        return this;
    }

    public void verifyLoanAmount(String amount) {

        try {
            Assert.assertEquals(getLoanAmount(), amount);
        } catch (AssertionError assertionError) {
            Assert.assertEquals(getLoanAmount() + ".0", amount);
        }
    }

    public String getLoanAmount() {
        String loanAmount = "";
        try {
            loanAmount = selenium.getText("//span[@id='schedulepreview.text.loanamount']");
        } catch (SeleniumException e) {
            Assert.fail("cant find span with id: schedulepreview.text.loanamount on page" + selenium.getLocation());
        }
        return loanAmount;
    }

    public ClientsAndAccountsHomepage cancel(){
        selenium.click("schedulePreview.button.cancel");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public CreateLoanAccountCashFlowPage editCashFlow() {
        selenium.click("_eventId_editCashflow");
        waitForPageToLoad();
        return new CreateLoanAccountCashFlowPage(selenium);
    }

    public void verifyDueDate(int installement, String dueDate) {
        Assert.assertEquals(selenium.getText("//table[@id='installments']//tbody//tr[" + (installement ) + "]/td[2]"), dueDate);
    }

    private Integer getMonthFromCalendar(){
    	return monthsMap.get(selenium.getText("//div[@id='ui-datepicker-div']/div/div/span[1]"));
    }

    private Integer getYearFromCalendar(){
    	return Integer.parseInt(selenium.getText("//div[@id='ui-datepicker-div']/div/div/span[2]"));
    }

    private void clickNextMonth(){
    	selenium.click("//div[@id='ui-datepicker-div']/div/a[2]/span");
    }

    private void clickPrevMonth(){
    	selenium.click("//div[@id='ui-datepicker-div']/div/a[1]/span");
    }

    private void chooseDay(Integer day){
    	selenium.click("link=" + day);
    }

    public String getDueDateForInstallment(Integer installment){
    	return selenium.getValue("installment.dueDate." + (installment-1));
    }

    public String getTotalForInstallment(Integer installment){
    	return selenium.getValue("name=installmentAmounts[" + (installment-1) + "]");
    }

    public void setTotalForInstallment(Integer installment, String total){
    	selenium.type("name=installmentAmounts[" + (installment-1) + "]", total);
    }

    public void setDueDateForInstallment(Integer installment, String dueDate){
    	selenium.type("installment.dueDate." + (installment-1), dueDate);
    }

    public String getFeesForInstallment(Integer installment){
    	return selenium.getValue("//table[@id='installments']/tbody/tr[" + installment + "]/td[5]");
    }

    public String getInterestForInstallment(Integer installment){
    	return selenium.getValue("//table[@id='installments']/tbody/tr[" + installment + "]/td[4]");
    }

    public void typeInstallmentDueDateByPicker(Integer installment, Calendar dueDate){
    	selenium.click("//table[@id='installments']/tbody/tr[" + installment + "]/td[2]/img");
    	Calendar calendar =Calendar.getInstance();
    	calendar.set(getYearFromCalendar(), getMonthFromCalendar(), dueDate.get(Calendar.DATE));
    	Integer months = 0;
    	if(calendar.before(dueDate)){
    		if(calendar.get(Calendar.YEAR)==dueDate.get(Calendar.YEAR)){
    			months = dueDate.get(Calendar.MONTH)-calendar.get(Calendar.MONTH);
    		}else{
    			months = dueDate.get(Calendar.YEAR)-calendar.get(Calendar.YEAR)*12+(dueDate.get(Calendar.MONTH)+1)+calendar.get(Calendar.MONTH);
    		}
    		for(int i=0; i<months; i++){
    			clickNextMonth();
    		}
    	}else if(calendar.after(dueDate)){
    		if(calendar.get(Calendar.YEAR)==dueDate.get(Calendar.YEAR)){
    			months = calendar.get(Calendar.MONTH)-dueDate.get(Calendar.MONTH);
    		}else{
    			months = calendar.get(Calendar.YEAR)-dueDate.get(Calendar.YEAR)*12+(calendar.get(Calendar.MONTH)+1)+dueDate.get(Calendar.MONTH);
    		}
    		for(int i=0; i<months; i++){
    			clickPrevMonth();
    		}
    	}
    	chooseDay(dueDate.get(Calendar.DATE));
    	Assert.assertEquals(getDueDateForInstallment(installment), format.format(dueDate.getTime()));
    }

    public void isDueDatesEditable(Integer noOfIntallments){
    	for(int i=0;i<noOfIntallments;i++){
    		Assert.assertTrue(selenium.isEditable("installment.dueDate."+i));
    	}
    }

    public void isTotalsEditable(Integer noOfIntallments){
    	for(int i=0;i<(noOfIntallments-1);i++){
    		Assert.assertTrue(selenium.isEditable("name=installmentAmounts["+i+"]"));
    	}
    	Assert.assertFalse(selenium.isEditable("name=installmentAmounts["+(noOfIntallments-1)+"]"));
    }

    public List<String> getDueDatesInstallments(Integer noOfIntallments){
    	List<String> dueDates = new ArrayList<String>();
    	for(int i=1;i<=noOfIntallments;i++){
    		dueDates.add(getDueDateForInstallment(i));
    	}
    	return dueDates;
    }

    public List<String> getTotalsInstallments(Integer noOfIntallments){
    	List<String> totals = new ArrayList<String>();
    	for(int i=1;i<=noOfIntallments;i++){
    		totals.add(getTotalForInstallment(i));
    	}
    	return totals;
    }
}