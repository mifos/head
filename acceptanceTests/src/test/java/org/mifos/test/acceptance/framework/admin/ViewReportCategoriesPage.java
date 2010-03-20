package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Represents the View Report Categories page
 *
 */
public class ViewReportCategoriesPage extends MifosPage {

    private static final String REPORT_CATEGORIES_TABLE_ID = "viewReportsCategory.table.categoryNames";
    // Category names start on the 3rd row of the Category Names table
    private static final int CATEGORY_NAMES_START_ROW = 2;
    public ViewReportCategoriesPage(Selenium selenium) {
        super(selenium);
    }

    public ViewReportCategoriesPage verifyPage() {
        super.verifyPage("ViewReportsCategory");
        return this;
    }

    public void verifyReportCategoriesExist(String[] expectedData) {
        int start = CATEGORY_NAMES_START_ROW;
        for (String expectedCellData : expectedData) {
            String actualCellData = selenium.getTable(REPORT_CATEGORIES_TABLE_ID + "." + (start++) + ".0");
            Assert.assertEquals(actualCellData, expectedCellData);
        }

    }



}
