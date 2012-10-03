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

import junit.framework.Assert;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.login.LoginPage;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.UseStringBufferForStringAppends")
public class K2IntegrationTestHelper {

    private final Selenium selenium;
    private final ObjectMapper objectMapper;
    
    private static final String DEFAULT_PASSWORD = "testmifos";
    private static final String DEFAULT_USERNAME = "mifos";

    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    public static class Parameter {
        public static final String K2_ACCOUNT_ID = "k2_account_id";
        public static final String K2_TRANSACTION_ID = "k2_transaction_id";
        public static final String MM_SYSTEM_ID = "mm_system_id";
        public static final String MP_TRANSACTION_ID = "mp_transaction_id";
        public static final String BILLER_NUMBER = "biller_number";
        public static final String TRANSACTION_DATE = "transaction_date";
        public static final String TRANSACTION_TIME = "transaction_time";
        public static final String TRANSACTION_TYPE = "transaction_type";
        public static final String AC_NO = "ac_no";
        public static final String SENDER = "sender";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String AMOUNT = "amount";
        public static final String CURRENCY = "currency";
        
        public static String getK2AccountIdParameter(String value) {
            return K2_ACCOUNT_ID + "=" + value;
        }
        public static String getK2TransactionIdParameter(String value) {
            return K2_TRANSACTION_ID + "=" + value;
        }
        public static String getMmSystemIdParameter(String value) {
            return MM_SYSTEM_ID + "=" + value;
        }
        public static String getMpTransactionIdParameter(String value) {
            return MP_TRANSACTION_ID + "=" + value;
        }
        public static String getBillerNumberParameter(String value) {
            return BILLER_NUMBER + "=" + value;
        }
        public static String getTransactionDateParameter(String value) {
            return TRANSACTION_DATE + "=" + value;
        }
        public static String getTransactionTimeParameter(String value) {
            return TRANSACTION_TIME + "=" + value;
        }
        public static String getTransactionTypeParameter(String value) {
            return TRANSACTION_TYPE + "=" + value;
        }
        public static String getAcNoParameter(String value) {
            return AC_NO + "=" + value;
        }
        public static String getSenderParameter(String value) {
            return SENDER + "=" + value;
        }
        public static String getFirstNameParameter(String value) {
            return FIRST_NAME + "=" + value;
        }
        public static String getLastNameParameter(String value) {
            return LAST_NAME + "=" + value;
        }
        public static String getMiddleNameParameter(String value) {
            return MIDDLE_NAME + "=" + value;
        }
        public static String getAmountParameter(String value) {
            return AMOUNT + "=" + value;
        }
        public static String getCurrencyParameter(String value) {
            return CURRENCY + "=" + value;
        }
        
    }
    
    public static class Response {
        public static final String ACCEPTED = "{ \"status\": \"01\", \"description\":\"Accepted\" }";
        public static final String ACCOUNT_NOT_FOUND = "{ \"status\":\"02\", \"description\": \"Account not Found\" }";
        public static final String INVALID_PAYMENT = "{ \"status\":\"03\", \"description\": \"Invalid Payment\" }";
    }
    
    private static final String DEFAULT_K2_ACCOUNT_ID = "12";
    private static final String DEFAULT_K2_TRANSACTION_ID = "15";
    private static final String DEFAULT_MP_TRANSACTION_ID = "54445";
    private static final String DEFAULT_BILLER_NUMBER = "888555";
    private static final String DEFAULT_TRANSACTION_DATE = "26%2F9%2F11";
    private static final String DEFAULT_TRANSACTION_TIME = "1%3A03%20PM";
    private static final String DEFAULT_TRANSACTION_TYPE = "Paybill";
    private static final String DEFAULT_SENDER = "0723456789";
    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String DEFAULT_MIDDLE_NAME = "K.";
    private static final String DEFAULT_LAST_NAME = "Doe";
    
