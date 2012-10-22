package org.mifos.test.acceptance.center;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.OfficeHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "center", "acceptance", "ui", "no_db_unit"})
public class CenterMeetingTest extends UiTestCaseBase{
    
    private String centerName;
    private String groupName;
    private String firstName;
    private String lastName;
    private static final String loanProductName = "WeeklyGroupFlatLoanWithOnetimeFee";
    
    @Autowired
    private ApplicationDatabaseOperation databaseOperation;
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
    }
    
    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    private void prepareGroupForTest() {
        GroupTestHelper groupHelper = new GroupTestHelper(selenium);
        
        CreateGroupEntryPage.CreateGroupSubmitParameters groupParams =
                new CreateGroupEntryPage.CreateGroupSubmitParameters();
        groupName = StringUtil.getRandomString(8);
        groupParams.setGroupName(groupName);
        groupParams.setRecruitedBy(firstName + " " + lastName);
        
        groupHelper.createNewGroup(centerName, groupParams);
        groupHelper.activateGroup(groupName);
        new NavigationHelper(selenium).navigateToHomePage();
    }
    
    private void prepareCenterForTest(MeetingParameters meeting) {
        
        OfficeHelper officeHelper = new OfficeHelper(selenium);
        
        String officeName = "Office" + StringUtil.getRandomString(10);
        
        OfficeParameters officeParameters = new OfficeParameters();
        officeParameters.setOfficeName(officeName);
        officeParameters.setOfficeType(OfficeParameters.BRANCH_OFFICE);
        officeParameters.setShortName(StringUtil.getRandomString(4));
        officeParameters.setParentOffice("Head Office(Mifos HO )");
        
        officeHelper.createOffice(officeParameters);
        UserHelper userHelper = new UserHelper(selenium);
        CreateUserParameters userParameters = new CreateUserParameters();
        firstName = StringUtil.getRandomString(6);
        lastName = StringUtil.getRandomString(6);
        userParameters.setFirstName(firstName);
        userParameters.setLastName(lastName);
        userParameters.setDateOfBirthDD("11");
        userParameters.setDateOfBirthMM("11");
        userParameters.setDateOfBirthYYYY("1950");
        String userName = StringUtil.getRandomString(6);
        String password = StringUtil.getRandomString(6);
        userParameters.setPassword(password);
        userParameters.setPasswordRepeat(password);
        userParameters.setUserName(userName);
        userParameters.setUserLevel(CreateUserParameters.LOAN_OFFICER);
        userParameters.setGender(CreateUserParameters.MALE);
        userHelper.createUser(userParameters, officeName);
        
        CenterTestHelper centerHelper = new CenterTestHelper(selenium);
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = 
                new CreateCenterEnterDataPage.SubmitFormParameters();
        centerName = "Center" + StringUtil.getRandomString(8);
        formParameters.setCenterName(centerName);
        formParameters.setLoanOfficer(firstName + " " + lastName);
        formParameters.setMeeting(meeting);
        
        centerHelper.createCenter(formParameters, officeName);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void setTime(DateTime time) throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService =
                new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(time);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithDefaultMeetingStartDate(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        meeting.setMeetingStartDate(null);

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("22", "10", "2012");       
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithDefaultMeetingStartDateWhenLSIMIsDisabled() throws Exception {
        verifyDefaultDisburstmentDateWithDefaultMeetingStartDate(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithDefaultMeetingStartDateWhenLSIMIsEnabled() throws Exception {
        verifyDefaultDisburstmentDateWithDefaultMeetingStartDate(1);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithTodayMeetingStartDate(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        meeting.setMeetingStartDate("19/10/2012");

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("22", "10", "2012");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithTodayMeetingStartDateWhenLSIMIsDisabled() throws Exception {
        verifyDefaultDisburstmentDateWithTodayMeetingStartDate(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithTodayMeetingStartDateWhenLSIMIsEnabled() throws Exception {
        verifyDefaultDisburstmentDateWithTodayMeetingStartDate(1);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithTodayMeetingStartDateAndCurrentWeekDay(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.FRIDAY);
        meeting.setMeetingStartDate("19/10/2012");

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("19", "10", "2012");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithTodayMeetingStartDateAndCurrentWeekDayWhenLSIMIsDisabled()
            throws Exception {
        verifyDefaultDisburstmentDateWithTodayMeetingStartDateAndCurrentWeekDay(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithTodayMeetingStartDateAndCurrentWeekDayWhenLSIMIsEnabled()
            throws Exception {
        verifyDefaultDisburstmentDateWithTodayMeetingStartDateAndCurrentWeekDay(1);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithMeetingStartDateInThePast(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        meeting.setMeetingStartDate("10/10/2000");

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("22", "10", "2012");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInThePastWhenLSIMIsDisabled() throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInThePast(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInThePastWhenLSIMIsEnabled() throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInThePast(1);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFuture(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meeting.setMeetingStartDate("25/10/2012");

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("31", "10", "2012");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWhenLSIMIsDisabled() throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInTheFuture(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWhenLSIMIsEnabled() throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInTheFuture(1);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWithSameWeekDays(int lsim) throws Exception {
        databaseOperation.updateLSIM(lsim);
        DateTime time = new DateTime(2012, 10, 19, 1, 0, 0, 0);
        setTime(time);
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(5));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meeting.setMeetingStartDate("31/10/2012");

        prepareCenterForTest(meeting);
        prepareGroupForTest();

        LoanTestHelper loanHelper = new LoanTestHelper(selenium);
        
        CreateLoanAccountEntryPage loanEntryPage = 
                loanHelper.navigateToCreateLoanAccountEntryPageWithoutLogout(groupName, loanProductName);
        loanEntryPage.verifyDisbsursalDate("31", "10", "2012");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWithSameWeekDaysWhenLSIMIsDisabled()
            throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWithSameWeekDays(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWithSameWeekDaysWhenLSIMIsEnabled()
            throws Exception {
        verifyDefaultDisburstmentDateWithMeetingStartDateInTheFutureWithSameWeekDays(1);
    }
}
