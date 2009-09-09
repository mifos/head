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
 
package org.mifos.test.acceptance.framework;

import org.mifos.test.acceptance.framework.center.CreateCenterChooseOfficePage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.ChooseOfficePage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientConfirmationPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewDataPage;
import org.mifos.test.acceptance.framework.group.GroupSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountsSearchPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchPage;
import org.mifos.test.acceptance.util.StringUtil;

import com.thoughtworks.selenium.Selenium;

public class ClientsAndAccountsHomepage extends AbstractPage {
	
	public ClientsAndAccountsHomepage() {
		super();
	}
	
	public ClientsAndAccountsHomepage(Selenium selenium) {
		super(selenium);
	}
	
	public void verifyPage() {
	    verifyPage("ClientsAccounts");
	}

	// TODO fix these 5 following methods. They all belong in a navigation helper.
	public CollectionSheetEntrySelectPage navigateToEnterCollectionSheetDataUsingLeftMenu() {
		selenium.click("id=menu.link.label.enter.label.collectionsheet.label.data");
		waitForPageToLoad();
		return new CollectionSheetEntrySelectPage(selenium);
	}

    public CreateLoanAccountsSearchPage navigateToCreateMultipleLoanAccountsUsingLeftMenu() {
        selenium.click("menu.link.label.createmultipleloanaccountsprefix.loan.label.createmultipleloanaccountssuffix"); 
        waitForPageToLoad();
        return new CreateLoanAccountsSearchPage(selenium);
    }
    
    public CreateLoanAccountSearchPage navigateToCreateLoanAccountUsingLeftMenu() {
        selenium.click("menu.link.label.createloanaccountprefix.loan.label.createloanaccountsuffix"); 
        waitForPageToLoad();
        return new CreateLoanAccountSearchPage(selenium);
    }

    public CreateSavingsAccountSearchPage navigateToCreateSavingsAccountUsingLeftMenu() {
        selenium.click("menu.link.label.createsavingsaccountprefix.savings.label.createsavingsaccountsuffix"); 
        waitForPageToLoad();
        return new CreateSavingsAccountSearchPage(selenium);
    }

    public CreateCenterChooseOfficePage navigateToCreateNewCenterPage() {
        selenium.click("menu.link.label.createnew.center"); 
        waitForPageToLoad();
        return new CreateCenterChooseOfficePage(selenium);
    }    
    
    public GroupSearchPage navigateToCreateNewClientPage() {
        selenium.click("menu.link.label.createnew.client"); 
        waitForPageToLoad();
        return new GroupSearchPage(selenium);
    }
    
    // TODO belongs in a helper
    public ClientViewDetailsPage createClient(String loanOfficer, String officeName) {
        GroupSearchPage groupSearchPage = navigateToCreateNewClientPage();
        ChooseOfficePage chooseOfficePage = groupSearchPage.navigateToCreateClientWithoutGroupPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = chooseOfficePage.chooseOffice(officeName);
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        formParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        formParameters.setFirstName("test");
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("22");
        formParameters.setDateOfBirthMM("05");
        formParameters.setDateOfBirthYYYY("1987");
        formParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        formParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        formParameters.setHandicapped("Yes");
        formParameters.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        formParameters.setSpouseFirstName("father");
        formParameters.setSpouseLastName("lastname" + StringUtil.getRandomString(8));
       
        CreateClientEnterMfiDataPage clientMfiDataPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage(formParameters);

        CreateClientEnterMfiDataPage.SubmitFormParameters mfiFormParameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        mfiFormParameters.setLoanOfficerId(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay(MeetingParameters.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Bangalore");
        
        mfiFormParameters.setMeeting(meetingFormParameters);

        CreateClientPreviewDataPage clientPreviewDataPage = clientMfiDataPage.submitAndGotoCreateClientPreviewDataPage(mfiFormParameters);
        CreateClientConfirmationPage clientConfirmationPage = clientPreviewDataPage.submit();
        clientConfirmationPage.verifyPage();
        
        ClientViewDetailsPage clientViewDetailsPage = clientConfirmationPage.navigateToClientViewDetailsPage();
        clientViewDetailsPage.verifyName(formParameters.getFirstName() + " " + formParameters.getLastName());
        clientViewDetailsPage.verifyDateOfBirth(formParameters.getDateOfBirthDD(), formParameters.getDateOfBirthMM(), formParameters.getDateOfBirthYYYY());
        clientViewDetailsPage.verifySpouseFather(formParameters.getSpouseFirstName() + " " + formParameters.getSpouseLastName());
        clientViewDetailsPage.verifyHandicapped(formParameters.getHandicapped());
        return clientViewDetailsPage;
    }  

    // TODO is this not in SearchHelper?
    public ClientSearchResultsPage searchForClient(String searchString)
    {
        selenium.type("clients_accounts.input.search", searchString);
        selenium.click("clients_accounts.button.search");
        waitForPageToLoad();
        return new ClientSearchResultsPage(selenium);
    }
    
    // TODO this belongs in a helper
    public ClientViewDetailsPage changeCustomerStatus(ClientViewDetailsPage clientDetailsPage) {    
        CustomerChangeStatusPage statusChangePage = clientDetailsPage.navigateToCustomerChangeStatusPage();
        
        CustomerChangeStatusPage.SubmitFormParameters statusParameters = new CustomerChangeStatusPage.SubmitFormParameters();
        statusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.PARTIAL_APPLICATION);
        statusParameters.setNotes("Status change");        
        
        CustomerChangeStatusPreviewDataPage statusPreviewPage = statusChangePage.submitAndGotoCustomerChangeStatusPreviewDataPage(statusParameters);
        ClientViewDetailsPage clientDetailsPage2 = statusPreviewPage.submitAndGotoClientViewDetailsPage();
        clientDetailsPage2.verifyNotes(statusParameters.getNotes());
        
        CustomerChangeStatusPage statusChangePage2 = clientDetailsPage2.navigateToCustomerChangeStatusPage();
        statusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.PENDING_APPROVAL);
        statusParameters.setNotes("notes");     
        CustomerChangeStatusPreviewDataPage statusPreviewPage2 = 
            statusChangePage2.submitAndGotoCustomerChangeStatusPreviewDataPage(statusParameters);
        
        ClientViewDetailsPage clientDetailsPage3 = statusPreviewPage2.submitAndGotoClientViewDetailsPage();
        clientDetailsPage3.verifyNotes(statusParameters.getNotes());
        
        CustomerChangeStatusPage statusChangePage3 = clientDetailsPage3.navigateToCustomerChangeStatusPage();

        ClientViewDetailsPage clientDetailsPage4 = statusChangePage3.cancelAndGotoClientViewDetailsPage(statusParameters);
        clientDetailsPage4.verifyNotes(statusParameters.getNotes());
        
        return clientDetailsPage4;
    }

}
