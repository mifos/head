package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
/**
 * Represents summary page of importing loan accounts data
 * @author lgadomski
 */
public class ImportLoansSaveSummaryPage extends MifosPage{

    public ImportLoansSaveSummaryPage(Selenium selenium) {
        super(selenium);
    }
    /**
     * Verify page
     */
    public void verifyPage() {
        verifyPage("ImportLoansSaved");
    }
    /**
     * Check if number of successfully parsed rows is equal to expected.
     * @param succesNumber expected number of rows
     */
    public void verifySuccesString(String succesNumber) {
        Assert.assertTrue(selenium.isTextPresent("Loans successfully saved: "+succesNumber+";"));
    }
    /**
     * Check if number of rows with errors is equal to expected.
     * @param errorNumber expected number of rows
     */
    public void verifyErrorStroing(String errorNumber) {
        Assert.assertTrue(selenium.isTextPresent("Ignored rows: "+errorNumber+";"));
    }

}
