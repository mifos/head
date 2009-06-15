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

package org.mifos.test.acceptance.holiday;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"holiday","workInProgress"})
public class AdditionalHolidayTest extends UiTestCaseBase {
    
    private AppLauncher appLauncher;
    
    private static final String NEXT_WORKING_DAY = "Next Working Day";
    private static final String SAME_DAY = "Same Day";
    private static final String NEXT_MEETING_OR_REPAYMENT = "Next Meeting/Repayment";
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,3,11,0,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    /* 
     * jhil comment:
     * no db test is needed for this test since the actual test is just to click a link ?
     * This is test TC-07.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createHolidayFromViewHolidays() throws Exception {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewHolidaysPage viewHolidayPage = adminPage.navigateToViewHolidaysPage();
        viewHolidayPage.verifyPage();
        
        CreateHolidayEntryPage createHolidayPage = viewHolidayPage.navigateToDefineHolidayPage();
        createHolidayPage.verifyPage();
        
        CreateHolidayConfirmationPage confirmHolidayPage = createHolidayPage.submitAndNavigateToHolidayConfirmationPage(this.getParametersForHolidayOverlappingOneMeeting(SAME_DAY));
        confirmHolidayPage.verifyPage();
        confirmHolidayPage.submitAndNavigateToViewHolidaysPage();
    }
    
    // TC08
    public void createHolidayOnAMeetingWithRepaymentSameDay() {
        CreateHolidaySubmitParameters sameDayRepaymentParameters = getParametersForHolidayOnAMeeting(SAME_DAY);
        this.createHoliday(sameDayRepaymentParameters);
        
        /* TODO:
         * 1. Run batch jobs (how do I do this from the test?)
         * 2. Navigate to the repayment schedule for the loan (or check the db?). Perhaps DB is better and easier.
         */
       
    }
    
    // TC09
    public void createHolidayOnAMeetingWithRepaymentNextWorkingDay() {
        CreateHolidaySubmitParameters nextWorkingDayRepaymentParameters = getParametersForHolidayOnAMeeting(NEXT_WORKING_DAY);
        this.createHoliday(nextWorkingDayRepaymentParameters);

       
    }
    
    // TC10
    public void createHolidayOnAMeetingWithRepaymentNextMeeting() {
        CreateHolidaySubmitParameters nextMeetingRepaymentParameters = getParametersForHolidayOnAMeeting(NEXT_MEETING_OR_REPAYMENT);
        this.createHoliday(nextMeetingRepaymentParameters);

       
    }
    
    
    private void createHoliday(CreateHolidaySubmitParameters params)
    {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        createHolidayEntryPage.verifyPage();
        
        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.verifyPage();
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }   
    
    /*
     * Using acceptance_small_003 as a base, this method returns a holiday that overlaps a loan.
     * Loan #000100000000206 is used for most of the tests. The loan's product name is WeeklyGroupFlatLoanWithOnetimeFee
     * and the repayments are weekly.
     * 
     */
    private CreateHolidaySubmitParameters getParametersForHolidayOverlappingOneMeeting(String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        
        params.setName("Additional Test Holiday for overlapping one meeting");
        params.setFromDateDD("20");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2009");
        params.setThruDateDD("26"); // TODO find the right date
        params.setThruDateMM("03");
        params.setThruDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        
        return params;
    }
    
/* Comment out this currently unused method for now to avoid PMD violation.
 *     private CreateHolidaySubmitParameters getParametersForHolidayOverlappingTwoMeetings(String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        
        params.setName("Additional Test Holiday");
        params.setFromDateDD("20");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2009");
        params.setThruDateDD("5"); // TODO find the right date
        params.setThruDateMM("04");
        params.setThruDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        
        return params;
    }
*/    
    private CreateHolidaySubmitParameters getParametersForHolidayOnAMeeting(String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        
        params.setName("Additional Test Holiday");
        params.setFromDateDD("19");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        
        return params;
    }
    
    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }
}
