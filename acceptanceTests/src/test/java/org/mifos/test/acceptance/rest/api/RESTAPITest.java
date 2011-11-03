/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.rest.api;

import static org.mifos.test.acceptance.rest.api.RESTAPITestHelper.Type;
import static org.mifos.test.acceptance.rest.api.RESTAPITestHelper.By;

import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "rest", "acceptance"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class RESTAPITest extends UiTestCaseBase {

    public static final String CLIENT_GLOBAL_ID = "0002-000000003";
    public static final String PERSONNEL_CURRENT_ID = "current";
    public static final String SYSTEM_INFORMATION_ID = "information";
    public static final String LOAN_ACCOUNT_GLOBAL_ID = "000100000000004";
    public static final String SAVINGS_ACCOUNT_GLOBAL_ID = "000100000000006";

    private RESTAPITestHelper helper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeClass
    public void setUp() throws Exception {
        super.setUp();
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 9, 13, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "REST_API_20110912_dbunit.xml", dataSource, selenium);

        helper = new RESTAPITestHelper(selenium);
        helper.navigateToJsonAjaxPage();
    }

    @AfterClass
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void clientByGlobalNum() throws Exception {
        String type = Type.CLIENT;
        String by = By.GLOBAL_NUMBER;
        String value = CLIENT_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void personnelByCurrentId() throws Exception {
        String type = Type.PERSONNEL;
        String by = By.ID;
        String value = PERSONNEL_CURRENT_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("id");
        jsonAssert.assertEqual("displayName");
        jsonAssert.assertEqual("status");
        jsonAssert.assertEqual("userName");
        jsonAssert.assertEqual("title");
        jsonAssert.assertEqual("locked");
        jsonAssert.assertEqual("levelId");
        jsonAssert.assertEqual("personnelId");
        jsonAssert.assertEqual("officeId");
        jsonAssert.assertEqual("personnelRoles");
        jsonAssert.assertEqual("officeName");
        jsonAssert.assertEqual("emailId");
        jsonAssert.assertEqual("customFields");
        jsonAssert.assertEqual("personnelNotes");
        jsonAssert.assertEqual("globalPersonnelNum");
        jsonAssert.assertEqual("age");
        jsonAssert.assertEqual("recentPersonnelNotes");
        jsonAssert.assertEqual("preferredLocaleLanguageName");
        jsonAssert.assertEqual("preferredLanguageId");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void personnelClientsByCurrentId() throws Exception {
        String type = Type.PERSONNEL_CLIENTS;
        String by = By.ID;
        String value = PERSONNEL_CURRENT_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("centers");
        jsonAssert.assertEqual("groups");
        jsonAssert.assertEqual("clients");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void systemByInformationId() throws Exception {
        String type = Type.SYSTEM;
        String by = By.ID;
        String value = SYSTEM_INFORMATION_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("applicationVersion");
        jsonAssert.assertEqual("databaseVendor");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void loanByGlobalNum() throws Exception {
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("accountFees");
        jsonAssert.assertEqual("accountFlagNames");
        jsonAssert.assertEqual("accountId");
        jsonAssert.assertEqual("accountStateId");
        jsonAssert.assertEqual("accountStateName");
        jsonAssert.assertEqual("accountSurveys");
        jsonAssert.assertEqual("accountTypeId");
        jsonAssert.assertEqual("activeSurveys");
        jsonAssert.assertEqual("businessActivityId");
        jsonAssert.assertEqual("collateralNote");
        jsonAssert.assertEqual("collateralTypeId");
        jsonAssert.assertEqual("customerId");
        jsonAssert.assertEqual("customerName");
        jsonAssert.assertEqual("disbursed");
        jsonAssert.assertEqual("externalId");
        jsonAssert.assertEqual("fundName");
        jsonAssert.assertEqual("globalAccountNum");
        jsonAssert.assertEqual("globalCustNum");
        jsonAssert.assertEqual("gracePeriodDuration");
        jsonAssert.assertEqual("gracePeriodTypeName");
        jsonAssert.assertEqual("group");
        jsonAssert.assertEqual("interestDeductedAtDisbursement");
        jsonAssert.assertEqual("interestRate");
        jsonAssert.assertEqual("interestTypeName");
        jsonAssert.assertEqual("loanActivityDetails");
        jsonAssert.assertEqual("loanSummary");
        jsonAssert.assertEqual("maxNoOfInstall");
        jsonAssert.assertEqual("minNoOfInstall");
        jsonAssert.assertEqual("nextMeetingDate");
        jsonAssert.assertEqual("noOfInstallments");
        jsonAssert.assertEqual("officeId");
        jsonAssert.assertEqual("officeName");
        jsonAssert.assertEqual("performanceHistory");
        jsonAssert.assertEqual("personnelId");
        jsonAssert.assertEqual("prdOfferingName");
        jsonAssert.assertEqual("prinDueLastInst");
        jsonAssert.assertEqual("recurAfter");
        jsonAssert.assertEqual("recurrenceId");
        jsonAssert.assertEqual("redone");
        jsonAssert.assertEqual("totalAmountDue");
        jsonAssert.assertEqual("totalAmountInArrears");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void savingsByGlobalNum() throws Exception {
        String type = Type.SAVINGS;
        String by = By.GLOBAL_NUMBER;
        String value = SAVINGS_ACCOUNT_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("globalAccountNum");
        jsonAssert.assertEqual("amountForDeposit");
        jsonAssert.assertEqual("depositGlCode");
        jsonAssert.assertEqual("depositGlCodeValue");
        jsonAssert.assertEqual("depositType");
        jsonAssert.assertEqual("groupMandatorySavingsAccount");
        jsonAssert.assertEqual("groupMandatorySavingsType");
        jsonAssert.assertEqual("interestCalculationFrequency");
        jsonAssert.assertEqual("interestCalculationFrequencyPeriod");
        jsonAssert.assertEqual("interestCalculationType");
        jsonAssert.assertEqual("interestGlCode");
        jsonAssert.assertEqual("interestGlCodeValue");
        jsonAssert.assertEqual("interestPostingMonthlyFrequency");
        jsonAssert.assertEqual("interestRate");
        jsonAssert.assertEqual("maxWithdrawal");
        jsonAssert.assertEqual("minBalanceForInterestCalculation");
        jsonAssert.assertEqual("openSavingsAccountsExist");
        jsonAssert.assertEqual("recentNoteDtos");
        jsonAssert.assertEqual("recommendedOrMandatoryAmount");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void repayLoanByGlobalNum() throws Exception {
        String data = "amount=100&client="+CLIENT_GLOBAL_ID;
        String type = Type.LOAN_REPAYMENT;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("loanDisplayName");
        jsonAssert.assertEqual("paymentDate");
        jsonAssert.assertEqual("paymentAmount");
        jsonAssert.assertEqual("paymentMadeBy");
        jsonAssert.assertEqual("outstandingBeforePayment");
        jsonAssert.assertEqual("outstandingAfterPayment");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void savingsDepositByGlobalNum() throws Exception {
        String data = "amount=100&client="+CLIENT_GLOBAL_ID;
        String type = Type.SAVINGS_DEPOSIT;
        String by = By.GLOBAL_NUMBER;
        String value = SAVINGS_ACCOUNT_GLOBAL_ID;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("savingsDisplayName");
        jsonAssert.assertEqual("paymentDate");
        jsonAssert.assertEqual("paymentAmount");
        jsonAssert.assertEqual("paymentMadeBy");
        jsonAssert.assertEqual("balanceBeforePayment");
        jsonAssert.assertEqual("balanceAfterPayment");
    }

    class AssertJSON {
        Map<String, Object> actualJSON;
        Map<String, Object> expectedJSON;
        @SuppressWarnings("PMD.SignatureDeclareThrowsException")
        public AssertJSON(String actualJSONString, String expectedJSONString) throws Exception {
            ObjectMapper mapper = helper.getObjectMapper();
            actualJSON = mapper.readValue(actualJSONString, Map.class);
            expectedJSON = mapper.readValue(expectedJSONString, Map.class);
        }

        public void assertEqual(String property) {
            Assert.assertEquals(expectedJSON.get(property), actualJSON.get(property));
        }
    }


}
