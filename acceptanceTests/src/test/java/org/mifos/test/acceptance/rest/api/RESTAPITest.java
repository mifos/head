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

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.rest.api.RESTAPITestHelper.By;
import org.mifos.test.acceptance.rest.api.RESTAPITestHelper.Op;
import org.mifos.test.acceptance.rest.api.RESTAPITestHelper.Type;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "rest", "acceptance"})
@SuppressWarnings("PMD")
public class RESTAPITest extends UiTestCaseBase {

    public static final String CLIENT_GLOBAL_ID = "0002-000000003";
    public static final String GROUP_GLOBAL_ID = "0002-000000002";
    public static final String CENTER_GLOBAL_ID = "0002-000000001";
    public static final String CENTER_ID = "1";
    public static final String OFFICE_ID = "1";
    public static final String PERSONNEL_CURRENT_ID = "current";
    public static final String SYSTEM_INFORMATION_ID = "information";
    public static final String PAYMENT_TYPES_ACCEPTED = "accepted";
    public static final String LOAN_ACCOUNT_GLOBAL_ID = "000100000000004";
    public static final String LOAN_ACCOUNT_2_GLOBAL_ID = "000100000000005";
    public static final String LOAN_ACCOUNT_3_GLOBAL_ID = "000100000000008";
    public static final String SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID = "000100000000006";
    public static final String SAVINGS_MANDATORY_ACCOUNT_GLOBAL_ID = "000100000000007";
    public static final String MEETINGS_DAY = "19-09-2011";
    public static final String TODAY = "13-09-2011";
    public static final String SAVINGS_PRODUCT = "3";
    
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
    public void clientChargesByGlobalNum() throws Exception {
        String type = Type.CLIENT;
        String by = By.GLOBAL_NUMBER;
        String value = CLIENT_GLOBAL_ID + Op.CHARGES;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void groupByGlobalNum() throws Exception {
        String type = Type.GROUP;
        String by = By.GLOBAL_NUMBER;
        String value = GROUP_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void groupChargesByGlobalNum() throws Exception {
        String type = Type.GROUP;
        String by = By.GLOBAL_NUMBER;
        String value = GROUP_GLOBAL_ID + Op.CHARGES;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void centerByGlobalNum() throws Exception {
        String type = Type.CENTER;
        String by = By.GLOBAL_NUMBER;
        String value = CENTER_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void centerChargesByGlobalNum() throws Exception {
        String type = Type.CENTER;
        String by = By.GLOBAL_NUMBER;
        String value = CENTER_GLOBAL_ID + Op.CHARGES;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void officeById() throws Exception {
        String type = Type.OFFICE;
        String by = By.ID;
        String value = OFFICE_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly", dependsOnMethods={"centerByGlobalNum", "centerChargesByGlobalNum"})
    public void applyCustomerChargeByGlobalNum() throws Exception {
    	String data = "?amount=5&feeId=-1";
        String type = Type.CUSTOMER;
        String by = By.GLOBAL_NUMBER;
        String value = CENTER_GLOBAL_ID + Op.CHARGE;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("chargeDate");
        jsonAssert.assertEqual("chargeAmount");
        jsonAssert.assertEqual("chargeMadeBy");
        jsonAssert.assertEqual("totalDueBeforeCharge");
        jsonAssert.assertEqual("totalDueAfterCharge");
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
        String type = Type.PERSONNEL;
        String by = By.ID;
        String value = PERSONNEL_CURRENT_ID + Op.CLIENTS;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("centers");
        jsonAssert.assertEqual("groups");
        jsonAssert.assertEqual("clients");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void personnelClientsByCurrentIdAndSpecifiedDay() throws Exception {
        logOut();
        helper.navigateToJsonAjaxPageAsLoanOfficer();
        String type = Type.PERSONNEL + "/" + By.ID + "-" + PERSONNEL_CURRENT_ID;
        String by = By.MEETINGS;
        String value = MEETINGS_DAY;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("centers");
        jsonAssert.assertEqual("groups");
        jsonAssert.assertEqual("clients");
        logOut();
        helper.navigateToJsonAjaxPage();
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
    public void paymentTypesByAcceptedState() throws Exception {
        String type = Type.PAYMENT_TYPES;
        String by = By.STATE;
        String value = PAYMENT_TYPES_ACCEPTED;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void accountTransactionHistoryByGlobalNum() throws Exception {
        String type = Type.ACCOUNT;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.TRXNHISTORY;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSONList jsonAssert = new AssertJSONList(actualJSON, expectedJSON);
        jsonAssert.assertEqual("type");
        jsonAssert.assertEqual("locale");
        jsonAssert.assertEqual("glcode");
        jsonAssert.assertEqual("notes");
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("paymentId");
        jsonAssert.assertEqual("accountTrxnId");
        jsonAssert.assertEqual("balance");
        jsonAssert.assertEqual("userPrefferedTransactionDate");
        jsonAssert.assertEqual("postedBy");
        jsonAssert.assertEqual("credit");
        jsonAssert.assertEqual("debit");
        jsonAssert.assertEqual("userPrefferedPostedDate");
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
        jsonAssert.assertEqual("recentNoteDtos");
        jsonAssert.assertEqual("recurAfter");
        jsonAssert.assertEqual("recurrenceId");
        jsonAssert.assertEqual("redone");
        jsonAssert.assertEqual("totalAmountDue");
        jsonAssert.assertEqual("totalAmountInArrears");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void loanInstallmentByGlobalNum() throws Exception {
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.INSTALLMENT;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void loanRepaymentScheduleByGlobalNum() throws Exception {
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.SCHEDULE;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void applicableFeesForLoanByGlobalNum() throws Exception {
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.APPLICABLE_FEES;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void applicableFeesForClientByGlobalNum() throws Exception {
        String type = Type.CUSTOMER;
        String by = By.GLOBAL_NUMBER;
        String value = CLIENT_GLOBAL_ID + Op.APPLICABLE_FEES;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void savingsByGlobalNum() throws Exception {
        String type = Type.SAVINGS;
        String by = By.GLOBAL_NUMBER;
        String value = SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID;
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
        jsonAssert.assertEqual("accountBalance");
        jsonAssert.assertEqual("accountStateName");
        jsonAssert.assertEqual("depositTypeName");
        jsonAssert.assertEqual("dueDate");
        jsonAssert.assertEqual("missedDeposits");
        jsonAssert.assertEqual("totalDeposits");
        jsonAssert.assertEqual("totalInterestEarned");
        jsonAssert.assertEqual("totalWithdrawals");
        jsonAssert.assertEqual("totalAmountDue");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups="readOnly")
    public void savingsDueByGlobalNum() throws Exception {
        String type = Type.SAVINGS;
        String by = By.GLOBAL_NUMBER;
        String value = SAVINGS_MANDATORY_ACCOUNT_GLOBAL_ID + Op.DUE;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void repayLoanByGlobalNum() throws Exception {
        String data = "?amount=100&paymentDate="+TODAY+"&paymentModeId=1&receiptId=12&receiptDate="+TODAY;
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.REPAY;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertLoanRepaymentResponse();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly", dependsOnMethods = "repayLoanByGlobalNum")
    public void applyLoanAdjustmentByGlobalNum() throws Exception {
        String data = "?note=Adjustment applied";
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_GLOBAL_ID + Op.ADJUSTMENT;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("loanDisplayName");
        jsonAssert.assertEqual("adjustmentDate");
        jsonAssert.assertEqual("adjustmentAmount");
        jsonAssert.assertEqual("adjustmentMadeBy");
        jsonAssert.assertEqual("outstandingAfterAdjustment");
        jsonAssert.assertEqual("outstandingBeforeAdjustment");
        jsonAssert.assertEqual("note");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly", dependsOnMethods = "disburseLoanByGlobalNum")
    public void applyLoanChargeByGlobalNum() throws Exception {
        String data = "?amount=5&feeId=-1";
        String type = Type.LOAN;
        String by = By.GLOBAL_NUMBER;
        String value = LOAN_ACCOUNT_3_GLOBAL_ID + Op.CHARGE;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("loanDisplayName");
        jsonAssert.assertEqual("chargeDate");
        jsonAssert.assertEqual("chargeAmount");
        jsonAssert.assertEqual("chargeMadeBy");
        jsonAssert.assertEqual("outstandingAfterCharge");
        jsonAssert.assertEqual("outstandingBeforeCharge");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly", dependsOnMethods="savingsDepositWithdrawByGlobalNum")
    public void applySavingsAdjustmentByGlobalNum() throws Exception {
        String data = "?amount=60&note=Adjustment applied";
        String type = Type.SAVINGS;
        String by = By.GLOBAL_NUMBER;
        String value = SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID + Op.ADJUSTMENT;
        String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("savingsDisplayName");
        jsonAssert.assertEqual("adjustmentDate");
        jsonAssert.assertEqual("adjustmentAmount");
        jsonAssert.assertEqual("adjustmentMadeBy");
        jsonAssert.assertEqual("outstandingAfterAdjustment");
        jsonAssert.assertEqual("outstandingBeforeAdjustment");
        jsonAssert.assertEqual("note");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void fullRepayLoanByGlobalNum() throws Exception {
        String data = "?waiveInterest=false&paymentDate="+TODAY+"&paymentModeId=1&receiptId=12&receiptDate="+TODAY;
    	String type = Type.LOAN;
    	String by = By.GLOBAL_NUMBER;
    	String value = LOAN_ACCOUNT_2_GLOBAL_ID + Op.FULLREPAY;
    	String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertLoanRepaymentResponse();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void disburseLoanByGlobalNum() throws Exception {
    	String data = "?disbursalDate="+TODAY+"&disbursePaymentTypeId=1";
    	String type = Type.LOAN;
    	String by = By.GLOBAL_NUMBER;
    	String value = LOAN_ACCOUNT_3_GLOBAL_ID + Op.DISBURSE;
    	String actualJSON = helper.postJSONFromUI(type, by, value, data);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("clientName");
        jsonAssert.assertEqual("clientNumber");
        jsonAssert.assertEqual("savingsDisplayName");
        jsonAssert.assertEqual("disbursementDate");
        jsonAssert.assertEqual("disbursementAmount");
        jsonAssert.assertEqual("disbursementMadeBy");
        jsonAssert.assertEqual("outstandingBeforeDisbursement");
        jsonAssert.assertEqual("outstandingAfterDisbursement");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void savingsDepositWithdrawByGlobalNum() throws Exception {
        String data = "?amount=100&trxnDate="+TODAY+"&paymentTypeId=1&receiptId=12&receiptDate="+TODAY;
        String type = Type.SAVINGS;
        String by = By.GLOBAL_NUMBER;

        // deposit
        String value = SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID + Op.DEPOSIT;
        verifySavingsTrxn(data, type, by, value);

        // withdraw
        data = "?amount=69&trxnDate="+TODAY+"&paymentTypeId=1";
        value = SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID + Op.WITHDRAW;
        verifySavingsTrxn(data, type, by, value);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void createSavingsAccount() throws Exception {
        String url = "?globalCustomerNum="+ CLIENT_GLOBAL_ID +"&productId=" + SAVINGS_PRODUCT;
        String type = Type.SAVINGS;
        String value = Op.CREATE;
                
        String actualJSON = helper.postJSONFromUI(type, value, url);
        String expectedJSON = helper.getJSONFromDataSet(type, value);
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("customerName");
        jsonAssert.assertEqual("productName");
        jsonAssert.assertEqual("interesRate");
        jsonAssert.assertEqual("interestRatePeriod");
        jsonAssert.assertEqual("recommendedAmount");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void createNewClient() throws Exception {
        String url = Op.CREATE + "-response";
        String request = helper.getJSONFromDataSet(Type.CLIENT, Op.CREATE);
        String actualJSON = helper.postJSONFromUI("client/create.json", request);
        String expectedJSON = helper.getJSONFromDataSet(Type.CLIENT, url);
        checkCustomer(actualJSON, expectedJSON);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void createNewCenter() throws Exception {
        String url = Op.CREATE + "-response";
        String request = helper.getJSONFromDataSet(Type.CENTER, Op.CREATE);
        String actualJSON = helper.postJSONFromUI("center/create.json", request);
        String expectedJSON = helper.getJSONFromDataSet(Type.CENTER, url);
        checkCustomer(actualJSON, expectedJSON);
        AssertJSON jsonAssert =new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("mfiDate");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(dependsOnGroups="readOnly")
    public void createNewGroup() throws Exception {
        String url = Op.CREATE + "-response";
        String request = helper.getJSONFromDataSet(Type.GROUP, Op.CREATE);
        String actualJSON = helper.postJSONFromUI("group/create.json", request);
        String expectedJSON = helper.getJSONFromDataSet(Type.GROUP, url);
        checkCustomer(actualJSON, expectedJSON);
    }
    
    private void checkCustomer(String actualJSON, String expectedJSON) throws Exception {
        AssertJSON jsonAssert = new AssertJSON(actualJSON, expectedJSON);
        jsonAssert.assertEqual("phone");
        jsonAssert.assertEqual("dispalyName");
        jsonAssert.assertEqual("externalId");
        jsonAssert.assertEqual("loanOfficer");
        jsonAssert.assertEqual("address");
        jsonAssert.assertEqual("status");
        jsonAssert.assertEqual("state");
        jsonAssert.assertEqual("postal code");
        jsonAssert.assertEqual("country");
        jsonAssert.assertEqual("city");
    }

    private void verifySavingsTrxn(String data, String type, String by, String value) throws Exception {
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
        @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "unchecked" })
        public AssertJSON(String actualJSONString, String expectedJSONString) throws Exception {
            ObjectMapper mapper = helper.getObjectMapper();
            actualJSON = mapper.readValue(actualJSONString, Map.class);
            expectedJSON = mapper.readValue(expectedJSONString, Map.class);
        }

        public void assertEqual(String property) {
            Assert.assertEquals(expectedJSON.get(property), actualJSON.get(property));
        }
        
        public void assertLoanRepaymentResponse(){
            this.assertEqual("clientName");
            this.assertEqual("clientNumber");
            this.assertEqual("savingsDisplayName");
            this.assertEqual("paymentDate");
            this.assertEqual("paymentAmount");
            this.assertEqual("paymentMadeBy");
            this.assertEqual("outstandingBeforePayment");
            this.assertEqual("outstandingAfterPayment");
        }
    }

    class AssertJSONList {
        List<Map> actualJSON;
        List<Map> expectedJSON;
        @SuppressWarnings("PMD.SignatureDeclareThrowsException")
        public AssertJSONList(String actualJSONString, String expectedJSONString) throws Exception {
            ObjectMapper mapper = helper.getObjectMapper();
            actualJSON = mapper.readValue(actualJSONString, List.class);
            expectedJSON = mapper.readValue(expectedJSONString, List.class);
            Assert.assertEquals(expectedJSON.size(), actualJSON.size());
        }

        public void assertEqual(String property) {
            for (int i = 0; i < expectedJSON.size(); i++) {
                Assert.assertEquals(expectedJSON.get(i).get(property), actualJSON.get(i).get(property));
            }
        }
    }
}
