package org.mifos.test.acceptance.framework.client;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;

public class EditMeetingPage extends MifosPage {
    public EditMeetingPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("updatemeeting");
    }

    public ClientViewDetailsPage editClientMeeting(MeetingParameters parameters) {
        selectValueIfNotZero("weekDay", parameters.getWeekDay().getId());
        submit();
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }
    
    public void submit(){
    	selenium.click("//input[@value='Save']");
    }
    
    public CenterViewDetailsPage editCenterMeeting(MeetingParameters parameters) {
    	selectValueIfNotZero("weekDay", parameters.getWeekDay().getId());
    	submit();
    	waitForPageToLoad();
    	return new CenterViewDetailsPage(selenium);
    }

    public GroupSearchAddMemberPage addGroupMembership(){
        selenium.click("updatemeeting.link.addGroupMembership");
        waitForPageToLoad();
        return new GroupSearchAddMemberPage(selenium);
    }

}
