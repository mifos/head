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
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.springframework.core.io.ClassPathResource;

import com.thoughtworks.selenium.Selenium;

public class RESTAPITestHelper {

    private final Selenium selenium;

    public static class Type {
        public static final String CLIENT = "client";
    }

    public static class By {
        public static final String GLOBAL_NUMBER = "num";
    }

    public RESTAPITestHelper(Selenium selenium) {
        this.selenium = selenium;
    }

    public void navigateToJsonAjaxPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.loginSuccessfullyUsingDefaultCredentials();
        selenium.open("jsonAjax.ftl");
    }

    public String getJSONFromUI(String type, String by, String value) throws InterruptedException {
        String url = String.format("%s/%s-%s.json", type, by, value);
        selenium.type("resturl", url);
        selenium.click("getJSON");
        Thread.sleep(1000);
        return selenium.getText("restdata");
    }

    public String getJSONFromDataSet(String type, String by, String value) throws IOException {
        String path = String.format("/dataSets/rest/%s-%s-%s.json", type, by, value);
        ClassPathResource resource = new ClassPathResource(path);
        File file = resource.getFile();
        if (file == null) {
            throw new FileNotFoundException("Couldn't find file");
        }
        return FileUtil.readAsString(file);
    }



}
