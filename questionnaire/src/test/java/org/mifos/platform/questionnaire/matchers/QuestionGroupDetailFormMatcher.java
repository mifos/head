/*
 * Copyright (c) 2005-2011 Grameen Foundation USA

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
package org.mifos.platform.questionnaire.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.mifos.platform.questionnaire.ui.model.QuestionGroupForm;
import org.mifos.platform.questionnaire.ui.model.SectionDetailForm;

@SuppressWarnings("PMD")
public class QuestionGroupDetailFormMatcher extends TypeSafeMatcher<QuestionGroupForm> {
    private QuestionGroupForm questionGroupForm;

    public QuestionGroupDetailFormMatcher(QuestionGroupForm questionGroupForm) {
        this.questionGroupForm = questionGroupForm;
    }

    @Override
    public boolean matchesSafely(QuestionGroupForm questionGroupForm) {
        if (StringUtils.equalsIgnoreCase(this.questionGroupForm.getTitle(), questionGroupForm.getTitle())
                && this.questionGroupForm.getSections().size() == questionGroupForm.getSections().size()) {
            for (SectionDetailForm sectionDetailForm : this.questionGroupForm.getSections()) {
                Assert.assertThat(questionGroupForm.getSections(), org.hamcrest.Matchers.hasItem(new SectionDetailFormMatcher(sectionDetailForm)));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("QuestionGroupDetailForm do not match");
    }
}
