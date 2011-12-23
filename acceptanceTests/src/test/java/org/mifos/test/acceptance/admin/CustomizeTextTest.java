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

import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Arrays;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextAddPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextEditPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextViewPage;
import org.mifos.test.acceptance.framework.loan.AccountChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
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
    
    /*
     * http://mifosforge.jira.com/browse/MIFOSTEST-1171
     * Verify customized text on various Mifos pages
     */
    @SuppressWarnings("PMD")
    @Test
    public void verifyCustomizedTextOnVariousPages() {
        TreeMap<String, String> originalCustomText = new TreeMap<String, String>();
        originalCustomText.put("Complete the fields below.", "Fill necessary");
        originalCustomText.put("Click Cancel to return", "Cancel button is to back");
        originalCustomText.put("Product category", "搜索工具");
        originalCustomText.put("Date", "عر");
        originalCustomText.put("fields", "information");
        originalCustomText.put("Targeted Deposits and Withdrawal Restrictions", "deposits and withdrawals");
        originalCustomText.put("details", "有");
        originalCustomText.put("Create new Center", "Define center");
        originalCustomText.put("Create multiple Loan Accounts", "下面的搜索工具");
        originalCustomText.put("Approve multiple loans", "أبجدية‎");
        originalCustomText.put("Client", "Person");
        originalCustomText.put("Home", "Main");
        originalCustomText.put("Clients & Accounts", "òèßñ 有 عربية");
        originalCustomText.put("Then click Preview", "After that click on Preview button");
        originalCustomText.put("Fields marked with an asterisk are required.", "Mandatory information are necessary");
        originalCustomText.put("Date of Birth", "搜索工具");
        originalCustomText.put("Name", "عر");
        originalCustomText.put("User", "Mifos");
        originalCustomText.put("User Title", "Personnel Role");
        originalCustomText.put("Login information", "漢字");
        originalCustomText.put("description", "info");
        originalCustomText.put("recommended Amount for Deposit", "عبية");
        originalCustomText.put("create New group", "Define Group");
        originalCustomText.put("create Loan Account", "عربة");
        originalCustomText.put("reports", "Summaries");
        originalCustomText.put("admin", "عربية");
        originalCustomText.put("first", "1th");
        originalCustomText.put("postal code", "ية عر");
           
           
        CustomizeTextViewPage customizeTextViewPage = navigationHelper
            .navigateToAdminPage()
            .navigateToCustomizeTextViewPage();
            
        String value;
        for (String key : originalCustomText.keySet()) {
            CustomizeTextAddPage customizeTextAddPage = customizeTextViewPage.navigateToCustomizeTextAddPage();
            customizeTextAddPage.setOriginalText(key);
            value=originalCustomText.get(key);
            customizeTextAddPage.setCustomText(value);
            customizeTextAddPage.submit();
            verifyCustomTextIsAdded(key, value);
        }
       
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        verifyClientsAndAccountsCreateCenterLink(originalCustomText.get("Create new Center"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsCreateMultipleLoansLink(originalCustomText.get("Create multiple Loan Accounts"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsApproveMultipleLoansLink(originalCustomText.get("Approve multiple loans"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsCreateClientLink(originalCustomText.get("Client"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsCreateNewClientsText(originalCustomText.get("Client"));
        verifyClientsAndAccountsCreateGroupLink(originalCustomText.get("create New group"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsCreateLoanAccountLink(originalCustomText.get("create Loan Account"), clientsAndAccountsHomepage);
       
        verifyClientsAndAccountsHomeTab(originalCustomText.get("Home"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsTab(originalCustomText.get("Clients & Accounts"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsAdminTab(originalCustomText.get("admin"), clientsAndAccountsHomepage);
        verifyClientsAndAccountsReportsTab(originalCustomText.get("reports"), clientsAndAccountsHomepage);
           
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.navigateToDefineSavingsProduct();
        verifyDefineNewSavingsProductText(originalCustomText.get("Complete the fields below."));
        verifyDefineNewSavingsProductText(originalCustomText.get("Click Cancel to return"));
        verifyDefineNewSavingsProductText(originalCustomText.get("Product category"));
        verifyDefineNewSavingsProductTextDate(originalCustomText.get("Date"));
        verifyDefineNewSavingsProductTextFields(originalCustomText.get("fields"));
        verifyDefineNewSavingsProductText(originalCustomText.get("Targeted Deposits and Withdrawal Restrictions"));
        verifyDefineNewSavingsProductTextDetails(originalCustomText.get("details"));
        verifyDefineNewSavingsProductTextDescription(originalCustomText.get("description"));
        verifyDefineNewSavingsProductTextDeposit(originalCustomText.get("recommended Amount for Deposit"));
        AdminPage  adminPageAgain = navigationHelper.navigateToAdminPage();
        ChooseOfficePage chooseOfficePage = adminPageAgain.navigateToCreateUserPage();
        chooseOfficePage.selectOffice("branch1");
        verifyDefineNewSystemUserText(originalCustomText.get("Then click Preview"));
        verifyDefineNewSystemUserText(originalCustomText.get("Fields marked with an asterisk are required."));
        verifyDefineNewSystemUserText(originalCustomText.get("Date of Birth"));
        verifyDefineNewSystemUserTextName(originalCustomText.get("Name"));
        verifyDefineNewSystemUserText(originalCustomText.get("User"));
        verifyDefineNewSystemUserText(originalCustomText.get("User Title"));
        verifyDefineNewSystemUserText(originalCustomText.get("Login information"));
        verifyDefineNewSystemUserTextFirst(originalCustomText.get("first"));
        verifyDefineNewSystemUserTextPostal(originalCustomText.get("postal code"));
       
        AdminPage  goToAdminPage = navigationHelper.navigateToAdminPage();
        goToAdminPage.navigateToCustomizeTextViewPage();
        for (Entry<String, String> entry: originalCustomText.entrySet()) {
            customizeTextViewPage.removeCustomizedText(entry.getKey());
        }
       
        Assert.assertThat(customizeTextViewPage.getCustomizedTextCount(), is(0));
       
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        verifyClientsAndAccountsCreateCenterLink("Create new Center", clientsAndAccountsPage);
        verifyClientsAndAccountsCreateMultipleLoansLink("Create multiple Loan Accounts", clientsAndAccountsPage);
        verifyClientsAndAccountsApproveMultipleLoansLink("Approve multiple loans", clientsAndAccountsPage);
        verifyClientsAndAccountsCreateClientLink("Client", clientsAndAccountsPage);
        verifyClientsAndAccountsCreateNewClientsText("Client");
        verifyClientsAndAccountsCreateGroupLink(originalCustomText.get("create New group"), clientsAndAccountsPage);
        verifyClientsAndAccountsCreateLoanAccountLink(originalCustomText.get("create Loan Account"), clientsAndAccountsPage);
       
        verifyClientsAndAccountsHomeTab("Home", clientsAndAccountsPage);
        verifyClientsAndAccountsTab("Clients & Accounts", clientsAndAccountsPage);
        verifyClientsAndAccountsAdminTab(originalCustomText.get("admin"), clientsAndAccountsPage);
        verifyClientsAndAccountsReportsTab(originalCustomText.get("reports"), clientsAndAccountsPage);
       
        AdminPage adminHomePage = navigationHelper.navigateToAdminPage();
        adminHomePage.navigateToDefineSavingsProduct();
        verifyDefineNewSavingsProductText("Complete the fields below.");
        verifyDefineNewSavingsProductText("Click Cancel to return");
        verifyDefineNewSavingsProductText("Product category");
        verifyDefineNewSavingsProductTextDate("Date");
        verifyDefineNewSavingsProductTextFieldsRemoved("fields");
        verifyDefineNewSavingsProductText("Targeted Deposits and Withdrawal Restrictions");
        verifyDefineNewSavingsProductTextDetails("details");
        verifyDefineNewSavingsProductTextDescription(originalCustomText.get("description"));
        verifyDefineNewSavingsProductTextDeposit(originalCustomText.get("recommended Amount for Deposit"));
       
        AdminPage  adminHomePageAgain = navigationHelper.navigateToAdminPage();
        adminHomePageAgain.navigateToCreateUserPage();
        chooseOfficePage.selectOffice("branch1");
        verifyDefineNewSystemUserText("Then click Preview");
        verifyDefineNewSystemUserText("Fields marked with an asterisk are required.");
        verifyDefineNewSystemUserText("Date of Birth");
        verifyDefineNewSystemUserTextNameRemoved("Name");
        verifyDefineNewSystemUserText("User");
        verifyDefineNewSystemUserText("User Title");
        verifyDefineNewSystemUserText("Login information");
        verifyDefineNewSystemUserTextFirst(originalCustomText.get("first"));
        verifyDefineNewSystemUserTextPostal(originalCustomText.get("postal code"));
    }
    
    /*
     * http://mifosforge.jira.com/browse/MIFOSTEST-1170
     * Verify customized text on 'Change Loan status' page
     */
    @Test
    public void verifyCustomizedTextChangeLoanStatusPage() {
        String errorMessage;
        List<String> originalTextVector = new ArrayList<String>(Arrays.asList("Select from the status options below","Then click Continue",
                "information","Status","Change status","Other","explanation","Cancel","note","current status"));
        List<String> customTextVector = new ArrayList<String>(Arrays.asList("Choose options","搜索工具","عر","State","Set different state",
                "Different","answer","Abort","ربية ر","Actual state"));
        for(int i=0;i<originalTextVector.size();i++) {
            adminTestHelper.addCustomizedText(originalTextVector.get(i), customTextVector.get(i));
        }
        
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage("000100000000050");
        AccountChangeStatusPage accountChangeStatusPage = loanAccountPage.navigateToEditAccountStatus();
        
        for(int i=0;i<customTextVector.size()-2;i++) {
            errorMessage = "No text <"+ customTextVector.get(i) +"> present on the page";
            accountChangeStatusPage.verifyTextPresent(customTextVector.get(i), errorMessage);
        }
        
        for(int i=customTextVector.size()-2;i<customTextVector.size();i++) {
            errorMessage = "The Text <"+ customTextVector.get(i) +"> presents on the page";
            accountChangeStatusPage.verifyNotTextPresent(customTextVector.get(i), errorMessage);
        }
        
        for(int i=0;i<3;i++) {
            adminTestHelper.removeCustomizedText(originalTextVector.get(i));
        }
        
        loanAccountPage = navigationHelper.navigateToLoanAccountPage("000100000000050");
        accountChangeStatusPage = loanAccountPage.navigateToEditAccountStatus();
        
        for(int i=0;i<3;i++) {
            errorMessage = "No text <"+ originalTextVector.get(i) +"> present on the page";
            accountChangeStatusPage.verifyTextPresent(originalTextVector.get(i), errorMessage);
        }
        
        for(int i=3;i<originalTextVector.size();i++) {
            adminTestHelper.removeCustomizedText(originalTextVector.get(i));
        }
     }
    
    private void verifyCustomTextIsAdded(String originalText, String customText) {
        String customizedTextLabel = originalText + " > " + customText;
        Assert.assertTrue(selenium.isTextPresent(customizedTextLabel));     
    }
            
    private void verifyClientsAndAccountsCreateCenterLink(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals(customizedText, clientsAndAccountsHomepage.getClientsAndAccountsCreateCenterLink());        
    }
    private void verifyClientsAndAccountsCreateMultipleLoansLink(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals(customizedText, clientsAndAccountsHomepage.getClientsAndAccountsCreateMultipleLoansLink());     
    }
    private void verifyClientsAndAccountsApproveMultipleLoansLink(String customizedText,
                                                                  ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals(customizedText, clientsAndAccountsHomepage.getClientsAndAccountsChangeAccountStatusLink());     
    }
    private void verifyClientsAndAccountsCreateClientLink(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals("Create new " + customizedText, clientsAndAccountsHomepage.getClientsAndAccountsCreateClientLink());
    }
    private void verifyClientsAndAccountsCreateNewClientsText(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("Create new " + customizedText + "s"));
    }
    private void verifyClientsAndAccountsCreateGroupLink(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertFalse(customizedText == clientsAndAccountsHomepage.getClientsAndAccountsCreateGroupLink());        
    }
    private void verifyClientsAndAccountsCreateLoanAccountLink(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertFalse(customizedText == clientsAndAccountsHomepage.getClientsAndAccountsCreateLoanAccountLink());      
    }
    
    private void verifyClientsAndAccountsHomeTab(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals(customizedText, clientsAndAccountsHomepage.getClientsAndAccountsHomeHeaderTab());       
    }
    
    private void verifyClientsAndAccountsTab(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertEquals(customizedText, clientsAndAccountsHomepage.getClientsAndAccountsHeaderTab());       
    }
    
    private void verifyClientsAndAccountsAdminTab(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertFalse(customizedText == clientsAndAccountsHomepage.getClientsAndAccountsAdminHeaderTab());         
    }
    
    private void verifyClientsAndAccountsReportsTab(String customizedText, 
        ClientsAndAccountsHomepage clientsAndAccountsHomepage) {
        Assert.assertFalse(customizedText == clientsAndAccountsHomepage.getClientsAndAccountsReportsHeaderTab());   
    }
    
    private void verifyDefineNewSavingsProductText(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent(customizedText));
    }
    private void verifyDefineNewSavingsProductTextDate(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("From " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("End " + customizedText));
    }
    private void verifyDefineNewSavingsProductTextFields(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("Mandatory " + customizedText + " are necessary"));
    }
    private void verifyDefineNewSavingsProductTextFieldsRemoved(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("Complete the " + customizedText + " below"));
    }
    private void verifyDefineNewSavingsProductTextDetails(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("Savings product " + customizedText));
    }
    private void verifyDefineNewSavingsProductTextDescription(String customizedText) {
        Assert.assertFalse(selenium.isTextPresent(customizedText + " "));
    }
    private void verifyDefineNewSavingsProductTextDeposit(String customizedText) {
        Assert.assertFalse(selenium.isTextPresent(customizedText));
    }
    
    private void verifyDefineNewSystemUserText(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent(customizedText));
    }
    private void verifyDefineNewSystemUserTextName(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("First " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Middle " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Second Last " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Last " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Mifos " + customizedText));
    }
    private void verifyDefineNewSystemUserTextFirst(String customizedText) {
        Assert.assertFalse(selenium.isTextPresent(customizedText + " Name"));
    }
    private void verifyDefineNewSystemUserTextPostal(String customizedText) {
        Assert.assertFalse(selenium.isTextPresent(customizedText));
    }
    private void verifyDefineNewSystemUserTextNameRemoved(String customizedText) {
        Assert.assertTrue(selenium.isTextPresent("First " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Middle " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Second Last " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("Last " + customizedText));
        Assert.assertTrue(selenium.isTextPresent("User " + customizedText));
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