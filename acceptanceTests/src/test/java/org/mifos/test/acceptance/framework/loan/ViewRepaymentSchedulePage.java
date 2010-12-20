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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewRepaymentSchedulePage extends AbstractPage {

    String scheduleTable = "//td[@class='drawtablerow']/parent::tr/parent::tbody/parent::table";
    String scheduleDate = "scheduleViewDate";
    String viewScheduleButton = "viewScheduleButton";

    public ViewRepaymentSchedulePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("LoanRepayment");
    }


    public void verifyInstallmentAmount(int row, int column, String amount) {
        Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), amount);
    }

    public void verifyFirstInstallmentDate(int row, int column, String date) {
        Assert.assertEquals(selenium.getText("//tr[" + row + "]/td[" + column + "]"), date);
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

        return this;

    }

    private String formatDate(DateTime systemDateTime) {
        return DateTimeFormat.forPattern("dd/MM/yyyy").print(systemDateTime);
    }

    private void verifyOnDayBetweenSecondAndThirdInstallment() {
        String[][] tableOnDayBetweenSecondAndThirdInstallment =
                {{"Installments due", "", "", "", "", ""},
                {"1", "19-Oct-2010", "-", "332.2", "3.8", "0.0", "336.0"},
                {"2", "26-Oct-2010", "-", "333.4", "4.2", "0.0", "337.6"},
                {"Future Installments", "", "", "", "", ""},
                {"3", "02-Nov-2010", "-", "334.4", "1.7", "0.0", "336.1"}};
        verifyScheduleTable(tableOnDayBetweenSecondAndThirdInstallment);
    }

    private void verifyOnDayOfSecondInstallment() {
        String[][] tableOnSecondInstallment =
                {{"Installments due", "", "", "", "", ""},
                {"1", "19-Oct-2010", "-", "332.2", "3.8", "0.0", "336.0"},
                {"2", "26-Oct-2010", "-", "333.4", "3.9", "0.0", "337.3"},
                {"Future Installments", "", "", "", "", ""},
                {"3", "02-Nov-2010", "-", "334.4", "1.3", "0.0", "335.7"}};
        verifyScheduleTable(tableOnSecondInstallment);
    }

    private void verifyOnDayBetweenFirstAndSecondInstallment() {
        String[][] tableOnBetweenFirstAndSecondInstallment =
                {{"Installments due", "", "", "", "", ""},
                {"1", "19-Oct-2010", "-", "332.2", "3.8", "0.0", "336.0"},
                {"Future Installments", "", "", "", "", ""},
                {"2", "26-Oct-2010", "-", "333.4", "3.5", "0.0", "336.9"},
                {"3", "02-Nov-2010", "-", "334.4", "1.3", "0.0", "335.7"}};
        verifyScheduleTable(tableOnBetweenFirstAndSecondInstallment);
    }

    private void verifyOnFirstInstallmentDate() {
        String[][] tableOnFirstInstalment =
                {{"Installments due", "", "", "", "", ""},
                {"1", "19-Oct-2010", "-", "332.2", "3.8", "0.0", "336.0"},
                {"Future Installments", "", "", "", "", ""},
                {"2", "26-Oct-2010", "-", "333.4", "2.6", "0.0", "336.0"},
                {"3", "02-Nov-2010", "-", "334.4", "1.3", "0.0", "335.7"}};
        verifyScheduleTable(tableOnFirstInstalment);
    }

    private void verifyScheduleTable(String[][] tableOnFirstInstalment) {
        for (int rowIndex = 0; rowIndex < tableOnFirstInstalment.length; rowIndex++) {
            String[] rowValues = tableOnFirstInstalment[rowIndex];
            int row = rowIndex + 3;
            for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
                String cellValue = rowValues[columnIndex];
                int column = columnIndex + 1;
                if (!"".equals(cellValue)) {
                    String actualCellValue = selenium.getText(scheduleTable + "//tr[" + row + "]/td[" + column + "]");
                    Assert.assertEquals(actualCellValue, cellValue, "In Schedule Table for row " + row + " and column " + column + " expected value is " + cellValue + " but the actual value is " + actualCellValue);
                }
            }
        }
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
}
