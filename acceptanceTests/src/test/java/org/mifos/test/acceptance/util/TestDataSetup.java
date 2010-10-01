package org.mifos.test.acceptance.util;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.OfficeHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;

import java.sql.SQLException;

public class TestDataSetup {

    private final ApplicationDatabaseOperation applicationDatabaseOperation;
    private final Selenium selenium;

    public TestDataSetup(Selenium selenium, ApplicationDatabaseOperation applicationDatabaseOperation) {
        this.selenium = selenium;
        this.applicationDatabaseOperation = applicationDatabaseOperation;
    }

    public void createBranch(int officeType, String officeName, String shortName) throws SQLException {
        if (applicationDatabaseOperation.doesBranchOfficeExist(officeName,officeType, shortName)) {
            return;
        }
        OfficeHelper officeHelper = new OfficeHelper(selenium);
        OfficeParameters officeParameters = new OfficeParameters();
        officeParameters.setOfficeType(officeType);
        officeParameters.setOfficeName(officeName);
        officeParameters.setShortName(shortName);
        officeParameters.setParentOffice("Head Office(Mifos HO )");
        officeHelper.createOffice(officeParameters);
    }

    public void createUser(String userLoginName, String userName, String officeName) throws SQLException {
        String firstName = userName.split(" ")[0];
        String lastName = userName.split(" ")[1];
        if (applicationDatabaseOperation.doesSystemUserExist(userLoginName,userName,officeName)) {
            return;
        }
        CreateUserParameters userParameters = new AdminPage(selenium).getAdminUserParameters();
        userParameters.setUserName(userLoginName);
        userParameters.setFirstName(firstName);
        userParameters.setLastName(lastName);
        new UserHelper(selenium).createUser(userParameters, officeName);
    }

    public void createClient(String clientName, String officeName, String loanOfficerName) throws SQLException {
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientPersonalInfoParameters = defineClient(clientName);
        if (applicationDatabaseOperation.doesClientExist(clientName, officeName)) {
            return;
        }
        new NavigationHelper(selenium).navigateToHomePage().
                navigateToClientsAndAccountsUsingHeaderTab().
                navigateToCreateNewClientPage().
                navigateToCreateClientWithoutGroupPage().
                chooseOffice(officeName).
                create(clientPersonalInfoParameters).
                submitAndGotoCreateClientEnterMfiDataPage().
                submitAndGotoCreateClientPreviewDataPage(defineMfiData(loanOfficerName)).
                submit().navigateToClientViewDetailsPage().
                navigateToCustomerChangeStatusPage().
                submitAndGotoCustomerChangeStatusPreviewDataPage(setApprovalStatus()).
                submitAndGotoClientViewDetailsPage();
    }

    private CustomerChangeStatusPage.SubmitFormParameters setApprovalStatus() {
        CustomerChangeStatusPage.SubmitFormParameters approvalStatusParameters = new CustomerChangeStatusPage.SubmitFormParameters();
        approvalStatusParameters.setNotes("For Test");
        approvalStatusParameters.setStatus(CustomerChangeStatusPage.SubmitFormParameters.APPROVED);
        return approvalStatusParameters;
    }

    private CreateClientEnterMfiDataPage.SubmitFormParameters defineMfiData(String userName) {
        CreateClientEnterMfiDataPage.SubmitFormParameters mfiDataParameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        mfiDataParameters.setMeeting(setMeetingScheduleParameters());
        mfiDataParameters.setLoanOfficerId(userName);
        return mfiDataParameters;
    }

    private MeetingParameters setMeetingScheduleParameters() {
        MeetingParameters meetingParameters = new MeetingParameters();
        meetingParameters.setMeetingPlace("test_location");
        meetingParameters.setWeekFrequency("1");
        meetingParameters.setWeekDay(MeetingParameters.TUESDAY);
        return meetingParameters;
    }

    private CreateClientEnterPersonalDataPage.SubmitFormParameters defineClient(String clientName) {
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientPersonalInfoParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientPersonalInfoParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        clientPersonalInfoParameters.setFirstName(clientName.split(" ")[0]);
        clientPersonalInfoParameters.setLastName(clientName.split(" ")[1]);
        clientPersonalInfoParameters.setDateOfBirthDD("22");
        clientPersonalInfoParameters.setDateOfBirthMM("05");
        clientPersonalInfoParameters.setDateOfBirthYYYY("1987");
        clientPersonalInfoParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        clientPersonalInfoParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        clientPersonalInfoParameters.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        clientPersonalInfoParameters.setSpouseFirstName("father");
        clientPersonalInfoParameters.setSpouseLastName("lastname" + StringUtil.getRandomString(8));
        return clientPersonalInfoParameters;
    }
}
