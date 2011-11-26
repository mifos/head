package org.mifos.test.acceptance.personnel;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanofficer", "acceptance", "ui", "no_db_unit"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class TaskListTest extends UiTestCaseBase {
	private static final String DEFAULT_LOANOFFICER_PASSWORD = "testmifos";
    private static final String DEFAULT_LOANOFFICER_USERNAME = "loanofficerbranch1";
    private static final int DAY_OF_WEEK = DateTimeConstants.THURSDAY;
    private static final String CENTER = "branch1 center";
    private static final String GROUP = "GroupInBranch1";
    private static final String CLIENT = "ClientInBranch1 ClientInBranch1";
	
    private NavigationHelper navigationHelper;
    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
    }
    
    /*	Test for loanofficerbranch1
     *  Meetings:  Recur every 1 Week(s) on Thursday
     */
    public void verifyLoanOfficerTaskList () throws Exception {
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2041,1,1,13,0,0,0));
    	LocalDate meetingDate = new LocalDate(2041,1,1);
    	DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
    	while ( meetingDate.getDayOfWeek() != DAY_OF_WEEK ){
    		meetingDate = meetingDate.plusDays(1);
    	}
    	
    	HomePage homePage = navigationHelper.navigateToHomePageAsLogedUser(DEFAULT_LOANOFFICER_USERNAME, DEFAULT_LOANOFFICER_PASSWORD);
    	if ( meetingDate.compareTo(new LocalDate()) != 0 ){
    		homePage.selectTaskListDateOption("value=" + fmt.print(meetingDate));
    	}
    	assertTextFoundOnPage(CENTER);
    	assertTextFoundOnPage(GROUP);
    	assertTextFoundOnPage(CLIENT);
    }
    
}