    private static final String DEFAULT_K2_ACCOUNT_ID_PARAMETER = Parameter.getK2AccountIdParameter(DEFAULT_K2_ACCOUNT_ID);
    private static final String DEFAULT_K2_TRANSACTION_ID_PARAMETER = Parameter.getK2TransactionIdParameter(DEFAULT_K2_TRANSACTION_ID);
    private static final String DEFAULT_MP_TRANSACTION_ID_PARAMETER = Parameter.getMpTransactionIdParameter(DEFAULT_MP_TRANSACTION_ID);
    private static final String DEFAULT_BILLER_NUMBER_PARAMETER = Parameter.getBillerNumberParameter(DEFAULT_BILLER_NUMBER);
    private static final String DEFAULT_TRANSACTION_DATE_PARAMETER = Parameter.getTransactionDateParameter(DEFAULT_TRANSACTION_DATE);
    private static final String DEFAULT_TRANSACTION_TIME_PARAMETER = Parameter.getTransactionTimeParameter(DEFAULT_TRANSACTION_TIME);
    private static final String DEFAULT_TRANSACTION_TYPE_PARAMETER = Parameter.getTransactionTypeParameter(DEFAULT_TRANSACTION_TYPE);
    private static final String DEFAULT_SENDER_PARAMETER = Parameter.getSenderParameter(DEFAULT_SENDER);
    private static final String DEFAULT_FIRST_NAME_PARAMETER = Parameter.getFirstNameParameter(DEFAULT_FIRST_NAME);
    private static final String DEFAULT_MIDDLE_NAME_PARAMETER = Parameter.getMiddleNameParameter(DEFAULT_MIDDLE_NAME);
    private static final String DEFAULT_LAST_NAME_PARAMETER = Parameter.getLastNameParameter(DEFAULT_LAST_NAME);
    
    public static final String URL = "basic/k2/processTransaction.json";

    public static final String RESTURL_FIELD_ID = "resturl";
    public static final String USERNAME_FIELD_ID = "username";
    public static final String PASSWORD_FIELD_ID = "password";
    public static final String RESTDATA_DIV_ID = "restdata";
    public static final String POST_DATA_BASIC_BUTTON_ID = "postDataBasic";
    
    public K2IntegrationTestHelper(Selenium selenium) {
        super();
        this.selenium = selenium;
        
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
    }

    public void navigateToJsonAjaxPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.loginSuccessfullyUsingDefaultCredentials();
        selenium.open("jsonAjax.ftl");
    }

    public String postBasicJSONFromUI(String data) throws InterruptedException  {
        String requestUrl = URL + "?" + data;
        selenium.type(RESTURL_FIELD_ID, requestUrl);
        selenium.type(USERNAME_FIELD_ID, DEFAULT_USERNAME);
        selenium.type(PASSWORD_FIELD_ID, DEFAULT_PASSWORD);
        selenium.click(POST_DATA_BASIC_BUTTON_ID);
        Thread.sleep(1000);
        return selenium.getText(RESTDATA_DIV_ID);
    }

    public String constructDefaultRequestData() {
        return constructRequestData(DEFAULT_K2_ACCOUNT_ID_PARAMETER, DEFAULT_K2_TRANSACTION_ID_PARAMETER, DEFAULT_MP_TRANSACTION_ID_PARAMETER,
                DEFAULT_BILLER_NUMBER_PARAMETER, DEFAULT_TRANSACTION_DATE_PARAMETER, DEFAULT_TRANSACTION_TIME_PARAMETER, DEFAULT_TRANSACTION_TYPE_PARAMETER,
                DEFAULT_SENDER_PARAMETER, DEFAULT_FIRST_NAME_PARAMETER, DEFAULT_MIDDLE_NAME_PARAMETER, DEFAULT_LAST_NAME_PARAMETER);
    }

    public String constructRequestData(String... params) {
        String resultParams = "";
        for (int i = 0; i < params.length; i++) {
            resultParams += params[i] + "&";
        }
        return resultParams;
    }
    
    public void assertResponseAccepted(String response) throws JsonProcessingException, IOException{
        Assert.assertEquals(objectMapper.readTree(Response.ACCEPTED), objectMapper.readTree(response));
    }
    
    public void assertResponseInvalidPayment(String response) throws JsonProcessingException, IOException{
        Assert.assertEquals(objectMapper.readTree(Response.INVALID_PAYMENT), objectMapper.readTree(response));
    }
    
    public void assertResponseAccountNotFound(String response) throws JsonProcessingException, IOException{
        Assert.assertEquals(objectMapper.readTree(Response.ACCOUNT_NOT_FOUND), objectMapper.readTree(response));
    }
    
}
