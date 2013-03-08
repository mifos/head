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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewRepaymentSchedulePage extends AbstractPage {

    public static final int FIRST_ROW = 4;

    public static final int PRINCIPAL_COLUMM = 4;
    public static final int INTEREST_COLUMN = 5;
    public static final int FEE_COLUMN = 6;
    public static final int PENALTY_COLUMN = 7;
    public static final int COLUMN_DAYS_LATE = 8;

    String scheduleTable = "//td[@class='drawtablerow']/parent::tr/parent::tbody/parent::table";
    String scheduleDate = "scheduleViewDate";
    String viewScheduleButton = "viewScheduleButton";

    public ViewRepaymentSchedulePage(Selenium selenium) {
        super(selenium);
        this.verifyPage("LoanRepayment");
    }

    public void verifyInstallmentAmount(int row, int column, String amount) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), amount);
    }

    public void verifyFirstInstallmentDate(int row, int column, String date) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), date);
    }

    public void verifyRepaymentScheduleTableRow(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + column), value);
    }
    
    public void verifyRunningBalanceTableRow(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("runningBalanceTable." + row + "." + column), value);
    }

    private String getCellOfScheduleTable(int row, int column) {
        return selenium.getTable("installments." + row + "." + column);
    }

    private String getNoOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, 0);
    }

    private String getDateOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, 1);
    }

    private String getPrincipalOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, 3);
    }
    
    private String getTotalOfInstallmentFromSchedule(int row) {
        return getCellOfScheduleTable(row, 8);
    }
    
    private void verifyTotalOfInstallmentFromSchedule(int row, String amount) {
    	Assert.assertEquals(getTotalOfInstallmentFromSchedule(row), amount);
    }
    
    private void verifyDateOfInstallmentFromSchedule(int row, String date) {
    	Assert.assertEquals(getDateOfInstallmentFromSchedule(row), date);
    }

    public LoanAccountPage navigateToLoanAccountPage() {
        selenium.click("id=loanRepayment.button.return");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public ViewRepaymentSchedulePage verifyScheduleForDecliningPrincipal(DateTime systemDateTime) {

        Assert.assertTrue(selenium.getValue(scheduleDate).equals(formatDate(systemDateTime)));
        String initialTable = selenium.getText(scheduleTable);
        setScheduleDate("12/10/2010");
        Assert.assertEquals(selenium.getText(scheduleTable), initialTable);

        setScheduleDate("19/10/2010");
        verifyOnFirstInstallmentDate();

        setScheduleDate("24/10/2010");
        verifyOnDayBetweenFirstAndSecondInstallment();

        setScheduleDate("26/10/2010");
        verifyOnDayOfSecondInstallment();

        setScheduleDate("28/10/2010");
        verifyOnDayBetweenSecondAndThirdInstallment();

        setScheduleDate("04/11/2010");
        verifyAfterLastInstallment();

        return this;

    }

    private void verifyAfterLastInstallment() {
        String[][] tableAfterLastInstallment = { { "Installments due", "", "", "", "", "" },
                { "1", "19-Oct-2010", "-", "332.2", "3.8", "0", "336" },
                { "2", "26-Oct-2010", "-", "333.4", "5.5", "0", "338.9" },
                { "3", "02-Nov-2010", "-", "334.4", "3.3", "0", "337.7" } };
        verifyScheduleTable(tableAfterLastInstallment);

    }

    private String formatDate(DateTime systemDateTime) {
        return DateTimeFormat.forPattern("dd/MM/yyyy").print(systemDateTime);
    }

    private void verifyOnDayBetweenSecondAndThirdInstallment() {
        String[][] tableOnDayBetweenSecondAndThirdInstallment = { { "Installments due", "", "", "", "", "" },
                { "1", "19-Oct-2010", "-", "332.2", "3.8", "0", "336" },
                { "2", "26-Oct-2010", "-", "333.4", "4.2", "0.", "337.6" },
                { "Future Installments", "", "", "", "", "" },
                { "3", "02-Nov-2010", "-", "334.4", "1.7", "0", "336.1" } };
        verifyScheduleTable(tableOnDayBetweenSecondAndThirdInstallment);
    }

    private void verifyOnDayOfSecondInstallment() {
        String[][] tableOnSecondInstallment = { { "Installments due", "", "", "", "", "" },
                { "1", "19-Oct-2010", "-", "332.2", "3.8", "0", "336" },
                { "2", "26-Oct-2010", "-", "333.4", "3.9", "0", "337.3" },
                { "Future Installments", "", "", "", "", "" },
                { "3", "02-Nov-2010", "-", "334.4", "1.3", "0", "335.7" } };
        verifyScheduleTable(tableOnSecondInstallment);
    }

    private void verifyOnDayBetweenFirstAndSecondInstallment() {
        String[][] tableOnBetweenFirstAndSecondInstallment = { { "Installments due", "", "", "", "", "" },
                { "1", "19-Oct-2010", "-", "332.2", "3.8", "0", "336" },
                { "Future Installments", "", "", "", "", "" },
                { "2", "26-Oct-2010", "-", "333.4", "3.5", "0", "336.9" },
                { "3", "02-Nov-2010", "-", "334.4", "1.3", "0", "335.7" } };
        verifyScheduleTable(tableOnBetweenFirstAndSecondInstallment);
    }

    private void verifyOnFirstInstallmentDate() {
        String[][] tableOnFirstInstalment = { { "Installments due", "", "", "", "", "" },
                { "1", "19-Oct-2010", "-", "332.2", "3.8", "0", "336" },
                { "Future Installments", "", "", "", "", "" },
                { "2", "26-Oct-2010", "-", "333.4", "2.6", "0", "336" },
                { "3", "02-Nov-2010", "-", "334.4", "1.3", "0", "335.7" } };
        verifyScheduleTable(tableOnFirstInstalment);
    }

    public ViewRepaymentSchedulePage verifyScheduleTable(String[][] tableOnFirstInstalment) {
        for (int rowIndex = 0; rowIndex < tableOnFirstInstalment.length; rowIndex++) {
            String[] rowValues = tableOnFirstInstalment[rowIndex];
            int row = rowIndex + 3;
            for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
                String cellValue = rowValues[columnIndex];
                int column = columnIndex + 1;
                if (column == COLUMN_DAYS_LATE) {
            		//TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
            		continue;
            	}
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
                    && getDateOfInstallmentFromSchedule(i).equals(date)) {
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
    
    public void verifyRepaymentScheduleTableDueDate(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("installments." + row + "." + column), value);
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
    		if (i == COLUMN_DAYS_LATE-1) {
        		//TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        		continue;
        	}
    	    Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + i), values[i]);
    	}
    }
    
    public void verifyRepaymentScheduleTablePrincipal(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("installments." + row + "." + column), value);
    }
       
    public void verifyRepaymentScheduleTableFees(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("installments." + row + "." + column), value);
    }
    
    public void verifyRepaymentScheduleTablePenalties(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("installments." + row + "." + column), value);
    }
    
    public void verifyRepaymentScheduleTableInterest(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("installments." + row + "." + column), value);
    }
    
    public void verifyRepaymentScheduleTableAfterPayInterest(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + column), value);
    }
    
    public void verifyRepaymentScheduleTableAfterPayPrincipal(int row, int column, String value) {
    	if (column == COLUMN_DAYS_LATE) { return; } //TODO: MIFOS-5987 Fix repayment schedule tests related to days late changes
        Assert.assertEquals(selenium.getTable("repaymentScheduleTable." + row + "." + column), value);
    }
}
