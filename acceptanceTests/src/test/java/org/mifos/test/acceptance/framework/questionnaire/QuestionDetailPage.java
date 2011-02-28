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
package org.mifos.test.acceptance.framework.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import java.util.Iterator;
import java.util.List;

public class QuestionDetailPage extends MifosPage {
    public QuestionDetailPage(Selenium selenium) {
        super(selenium);
    }

    public QuestionDetailPage verifyPage() {
        verifyPage("view_question_details");
        return this;
    }

    public ViewAllQuestionsPage navigateToViewAllQuestionsPage() {
        selenium.click("link=View Questions");
        waitForPageToLoad();
        return new ViewAllQuestionsPage(selenium);
    }

    public EditQuestionPage navigateToEditQuestionPage() {
        selenium.click("link=Edit Question");
        waitForPageToLoad();
        return new EditQuestionPage(selenium);
    }

    void assertForSmartChoices(List<Choice> choices) {
        for (Choice choice : choices) {
            Assert.assertTrue(isTextPresentInPage(choice.getChoiceText()));
            for (String tag : choice.getTags()) {
                Assert.assertTrue(isTextPresentInPage(tag));
            }
        }
    }

    String getCommaSeparatedChoices(List<Choice> choices) {
        StringBuilder commaSeparatedChoices = new StringBuilder();
        for (Iterator<Choice> choiceIterator = choices.iterator(); choiceIterator.hasNext();) {
            commaSeparatedChoices.append(choiceIterator.next().getChoiceText());
            if (choiceIterator.hasNext()) {
                commaSeparatedChoices.append(", ");
            }
        }
        return commaSeparatedChoices.toString();
    }

    public void assertForChoices(String type, List<Choice> choices) {
        if ("Multi Select".equals(type) || "Single Select".equals(type)) {
            Assert.assertTrue(isTextPresentInPage("Answer Choices: " + getCommaSeparatedChoices(choices)));
        } else {
            Assert.assertFalse(isTextPresentInPage("Answer Choices: "), "Answer choices should not be present");
        }
        if ("Smart Select".equals(type)) {
            assertForSmartChoices(choices);
        }
    }

    void assertForNumericDetails(String type, int numericMin, int numericMax) {
        if ("Number".equals(type)) {
            Assert.assertTrue(isTextPresentInPage("Minimum value: " + numericMin));
            Assert.assertTrue(isTextPresentInPage("Maximum value: " + numericMax));
        } else {
            Assert.assertFalse(isTextPresentInPage("Minimum value: "));
            Assert.assertFalse(isTextPresentInPage("Maximum value: "));
        }
    }

    void verifyQuestionTitle(String type, String title) {
        Assert.assertTrue(isTextPresentInPage("Question: " + title + type), "Title is missing");
        Assert.assertTrue(isTextPresentInPage("Answer Type: " + type), "Answer type is missing");
    }

    public void verifyQuestionTitle(String title) {
        Assert.assertTrue(isTextPresentInPage("Question: " + title), "Title is missing");
    }
}
