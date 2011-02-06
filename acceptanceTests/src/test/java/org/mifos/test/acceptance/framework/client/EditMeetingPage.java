package org.mifos.test.acceptance.framework.client;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;

public class EditMeetingPage extends MifosPage {
    public EditMeetingPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("updatemeeting");
    }

    public ClientViewDetailsPage editClientMeeting(MeetingParameters parameters) {
        selectValueIfNotZero("weekDay", parameters.getWeekDay());
        selenium.click("//input[@value='Save']");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }

    public GroupSearchAddMemberPage addGroupMembership(){
        selenium.click("updatemeeting.link.addGroupMembership");
        waitForPageToLoad();
        return new GroupSearchAddMemberPage(selenium);
    }

}
