/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.test.acceptance.properties;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","acceptance","ui", "properties"})
public class UpdateCustomPropertiesTest extends UiTestCaseBase {    
    NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;
    
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        super.setUp();
    }
    
    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    public void changeLocale() {
        // update the language to French
        propertiesHelper.setLocale("FR", "FR");
        
        // make sure that the welcome text does not contain "Welcome"
        HomePage homePage = navigationHelper.navigateToHomePage();
        String welcomeText = homePage.getWelcome();
        Assert.assertFalse(welcomeText.contains("Welcome"), "The welcome text contained \"Welcome\" even though the language is supposed to have changed!");
        
        propertiesHelper.setLocale("EN", "GB");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changeDecimalsToThree() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        propertiesHelper.setDigitsAfterDecimal(3);
        
        LoanAccountPage loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyLoanAmount("1050.000");
        
        propertiesHelper.setDigitsAfterDecimal(1);
        
    }
}
