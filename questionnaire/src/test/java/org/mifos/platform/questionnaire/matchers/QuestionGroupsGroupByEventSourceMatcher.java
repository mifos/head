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

package org.mifos.platform.questionnaire.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD")
public class QuestionGroupsGroupByEventSourceMatcher extends TypeSafeMatcher<Map <String,List <QuestionGroupDetail>>> {
    private Map <String,List <QuestionGroupDetail>> questionGroupsEventSourceSplit;

    public QuestionGroupsGroupByEventSourceMatcher(Map <String,List <QuestionGroupDetail>> questionGroupsEventSourceSplit) {
        this.questionGroupsEventSourceSplit = questionGroupsEventSourceSplit;
    }

    @Override
    public boolean matchesSafely(Map <String,List <QuestionGroupDetail>> questionGroupsEventSourceSplit) {
        if (this.questionGroupsEventSourceSplit.size() == questionGroupsEventSourceSplit.size()) {
            for (String eventSource : this.questionGroupsEventSourceSplit.keySet()) {
                if(this.questionGroupsEventSourceSplit.containsKey(eventSource)) {
                    Assert.assertThat(questionGroupsEventSourceSplit.get(eventSource), new QuestionGroupDetailListMatcher(this.questionGroupsEventSourceSplit.get(eventSource)));
                }
                else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("List of question group details do not match");
    }
}
