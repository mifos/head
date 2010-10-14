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
package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class QuestionGroupDetailPage extends MifosPage {

    public QuestionGroupDetailPage(Selenium selenium) {
        super(selenium);
    }

    public QuestionGroupDetailPage verifyPage() {
        verifyPage("view_question_groups_details");
        return this;
    }

    public String getTitle() {
        return selenium.getEval("window.document.getElementById('questionGroup.title').innerHTML").trim();
    }

    public String getAppliesTo() {
        String appliesToStr = selenium.getEval("window.document.getElementById('questionGroup.appliesTo').innerHTML");
        String appliesToTrimmed = appliesToStr.substring(appliesToStr.indexOf(':') + 1).trim();
        return appliesToTrimmed.substring(0, appliesToTrimmed.length()-1);
    }

    public List<String> getSections() {
        int numSections = Integer.valueOf(selenium.getEval("window.document.getElementById('questionGroup.sections').getElementsByTagName('table').length"));
        List<String> sections = new ArrayList<String>();
        for (int i=0; i<numSections; i++) {
            String tableId = selenium.getEval("window.document.getElementById('questionGroup.sections').getElementsByTagName('table')[" + i + "].id");
            sections.add(tableId.substring(tableId.lastIndexOf('.') + 1).trim());
        }
        return sections;
    }

    public List<String> getSectionsQuestions(String sectionName) {
        List<String> questions = new ArrayList<String>();
        int numQuestions = Integer.valueOf(selenium.getEval("window.document.getElementById('sections.table." + sectionName + "').rows.length"));
        for (int i=1; i<numQuestions; i++) {
            questions.add(selenium.getTable(format("sections.table.%s.%d.0", sectionName, i)).trim());
        }
        return questions;
    }

    public List<String> getMandatoryQuestions(String sectionName) {
        List<String> questions = new ArrayList<String>();
        int numQuestions = Integer.valueOf(selenium.getEval("window.document.getElementById('sections.table." + sectionName + "').rows.length"));
        for (int i = 1; i < numQuestions; i++) {
            if("Yes".equals(selenium.getTable(format("sections.table.%s.%d.1", sectionName, i)).trim())){
                questions.add(selenium.getTable(format("sections.table.%s.%d.0", sectionName, i)).trim());
            }
        }
        return questions;
    }

    public EditQuestionGroupPage navigateToEditPage() {
        selenium.click("link=Edit");
        waitForPageToLoad();
        return new EditQuestionGroupPage(selenium);
    }

    public ViewAllQuestionGroupsPage navigateToViewQuestionGroupsPage() {
        selenium.click("link=View Question Groups");
        waitForPageToLoad();
        return new ViewAllQuestionGroupsPage(selenium);
    }
}
