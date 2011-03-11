/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.test.acceptance.framework;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import java.io.File;

public class ScreenShotListener extends TestListenerAdapter {
    @Override
    public void onTestFailure(ITestResult testResult) {
        super.onTestFailure(testResult);
        if (!testResult.isSuccess()) {
            File file = new File(System.getProperty("java.io.tmpdir"));
            String fileName = file.getAbsolutePath() + System.getProperty("file.separator") + testResult.getName() + ".png";
            Reporter.log("Screen shot saved at " + fileName, 0, true);
            UiTestCaseBase.selenium.
                    captureEntirePageScreenshot(fileName, "background=#CCFFDD");
        }
    }
}
