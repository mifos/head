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
package org.mifos.platform.questionnaire.matchers;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;

import static org.junit.Assert.assertThat;
@SuppressWarnings("PMD")
public class QuestionGroupDetailMatcher extends TypeSafeMatcher<QuestionGroupDetail> {
    private QuestionGroupDetail questionGroupDetail;

    public QuestionGroupDetailMatcher(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
    }

    @Override
    public boolean matchesSafely(QuestionGroupDetail questionGroupDetail) {
        if (StringUtils.equals(this.questionGroupDetail.getTitle(), questionGroupDetail.getTitle())) {
            for (SectionDetail sectionDetail : this.questionGroupDetail.getSectionDetails()) {
                assertThat(questionGroupDetail.getSectionDetails(), Matchers.hasItem(new QuestionGroupSectionMatcher(sectionDetail)));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("QuestionGroupDetail do not match.");
    }
}
