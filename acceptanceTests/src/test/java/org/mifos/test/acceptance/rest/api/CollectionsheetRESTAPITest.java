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
public class CollectionsheetRESTAPITest extends UiTestCaseBase {

    public static final String CLIENT_GLOBAL_ID = "0002-000000003";
    public static final String GROUP_GLOBAL_ID = "0002-000000002";
    public static final String CENTER_GLOBAL_ID = "0002-000000001";
    public static final String CENTER_ID = "1";
    public static final String PERSONNEL_CURRENT_ID = "current";
    public static final String SYSTEM_INFORMATION_ID = "information";
    public static final String LOAN_ACCOUNT_GLOBAL_ID = "000100000000004";
    public static final String SAVINGS_VOLUNTARY_ACCOUNT_GLOBAL_ID = "000100000000006";
    public static final String SAVINGS_MANDATORY_ACCOUNT_GLOBAL_ID = "000100000000007";

    private RESTAPITestHelper helper;

    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeClass
    public void setUp() throws Exception {
        super.setUp();
        //Given
        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "REST_API_20110912_dbunit.xml", dataSource, selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2011, 10, 10, 13, 0, 0, 0));
        helper = new RESTAPITestHelper(selenium);
        helper.navigateToJsonAjaxPage();
    }

    @AfterClass
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test
    public void verifyCollectionSheet() throws Exception {
        String type = Type.CENTER_COLLECTIONSHEET;
        String by = By.ID;
        String value = CENTER_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        ObjectMapper mapper = helper.getObjectMapper();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));

        String saveCollectionsheetJSON = getTestSaveCollectionsheetJSON();

        actualJSON = helper.postJSONFromUI("collectionsheet/save.json", saveCollectionsheetJSON);
        expectedJSON = getSumitCollectionSheetResponse();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));

        actualJSON = helper.getJSONFromUI(type, by, value);
        expectedJSON = getEmptyCollectionsheet();
        Assert.assertEquals(mapper.readTree(expectedJSON), mapper.readTree(actualJSON));

        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2011, 9, 13, 13, 0, 0, 0));
    }

    private String getSumitCollectionSheetResponse() {
        return "{\"invalidCollectionSheet\":[],\"errors\":\"\"}";
    }
    private String getTestSaveCollectionsheetJSON() {
        return "{\"json\":" +
                "\"{\\\"userId\\\":1,\\\"transactionDate\\\":[2011,10,10],\\\"paymentType\\\":1," +
                "\\\"receiptId\\\":\\\"\\\",\\\"receiptDate\\\":null,\\\"saveCollectionSheetCustomers\\\":[{\\\"customerId\\\":1," +
                "\\\"parentCustomerId\\\":null,\\\"attendanceId\\\":null,\\\"saveCollectionSheetCustomerSavings\\\":[]," +
                "\\\"saveCollectionSheetCustomerIndividualSavings\\\":[],\\\"saveCollectionSheetCustomerLoans\\\":[]," +
                "\\\"saveCollectionSheetCustomerAccount\\\":{\\\"accountId\\\":1,\\\"currencyId\\\":2," +
                "\\\"totalCustomerAccountCollectionFee\\\":\\\"0\\\"}},{\\\"customerId\\\":2,\\\"parentCustomerId\\\":1," +
                "\\\"attendanceId\\\":null,\\\"saveCollectionSheetCustomerSavings\\\":[]," +
                "\\\"saveCollectionSheetCustomerIndividualSavings\\\":[],\\\"saveCollectionSheetCustomerLoans\\\":[]," +
                "\\\"saveCollectionSheetCustomerAccount\\\":{\\\"accountId\\\":2,\\\"currencyId\\\":2," +
                "\\\"totalCustomerAccountCollectionFee\\\":\\\"0\\\"}},{\\\"customerId\\\":3,\\\"parentCustomerId\\\":2," +
                "\\\"attendanceId\\\":1,\\\"saveCollectionSheetCustomerSavings\\\":[{\\\"accountId\\\":7,\\\"currencyId\\\":2," +
                "\\\"totalDeposit\\\":\\\"500\\\",\\\"totalWithdrawal\\\":\\\"0\\\"},{\\\"accountId\\\":6,\\\"currencyId\\\":2," +
                "\\\"totalDeposit\\\":\\\"100\\\",\\\"totalWithdrawal\\\":\\\"0\\\"}]," +
                "\\\"saveCollectionSheetCustomerIndividualSavings\\\":[],\\\"saveCollectionSheetCustomerLoans\\\"" +
                ":[{\\\"accountId\\\":4,\\\"currencyId\\\":2,\\\"totalDisbursement\\\":0,\\\"totalLoanPayment\\\":\\\"40\\\"}," +
                "{\\\"accountId\\\":5,\\\"currencyId\\\":2,\\\"totalDisbursement\\\":0,\\\"totalLoanPayment\\\":\\\"412\\\"}," +
                "{\\\"accountId\\\":8,\\\"currencyId\\\":2,\\\"totalDisbursement\\\":\\\"100\\\",\\\"totalLoanPayment\\\":0}]," +
                "\\\"saveCollectionSheetCustomerAccount\\\":{\\\"accountId\\\":3,\\\"currencyId\\\":2," +
                "\\\"totalCustomerAccountCollectionFee\\\":\\\"0\\\"}}]}\"}";
    }

    private String getEmptyCollectionsheet() {
        return "{\"date\":[2011,10,10],\"collectionSheetCustomer\":[{\"name\":\"Client-REST\"," +
                "\"levelId\":3,\"branchId\":2,\"customerId\":1,\"searchId\":\"1.1\",\"parentCustomerId\":null," +
                "\"attendanceId\":null,\"collectionSheetCustomerSaving\":[],\"collectionSheetCustomerLoan\":[]," +
                "\"collectionSheetCustomerAccount\":{\"accountId\":1,\"currencyId\":2,\"totalCustomerAccountCollectionFee" +
                "\":0},\"individualSavingAccounts\":[]},{\"name\":\"Group-REST\",\"levelId\":2,\"branchId\":2," +
                "\"customerId\":2,\"searchId\":\"1.1.1\",\"parentCustomerId\":1,\"attendanceId\":null," +
                "\"collectionSheetCustomerSaving\":[],\"collectionSheetCustomerLoan\":[],\"collectionSheetCustomerAccount" +
                "\":{\"accountId\":2,\"currencyId\":2,\"totalCustomerAccountCollectionFee\":0},\"individualSavingAccounts" +
                "\":[]},{\"name\":\"Client REST API\",\"levelId\":1,\"branchId\":2,\"customerId\":3,\"searchId\":\"1.1.1.1\"," +
                "\"parentCustomerId\":2,\"attendanceId\":1,\"collectionSheetCustomerSaving\":[{\"customerId\":3,\"accountId" +
                "\":6,\"currencyId\":2,\"productId\":3,\"productShortName\":\"SA\",\"recommendedAmountUnitId\":2," +
                "\"totalDepositAmount\":0,\"depositDue\":0,\"depositPaid\":0},{\"customerId\":3,\"accountId\":7,\"currencyId" +
                "\":2,\"productId\":4,\"productShortName\":\"SB\",\"recommendedAmountUnitId\":2,\"totalDepositAmount" +
                "\":0,\"depositDue\":0,\"depositPaid\":0}],\"collectionSheetCustomerLoan\":[{\"customerId\":3,\"accountId" +
                "\":4,\"payInterestAtDisbursement\":0,\"currencyId\":2,\"productId\":1,\"accountStateId\":5,\"productShortName\":" +
                "\"RLPA\",\"totalDisbursement\":0,\"totalRepaymentDue\":0,\"amountDueAtDisbursement\":0,\"disbursalAccount" +
                "\":false},{\"customerId\":3,\"accountId\":5,\"payInterestAtDisbursement\":0,\"currencyId\":2,\"productId\":2," +
                "\"accountStateId\":5,\"productShortName\":\"RLPB\",\"totalDisbursement\":0,\"totalRepaymentDue\":0," +
                "\"amountDueAtDisbursement\":0,\"disbursalAccount\":false},{\"customerId\":3,\"accountId\":8,\"payInterestAtDisbursement\":0,\"currencyId\":2,\"productId\":1," +
                "\"accountStateId\":5,\"productShortName\":\"RLPA\",\"totalDisbursement\":0,\"totalRepaymentDue\":0," +
                "\"amountDueAtDisbursement\":0,\"disbursalAccount\":false}],\"collectionSheetCustomerAccount\":{\"accountId" +
                "\":3,\"currencyId\":2,\"totalCustomerAccountCollectionFee\":0},\"individualSavingAccounts\":[]}]}";
    }
}
