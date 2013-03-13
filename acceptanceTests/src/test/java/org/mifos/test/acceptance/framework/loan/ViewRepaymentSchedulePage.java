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

import java.util.List;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewRepaymentSchedulePage extends AbstractPage {

    public static final int FIRST_ROW = 4;

    public static final int NUMBER_COLUMN = 1;
    public static final int DUE_DATE_COLUMN = 2;
    public static final int DATE_PAID_COLUMN = 3;
    public static final int PRINCIPAL_COLUMN = 4;
    public static final int INTEREST_COLUMN = 5;
    public static final int FEE_COLUMN = 6;
    public static final int PENALTY_COLUMN = 7;
    public static final int DAYS_LATE_COLUMN = 8;
    public static final int TOTAL_COLUMN = 9;

    String scheduleTable = "//td[@class='drawtablerow']/parent::tr/parent::tbody/parent::table";
    String scheduleDate = "scheduleViewDate";
    String viewScheduleButton = "viewScheduleButton";

    public ViewRepaymentSchedulePage(Selenium selenium) {
        super(selenium);
        this.verifyPage("LoanRepayment");
    }

    public void verifyInstallmentAmount(int row, int column, String amount) {
    	Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), amount);
    }

    public void verifyFirstInstallmentDate(int row, int column, String date) {
    	Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), date);
    }

    public void verifyRepaymentScheduleTableRow(int row, int column, String value) {
    	Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + column), value);
    }
    
    public void verifyRunningBalanceTableRow(int row, int column, String value) {
    	Assert.assertEquals(selenium.getTable("runningBalanceTable." + row + "." + column), value);
    }

    private String getCellOfScheduleTable(int row, int column) {
        return selenium.getTable("installments." + row + "." + column);
    }

    private String getNoOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, NUMBER_COLUMN-1);
    }

    private String getDueDateOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, DUE_DATE_COLUMN-1);
    }
    
    private String getPrincipalOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, PRINCIPAL_COLUMN-1);
    }
    
    private String getTotalOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, TOTAL_COLUMN-1);
    }
    
    private void verifyTotalOfInstallmentFromSchedule(int row, String amount) {
    	Assert.assertEquals(getTotalOfInstallmentFromSchedule(row), amount);
    }
    
    private void verifyDateOfInstallmentFromSchedule(int row, String date) {
    	Assert.assertEquals(getDueDateOfInstallmentFromSchedule(row), date);
    }

    public LoanAccountPage navigateToLoanAccountPage() {
        selenium.click("id=loanRepayment.button.return");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public ViewRepaymentSchedulePage verifyScheduleTable(String[][] tableOnFirstInstalment) {
        for (int rowIndex = 0; rowIndex < tableOnFirstInstalment.length; rowIndex++) {
            String[] rowValues = tableOnFirstInstalment[rowIndex];
            int row = rowIndex + 3;
            for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
                String cellValue = rowValues[columnIndex];
                int column = columnIndex + 1;
                if (!"".equals(cellValue)) {
                    String actualCellValue = selenium.getText(scheduleTable + "//tr[" + row + "]/td[" + column + "]");
                    Assert.assertEquals(actualCellValue, cellValue, "In Schedule Table for row " + row + " and column "
                            + column + " expected value is " + cellValue + " but the actual value is "
                            + actualCellValue);
                }
            }
        }
        return this;
    }

    private void setScheduleDate(String date) {
        selenium.type(scheduleDate, date);
        selenium.click(viewScheduleButton);
        waitForPageToLoad();
    }

    public ViewRepaymentSchedulePage verifyScheduleDateField() {
        setScheduleDate("19/10/2010");
        return this;
    }

    public ViewOriginalSchedulePage navigateToViewOriginalSchedulePage() {
        selenium.click("loanRepayment.link.original_schedule");
        waitForPageToLoad();
        return new ViewOriginalSchedulePage(selenium);
    }
    
    public ApplyPaymentPage navigateToApplyPaymentPage() {
        selenium.click("loanRepayment.link.applyPayment");
        waitForPageToLoad();
        
        return new ApplyPaymentPage(selenium);
    }

    public ApplyAdjustmentPage navigateToApplyAdjustment() {
        selenium.click("loanRepayment.link.applyAdjustment");
        waitForPageToLoad();
        
        ListAdjustmentsPage listAdjustmentsPage = new ListAdjustmentsPage(selenium);       
        return listAdjustmentsPage.navigateToFirstAdjustment();
    }

    public void verifyScheduleNotContainDate(String date) {
        int rowCount = selenium.getXpathCount("//table[@id='installments']/tbody/tr").intValue();
        boolean resault = true;
        for (int i = 1; i < rowCount; i++) {
            if (getNoOfInstallmentFromSchedule(i).matches("^[0-9]+$")
                    && getDueDateOfInstallmentFromSchedule(i).equals(date)) {
                resault = false;
                break;
            }
        }
        Assert.assertTrue(resault);
    }

    public void verifySchedulePrincipalWithGrace(Integer gracePeriodDuration) {
        int rowCount = selenium.getXpathCount("//table[@id='installments']/tbody/tr").intValue();
        Integer zeroPrincipalCount = 0;
        for (int i = 1; i < rowCount; i++) {
            if (getNoOfInstallmentFromSchedule(i).matches("^[0-9]+$")){
                    if(getPrincipalOfInstallmentFromSchedule(i).equals("0")) {
                        zeroPrincipalCount++;
                    }
                    else{
                        break;
                    }
            }
        }
        Assert.assertEquals(gracePeriodDuration, zeroPrincipalCount);
    }
    
    public void verifyScheduleAndAmounts(List<String> totals, List<String> dueDates){
        int rowCount = selenium.getXpathCount("//table[@id='installments']/tbody/tr").intValue();
        int j=0;
        for (int i = 1; i < rowCount; i++) {
            if (getNoOfInstallmentFromSchedule(i).matches("^[0-9]+$")){
            	verifyTotalOfInstallmentFromSchedule(i, totals.get(j));
            	verifyDateOfInstallmentFromSchedule(i, dueDates.get(j));
            	j++;
            }
        }
    }

    public void verifyRepaymentScheduleTableRow(int row, String... values) {
    	for (int i=0; i<values.length; i+=1) {
    	    Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + i), values[i]);
    	}
    }

    public void verifyRepaymentScheduleTableNumber(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (NUMBER_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableDueDate(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (DUE_DATE_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableDatePaid(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (DATE_PAID_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTablePrincipal(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (PRINCIPAL_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableInterest(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (INTEREST_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableFees(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (FEE_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTablePenalties(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (PENALTY_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableDaysLate(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (DAYS_LATE_COLUMN-1)), value);
    }
    
    public void verifyRepaymentScheduleTableTotal(int row, String value) {
    	Assert.assertEquals(selenium.getTable("installments." + row + "." + (TOTAL_COLUMN-1)), value);
    }

}
