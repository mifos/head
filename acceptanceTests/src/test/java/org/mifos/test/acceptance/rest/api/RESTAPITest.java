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
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "rest", "acceptance"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class RESTAPITest extends UiTestCaseBase {

    public static final String CLIENT_GLOBAL_ID = "0002-000000003";
    public static final String LOAN_ACCOUNT_GLOBAL_ID = "000100000000004";
    public static final String SAVINGS_ACCOUNT_GLOBAL_ID = "000100000000006";

    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

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
    public void clientByGlobalNumJSON() throws Exception {
        String type = Type.CLIENT;
        String by = By.GLOBAL_NUMBER;
        String value = CLIENT_GLOBAL_ID;
        String actualJSON = helper.getJSONFromUI(type, by, value);
        String expectedJSON = helper.getJSONFromDataSet(type, by, value);
        helper.assertEquals(expectedJSON, actualJSON);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void repayLoanByGlobalNumJSON() throws Exception {
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
    public void savingsDepositByGlobalNumJSON() throws Exception {
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
            ObjectMapper mapper = new ObjectMapper();
            actualJSON = mapper.readValue(actualJSONString, Map.class);
            expectedJSON = mapper.readValue(expectedJSONString, Map.class);
        }

        public void assertEqual(String property) {
            Assert.assertEquals(expectedJSON.get(property), actualJSON.get(property));
        }
    }


}
