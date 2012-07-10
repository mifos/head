package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;
/**
 * Start page of importing loan accounts data.
 * @author lgadomski
 */
public class ImportLoansPage extends MifosPage{
    public ImportLoansPage(Selenium selenium){
        super(selenium);
    }
    /**
     * Verify page
     */
    public void verifyPage(){
        verifyPage("ImportLoansSelectFile");
    }
    /**
     * Submit file with accounts data and go to review step.
     * @param importFile xls spreadsheet with data
     * @return review page
     */
    public ImportLoansReviewPage submitToReview(String importFile) {
        selenium.type("importLoans.file", importFile);
        selenium.click("importLoans.review");
        waitForPageToLoad();
        return new ImportLoansReviewPage(selenium);
    }
}
