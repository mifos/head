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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.aspectj.util.FileUtil;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.springframework.core.io.ClassPathResource;

import com.thoughtworks.selenium.Selenium;

public class RESTAPITestHelper {

    private final Selenium selenium;

	private static final String LOANOFFICER_PASSWORD = "testmifos";
    private static final String LOANOFFICER_USERNAME = "loanofficer"; 
    
    public static class Type {
        public static final String CLIENT = "client";
        public static final String GROUP = "group";
        public static final String CENTER = "center";
        public static final String CENTER_COLLECTIONSHEET = "collectionsheet/customer";
        public static final String OFFICE = "office";
        public static final String CUSTOMER = "customer";
        public static final String PERSONNEL = "personnel";
        public static final String SYSTEM = "admin/system";
        public static final String PAYMENT_TYPES = "admin/payment-types";
        public static final String ACCOUNT = "account";
        public static final String LOAN = "account/loan";
        public static final String SAVINGS = "account/savings";
    }

    public static class By {
        public static final String GLOBAL_NUMBER = "num";
        public static final String ID = "id";
        public static final String MEETINGS = "meetings";
        public static final String STATE = "state";
    }

    public static class Op {
        public static final String TRXNHISTORY = "/trxnhistory";
        public static final String CHARGE = "/charge";
        public static final String CHARGES = "/charges";
        public static final String APPLICABLE_FEES = "/fees";
        public static final String REPAY = "/repay";
        public static final String SCHEDULE = "/schedule";
        public static final String INSTALLMENT = "/installment";
        public static final String FULLREPAY = "/fullrepay";
        public static final String DISBURSE = "/disburse";
        public static final String ADJUSTMENT = "/adjustment";
        public static final String CLIENTS = "/clients";
        public static final String DUE = "/due";
        public static final String DEPOSIT = "/deposit";
        public static final String WITHDRAW = "/withdraw";
    }

    public RESTAPITestHelper(Selenium selenium) {
        this.selenium = selenium;
    }

    public void navigateToJsonAjaxPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.loginSuccessfullyUsingDefaultCredentials();
        selenium.open("jsonAjax.ftl");
    }
    
    public void navigateToJsonAjaxPageAsLoanOfficer() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.loginSuccessfulAs(LOANOFFICER_USERNAME, LOANOFFICER_PASSWORD);
        selenium.open("jsonAjax.ftl"); 
    }

    public String getJSONFromUI(String type, String by, String value) throws InterruptedException {
        String url = String.format("%s/%s-%s.json", type, by, value);
        selenium.type("resturl", url);
        selenium.click("getJSON");
        Thread.sleep(1000);
        return selenium.getText("restdata");
    }

    public String postJSONFromUI(String type, String by, String value, String data) throws InterruptedException {
        String result;
        String url = String.format("%s/%s-%s.json", type, by, value);
        if(data.charAt(0) == '?') {
            // query string
            result = postJSONFromUI(url + data, "");
        } else {
            result = postJSONFromUI(url, data);
        }
        return result;
    }

    public String postJSONFromUI(String url, String data) throws InterruptedException {
        selenium.type("resturl", url);
        selenium.type("data", data);
        selenium.click("postData");
        Thread.sleep(1000);
        return selenium.getText("restdata");
    }

    public String getJSONFromDataSet(String apiType, String by, String apiValue) throws IOException {
        String type = apiType.replace('/', '-');
        String value = apiValue.replace('/', '-');
        String path = String.format("/dataSets/rest/%s-%s-%s.json", type, by, value);
        ClassPathResource resource = new ClassPathResource(path);
        File file = resource.getFile();
        if (file == null) {
            throw new FileNotFoundException("Couldn't find file");
        }
        return FileUtil.readAsString(file);
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        return mapper;
    }

}
