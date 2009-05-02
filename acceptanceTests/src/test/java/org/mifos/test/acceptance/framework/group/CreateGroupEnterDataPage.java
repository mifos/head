/**
 * 
 */
package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CreateCenterPreviewDataPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage.SubmitFormParameters;

import com.thoughtworks.selenium.Selenium;

public class CreateGroupEnterDataPage extends MifosPage {

	public CreateGroupEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateGroupEnterDataPage(Selenium selenium) {
		super(selenium);
	}
    
    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        String centerName;
        String loanOfficer;
        CreateMeetingPage.SubmitFormParameters meeting;
        
        public String getCenterName() {
            return this.centerName;
        }

        
        public String getLoanOfficer() {
            return this.loanOfficer;
        }


        public void setCenterName(String centerName) {
            this.centerName = centerName;
        }
        
        public void setLoanOfficer(String loanOfficer) {
            this.loanOfficer = loanOfficer;
        }
        
        public CreateMeetingPage.SubmitFormParameters getMeeting() {
            return this.meeting;
        }

        public void setMeeting(CreateMeetingPage.SubmitFormParameters meeting) {
            this.meeting = meeting;
        }        
    }
    
    public CreateCenterPreviewDataPage submitAndGotoCreateGroupPreviewDataPage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("createnewcenter.input.name", parameters.getCenterName());
        selectIfNotEmpty("loanOfficerId", parameters.getLoanOfficer());
        selenium.click("createnewcenter.link.meetingSchedule");
        waitForPageToLoad();
        CreateMeetingPage meetingPage = new CreateMeetingPage(selenium);
        meetingPage.submitAndGotoCreateCenterEnterDataPage(parameters.getMeeting());
        selenium.click("createnewcenter.button.preview");
        waitForPageToLoad();
        return new CreateCenterPreviewDataPage(selenium);
    }    
}
