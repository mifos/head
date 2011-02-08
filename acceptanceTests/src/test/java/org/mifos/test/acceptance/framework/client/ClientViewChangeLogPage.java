package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ClientViewChangeLogPage extends MifosPage{

    public ClientViewChangeLogPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ClientViewChangeLog");
    }

    public void verifyLastEntryOnChangeLog(String field, String oldvalue, String newValue, String user){
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.1"),field);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.2"),oldvalue);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.3"),newValue);
        Assert.assertEquals(selenium.getTable("auditLogRecords.1.4"),user);
    }

}
