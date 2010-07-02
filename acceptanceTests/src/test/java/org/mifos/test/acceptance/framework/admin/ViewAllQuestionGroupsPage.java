/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
package org.mifos.test.acceptance.framework.admin;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;

public class ViewAllQuestionGroupsPage extends MifosPage {
    public ViewAllQuestionGroupsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewAllQuestionGroupsPage verifyPage() {
        verifyPage("view_question_groups");
        return this;
    }

    public QuestionGroupDetailPage navigateToQuestionGroupDetailPage(String title) {
        selenium.click("link="+title);
        waitForPageToLoad();
        return new QuestionGroupDetailPage(selenium);
    }

    public String[] getAllQuestionGroups() {
        int rows = Integer.valueOf(selenium.getEval("window.document.getElementById('questionGroups.table').rows.length"));
        String[] questions = new String[rows];
        for (int i=0; i<rows; i++) {
            questions[i] = selenium.getTable("questionGroups.table." + i + ".1");
        }
        return questions;
    }
}
