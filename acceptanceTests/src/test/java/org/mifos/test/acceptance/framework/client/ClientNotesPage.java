package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;

import junit.framework.Assert;

import com.thoughtworks.selenium.Selenium;

public class ClientNotesPage extends MifosPage {

    public ClientNotesPage(Selenium selenium) {
       super(selenium);
    }
    public ClientViewDetailsPage addNotePreviewAndSubmit(String note){
        selenium.type("addCustomerNotes.input.note", note);
        selenium.click("addCustomerNotes.button.preview");
        waitForPageToLoad();
        selenium.click("previewCustomerNotes.button.submit");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }

    public void verifyTextOnPage(String testNote) {
        Assert.assertTrue(selenium.isTextPresent(testNote));

    }
}
