package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
/**
 * Represents a review page of importing loans data
 * @author lgadomski
 *
 */
public class ImportLoansReviewPage extends MifosPage{
    public ImportLoansReviewPage(Selenium selenium){
        super(selenium);
    }
    /**
     * Verify page
     */
    public void verifyPage() {
        verifyPage("ImportLoansReview");
    }
    /**
     * Check if errors from parses are the same as expected
     * @param arrayOfErrors array of expected errors
     */
    public void validateErrors(String[] arrayOfErrors) {
        for (String string : arrayOfErrors) {
            Assert.assertTrue(selenium.isTextPresent(string));
        }
    }
    /**
     * Check if there are successfully parsed rows
     * @param numberOfGoodRows expected number of rows
     */
    public void validateSuccesText(String numberOfGoodRows) {
        Assert.assertTrue(selenium.isTextPresent("Import Status: "+numberOfGoodRows+" rows parsed successfully"));
    }
    /**
     * Save successfully parsed rows to database.
     * @return
     */
    public ImportLoansSaveSummaryPage saveSuccessfullRows(){
        selenium.click("importLoans.submit");
        waitForPageToLoad();
        ImportLoansSaveSummaryPage summaryPage= new ImportLoansSaveSummaryPage(selenium);
        summaryPage.verifyPage();
        return summaryPage;
    }

}
