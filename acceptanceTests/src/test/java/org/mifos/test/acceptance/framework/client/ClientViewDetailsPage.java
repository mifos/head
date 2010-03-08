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

package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ClientViewDetailsPage extends MifosPage {

    public ClientViewDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("ViewClientDetails");
    }

    public String getHeading() {
        return selenium.getText("viewClientDetails.heading");
    }

    public String getStatus() {
        return selenium.getText("viewClientDetails.text.status");
    }

    public String getDateOfBirth() {
        return selenium.getText("viewClientDetails.text.dateOfBirth");
    }

    public String getSpouseFatherName() {
        return selenium.getText("viewClientDetails.text.spouseFatherName");
    }

    public String getPovertyStatus() {
        return selenium.getText("viewClientDetails.text.povertyStatus");
    }

    public String getHandicapped() {
        return selenium.getText("viewClientDetails.text.handicapped");
    }

    public String getNotes() {
        return selenium.getText("viewClientDetails.text.notes");
    }

    public void verifyName(String fullName) {
        Assert.assertTrue(getHeading().contains(fullName));
    }

    public void verifyStatus(String status) {
        Assert.assertEquals(getStatus(), status);
    }

    public void verifyDateOfBirth(String dd, String mm, String yyyy) {
      String dateOfBirth = getDateOfBirth();
      Assert.assertTrue(dateOfBirth.contains(dd));
      Assert.assertTrue(dateOfBirth.contains(mm));
      Assert.assertTrue(dateOfBirth.contains(yyyy));
    }

    public void verifySpouseFather(String spouseFatherName) {
        Assert.assertEquals(getSpouseFatherName(), spouseFatherName);
    }

    public void verifyPovertyStatus(String povertyStatus) {
        Assert.assertEquals(getPovertyStatus(), povertyStatus);
    }

    public void verifyHandicapped(String handicapped) {
        Assert.assertEquals(getHandicapped(), handicapped);
    }

    public void verifyNotes(String notes) {
        Assert.assertTrue(getNotes().contains(notes));
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsHomepage() {
        selenium.click("header.link.clientsAndAccounts");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public CustomerChangeStatusPage navigateToCustomerChangeStatusPage() {
        selenium.click("viewClientDetails.link.editStatus");
        waitForPageToLoad();
        return new CustomerChangeStatusPage(selenium);
    }

    public ClientEditMFIPage navigateToEditMFIPage() {
        selenium.click("viewClientDetails.link.editMfiInformation");
        waitForPageToLoad();
        return new ClientEditMFIPage(selenium);
    }

    public ClientNotesPage navigateToNotesPage(){
        selenium.click("viewClientDetails.link.notesLink");
        waitForPageToLoad();
        return new ClientNotesPage(selenium);
    }

    public void verifyTextOnPage(String text){
        Assert.assertTrue(selenium.isTextPresent(text));
    }

    public ClientNotesPage navigateToAllNotesPage() {
        selenium.click("viewClientDetails.link.seeAllNotes");
        waitForPageToLoad();
        return new ClientNotesPage(selenium);
    }

    public ClosedAccountsPage navigateToClosedAccountsPage() {
        selenium.click("viewClientDetails.link.viewAllClosedAccounts");
        waitForPageToLoad();
        return new ClosedAccountsPage(selenium);
    }

    public ClientEditFamilyPage editFamilyInformation() {
        selenium.click("viewClientDetails.link.editFamilyInformation");
        waitForPageToLoad();
        return new ClientEditFamilyPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("viewClientDetails.link.attachSurvey");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }


}
