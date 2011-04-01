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

package org.mifos.test.acceptance.admin;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.CustomizeTextAddPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextEditPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextViewPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"admin","acceptance","ui", "no_db_unit"})
public class CustomizeTextTest  extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;
    private NavigationHelper navigationHelper;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    private void verifyClientsAndAccountsTabIsUpdated(String customizedTextForClient, 
    		CustomizeTextViewPage customizeTextViewPage) {
    	String text = customizeTextViewPage.getClientsAndAccountsHeaderTabText();
    	Assert.assertEquals("Clients and accounts header tab should be updated", 
    			customizedTextForClient + "s & Accounts", text);    	
    }
    
    @Test
    public void addEditRemoveCustomizedText() {
    	String originalText = "Client";
    	String customText = "Person";
        String newCustomText = "有";  // try some Chinese
        
    	CustomizeTextViewPage customizeTextViewPage = 
    		adminTestHelper.addCustomizedText(originalText, customText);
    	
    	// verify that it shows up
    	customizeTextViewPage.verifyCustomTextIsPresent(originalText, customText);
    	verifyClientsAndAccountsTabIsUpdated(customText, customizeTextViewPage);
    	
    	// edit the custom text
    	CustomizeTextEditPage customizeTextEditPage = 
    		customizeTextViewPage.navigateToCustomizeTextEditPage(originalText);
    	customizeTextEditPage.setCustomText(newCustomText);
    	customizeTextViewPage = customizeTextEditPage.submit();
        	
    	// verify that it shows up
    	customizeTextViewPage.verifyCustomTextIsPresent(originalText, newCustomText);    	
    	verifyClientsAndAccountsTabIsUpdated(newCustomText, customizeTextViewPage);
    	
    	// remove the custom text
    	customizeTextViewPage.removeCustomizedText(originalText);
    	
    	// verify that custom text is gone
    	Assert.assertThat(customizeTextViewPage.getCustomizedTextCount(), is(0));    	
    }
    
    /*
     * http://mifosforge.jira.com/browse/MIFOSTEST-1172
     * Verify 'Define customized text' page
     */
    @Test
    public void verifyDefineCustomizedTextPage() {
        String originalText = "Client";
        String customText = "Person";
        
        verifyErrors();
        
        CustomizeTextViewPage customizeTextViewPage = 
            adminTestHelper.addCustomizedText(originalText, customText);
        
        customizeTextViewPage.verifyCustomTextIsPresent(originalText, customText);
        verifyEditRemove();
        
        customizeTextViewPage = navigationHelper
            .navigateToAdminPage()
            .navigateToCustomizeTextViewPage()
            .navigateToCustomizeTextAddPage()
            .cancel();
        
        customizeTextViewPage.verifyPage();
        CustomizeTextEditPage customizeTextEditPage = customizeTextViewPage.navigateToCustomizeTextEditPage("Client");
        customizeTextEditPage.verifyOriginalTextInput();
        
        customizeTextEditPage.setCustomText("");
        customizeTextEditPage.trySubmit();
        String error = "Please specify Custom Text";
        String errorMessage = "No text <"+ error +"> present on the page";
        customizeTextEditPage.verifyTextPresent(error, errorMessage);
        customizeTextViewPage = customizeTextEditPage.cancel();
        customizeTextViewPage.verifyPage();
        customizeTextViewPage.done().verifyPage();
        
        navigationHelper
            .navigateToAdminPage()
            .navigateToCustomizeTextViewPage()
            .removeCustomizedText(originalText);
    }
    
    private void verifyEditRemove() {
        CustomizeTextViewPage customizeTextViewPage = navigationHelper
        .navigateToAdminPage()
        .navigateToCustomizeTextViewPage()
        .clickEditButton();
    
        String error = "Please select an item to edit or remove";
        String errorMessage = "No text <"+ error +"> present on the page";
        
        customizeTextViewPage.verifyTextPresent(error, errorMessage);
        
        customizeTextViewPage = navigationHelper
            .navigateToAdminPage()
            .navigateToCustomizeTextViewPage()
            .clickRemoveButton();
        
        customizeTextViewPage.verifyTextPresent(error, errorMessage);
    }
    
    private void verifyErrors() {
        verifyEditRemove();
        
        CustomizeTextAddPage customizeTextAddPage = navigationHelper
            .navigateToAdminPage()
            .navigateToCustomizeTextViewPage()
            .navigateToCustomizeTextAddPage();
    
        String text = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm";
        String error = "The maximum length for Original Text is 50 characters";
        String errorMessage = "No text <"+ error +"> present on the page";
        
        customizeTextAddPage.setOriginalText(text);
        customizeTextAddPage.setCustomText(text);
        
        customizeTextAddPage = customizeTextAddPage.trySubmit();
        customizeTextAddPage.verifyTextPresent(error, errorMessage);
        
        error = "The maximum length for Custom Text is 50 characters";
        
        customizeTextAddPage.verifyTextPresent(error, errorMessage);
        
        text = "";
        error = "Please specify Custom Text";
        
        customizeTextAddPage.setOriginalText(text);
        customizeTextAddPage.setCustomText(text);
        
        customizeTextAddPage = customizeTextAddPage.trySubmit();
        customizeTextAddPage.verifyTextPresent(error, errorMessage);
        
        error = "Please specify Original Text";
        
        customizeTextAddPage.verifyTextPresent(error, errorMessage);
    }
}