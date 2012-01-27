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
    
    public double[][] getInstallments(double delta) {
        int installmentCount = getInstallmentCount();
        double[][] installments = new double[installmentCount][4];
        
        for (int i = 0; i < installmentCount; ++i) {
            installments[i][0] = Double.valueOf(getCellValue(i + 2, 3).replace(",", ""));
            installments[i][1] = Double.valueOf(getCellValue(i + 2, 4).replace(",", ""));
            installments[i][2] = Double.valueOf(getCellValue(i + 2, 5).replace(",", ""));
            installments[i][3] = Double.valueOf(getCellValue(i + 2, 6).replace(",", ""));
            
            Assert.assertEquals(installments[i][0] + installments[i][1] + installments[i][2], installments[i][3], delta);
        }
        
        return installments;
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
                    String actualCellValue = getCellValue(row, column);
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
    
    public LoanAccountPage returnToLoanAccountDetail() {
        selenium.click("returnToAccountDetailsbutton");
        waitForPageToLoad();
        verifyPage("LoanAccountDetail");
        return new LoanAccountPage(selenium);
    }
    
    private int getInstallmentCount() {
        return selenium.getXpathCount(originalScheduleTable + "//tr").intValue() - 1;
    }
    
    private String getCellValue(int row, int col) {
        return selenium.getText(originalScheduleTable + "//tr[" + row + "]/td[" + col + "]");
    }
}
