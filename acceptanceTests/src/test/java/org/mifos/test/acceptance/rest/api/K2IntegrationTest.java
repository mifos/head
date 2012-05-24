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

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.rest.api.K2IntegrationTestHelper.Parameter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class K2IntegrationTest extends UiTestCaseBase {

    private K2IntegrationTestHelper helper;

    public static final String LOAN_ACCOUNT_GLOBAL_ID = "000100000000004";
    public static final String SAVINGS_ACCOUNT_GLOBAL_ID = "000100000000007";
    public static final String CURRENCY = "INR";
    public static final String PAYMENT_TYPE_NAME = "Cash"; // TODO: add to REST_API_20110912_dbunit.xml better payment
                                                           // type (e.g. 'M-PESA')
    public static final String AMOUNT = "11.3";

    public static final String INVALID_LOAN_ACCOUNT_GLOBAL_ID = "000100000000114";
    public static final String INVALID_CURRENCY = "USD";

    @Override
    @BeforeClass
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void setUp() throws Exception {
        super.setUp();
        // Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        DateTime targetTime = new DateTime(2011, 9, 13, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "REST_API_20110912_dbunit.xml", dataSource, selenium);

        helper = new K2IntegrationTestHelper(selenium);
        helper.navigateToJsonAjaxPage();
    }

    @Test
    public void testSavingsDepositAccepted() throws InterruptedException, JsonProcessingException, IOException {
        String acNo = Parameter.getAcNoParameter(SAVINGS_ACCOUNT_GLOBAL_ID);
        String currency = Parameter.getCurrencyParameter(CURRENCY);
        String mmSystemId = Parameter.getMmSystemIdParameter(PAYMENT_TYPE_NAME);
        String amount = Parameter.getAmountParameter(AMOUNT);
        String defaultData = helper.constructDefaultRequestData();
        String data = helper.constructRequestData(defaultData, acNo, currency, mmSystemId, amount);

        String response = helper.postBasicJSONFromUI(data);
        helper.assertResponseAccepted(response);
    }

    @Test
    public void testSavingsDepositInvalidPayment() throws InterruptedException, JsonProcessingException, IOException {
        String acNo = Parameter.getAcNoParameter(SAVINGS_ACCOUNT_GLOBAL_ID);
        String currency = Parameter.getCurrencyParameter(INVALID_CURRENCY);
        String mmSystemId = Parameter.getMmSystemIdParameter(PAYMENT_TYPE_NAME);
        String amount = Parameter.getAmountParameter(AMOUNT);
        String defaultData = helper.constructDefaultRequestData();
        String data = helper.constructRequestData(defaultData, acNo, currency, mmSystemId, amount);

        String response = helper.postBasicJSONFromUI(data);
        helper.assertResponseInvalidPayment(response);
    }

    @Test
    public void testLoanPaymentAccepted() throws InterruptedException, JsonProcessingException, IOException {
        String acNo = Parameter.getAcNoParameter(LOAN_ACCOUNT_GLOBAL_ID);
        String currency = Parameter.getCurrencyParameter(CURRENCY);
        String mmSystemId = Parameter.getMmSystemIdParameter(PAYMENT_TYPE_NAME);
        String amount = Parameter.getAmountParameter(AMOUNT);
        String defaultData = helper.constructDefaultRequestData();
        String data = helper.constructRequestData(defaultData, acNo, currency, mmSystemId, amount);

        String response = helper.postBasicJSONFromUI(data);
        helper.assertResponseAccepted(response);
    }

    @Test
    public void testLoanPaymentAccountNotFound() throws InterruptedException, JsonProcessingException, IOException {
        String acNo = Parameter.getAcNoParameter(INVALID_LOAN_ACCOUNT_GLOBAL_ID);
        String currency = Parameter.getCurrencyParameter(CURRENCY);
        String mmSystemId = Parameter.getMmSystemIdParameter(PAYMENT_TYPE_NAME);
        String amount = Parameter.getAmountParameter(AMOUNT);
        String defaultData = helper.constructDefaultRequestData();
        String data = helper.constructRequestData(defaultData, acNo, currency, mmSystemId, amount);

        String response = helper.postBasicJSONFromUI(data);
        helper.assertResponseAccountNotFound(response);
    }

    @AfterClass
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
}
