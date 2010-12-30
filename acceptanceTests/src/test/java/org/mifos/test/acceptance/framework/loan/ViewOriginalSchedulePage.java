package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

public class ViewOriginalSchedulePage extends AbstractPage {

    String originalScheduleTable = "//table[@id='originalInstallments']/tbody";


    public ViewOriginalSchedulePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("ViewOriginalSchedule");
    }

    public ViewOriginalSchedulePage verifyScheduleTable(String[][] tableOnOriginalInstallment) {
        verifyPage();
        for (int rowIndex = 0; rowIndex < tableOnOriginalInstallment.length; rowIndex++) {
            String[] rowValues = tableOnOriginalInstallment[rowIndex];
            int row = rowIndex + 1;
            for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
                String cellValue = rowValues[columnIndex];
                int column = columnIndex + 1;
                if (!"".equals(cellValue)) {
                    String actualCellValue = selenium.getText(originalScheduleTable + "//tr[" + row + "]/td[" + column + "]");
                    Assert.assertEquals(actualCellValue, cellValue, "In Schedule Table for row " + row + " and column " + column + " expected value is " + cellValue + " but the actual value is " + actualCellValue);
                }
            }
        }
        return this;
    }

    public ViewRepaymentSchedulePage returnToRepaymentSchedule() {
        selenium.click("returnToRepaymentScheduleButton");
        waitForPageToLoad();
        verifyPage("LoanRepayment");
        return new ViewRepaymentSchedulePage(selenium);
    }
}
