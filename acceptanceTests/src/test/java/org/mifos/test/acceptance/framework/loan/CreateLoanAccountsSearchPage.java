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

import org.junit.Assert;
import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class CreateLoanAccountsSearchPage extends AbstractPage {
    private String selectedBranch = "";
    private String selectedOfficer = "";

    public CreateLoanAccountsSearchPage(Selenium selenium) {
        super(selenium);
        verifyPage("CreateMultipleLoanAccounts");
    }

    public void verifyPage() {
        this.verifyPage("CreateMultipleLoanAccounts");
    }

    public CreateLoanAccountsEntryPage searchAndNavigateToCreateMultipleLoanAccountsEntryPage(CreateMultipleLoanAccountSelectParameters formParameters) {
        verifyInformationMessage("Enter details and click Search.");
        
        selectBranch(formParameters.getBranch());
        selectCenter(formParameters.getCenter());
        selenium.select("id=createMultipleLoanAccounts.select.loanProduct", "label="+ formParameters.getLoanProduct());
        selenium.click ("id=createMultipleLoanAccounts.button.submit");
        waitForPageToLoad();
        
        verifyInformationMessage("Enter details and click Submit.");

        return new CreateLoanAccountsEntryPage(selenium);
    }

    public void selectBranch(String branch) {
        selenium.select("id=createMultipleLoanAccounts.select.branchOffice", "label=" + branch);
        if(!selectedBranch.equals(branch)) {
            selectedBranch = branch;
            waitForPageToLoad();
        }
    }

    public void selectOfficer(String officer) {
        selenium.select("id=createMultipleLoanAccounts.select.loanOfficer", "label="+ officer);
        if(!selectedOfficer.equals(officer)) {
            selectedOfficer = officer;
            waitForPageToLoad();
        }
    }

    public void selectCenter(String center) {
        selenium.select("id=createMultipleLoanAccounts.select.center", "label="+ center);
        waitForPageToLoad();
    }

    public void selectBranchOfficerAndCenter(String branch, String officer, String center) {
        selectBranch(branch);
        if(selenium.getSelectedLabel("loanOfficerId").equalsIgnoreCase("--Select--")) {
        selectOfficer(officer);
        }
        if(selenium.getSelectedLabel("centerId").equalsIgnoreCase("--Select--")) {
        selectCenter(center);
        }
    }

    public void verifyBranchNotInSelectOptions(String branch) {
        String[] branches = selenium.getSelectOptions("id=createMultipleLoanAccounts.select.branchOffice");
        Assert.assertTrue(checkNotInOptions(branches, branch));
    }

    public void verifyOfficerNotInSelectOptions(String branch, String officer) {
        selectBranch(branch);
        String[] officers = selenium.getSelectOptions("id=createMultipleLoanAccounts.select.loanOfficer");
        Assert.assertTrue(checkNotInOptions(officers, officer));
    }

    public void verifyCenterIsNotInSelectOptions(String branch, String center) {
        selectBranch(branch);
//        selectOfficer(officer);
        String[] centers = selenium.getSelectOptions("id=createMultipleLoanAccounts.select.center");
        Assert.assertTrue(checkNotInOptions(centers, center));
    }
    
    private void verifyInformationMessage(String startsWith) {
        boolean expected = true;
        boolean actual = selenium.getText("//span[@class=\"fontnormal\"][1]").startsWith(startsWith);
        
        Assert.assertEquals(expected, actual);
    }

    private boolean checkNotInOptions(String[] options, String value) {
        boolean result = true;
        for(String option : options) {
            if(option.equals(value)) {
                result = false;
                break;
            }
        }
        return result;
    }
}
