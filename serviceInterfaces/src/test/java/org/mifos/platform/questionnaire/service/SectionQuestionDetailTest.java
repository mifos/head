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

package org.mifos.platform.questionnaire.service;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*; // NOPMD
import static org.junit.Assert.assertThat;

public class SectionQuestionDetailTest {
    
    @Test
    public void shouldGenerateValuesArrayFromSelections() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail();
        sectionQuestionDetail.setSelections(asList(getSelectionDetail("Ch1", "Tag2"), getSelectionDetail("Ch2", "Tag3")));
        String[] valuesAsArray = sectionQuestionDetail.getValues();
        assertThat(valuesAsArray, is(notNullValue()));
        assertThat(valuesAsArray.length, is(2));
        assertThat(valuesAsArray[0], is("Ch1:Tag2"));
        assertThat(valuesAsArray[1], is("Ch2:Tag3"));
    }

    @Test
    public void shouldSetSelectionsFromValuesArray() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail();
        sectionQuestionDetail.setValues(new String[] {"Ch1:Tag2", "Ch2:Tag3", "Ch3"});
        List<SelectionDetail> selections = sectionQuestionDetail.getSelections();
        assertThat(selections, is(notNullValue()));
        assertThat(selections.size(), is(3));
        assertSelection(selections.get(0), "Ch1", "Tag2");
        assertSelection(selections.get(1), "Ch2", "Tag3");
        assertSelection(selections.get(2), "Ch3", null);
    }
    
    @Test
    public void shouldMultiSelectValue() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail();
        sectionQuestionDetail.setSelections(asList(getSelectionDetail("Ch1", "Tag2"), getSelectionDetail("Ch2", "Tag3")));
        assertThat(sectionQuestionDetail.getMultiSelectValue(), is("Ch1:Tag2, Ch2:Tag3"));
    }

    @Test
    public void shouldGetAnswerStringForSelections() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail();
        sectionQuestionDetail.setQuestionDetail(new QuestionDetail("Text", QuestionType.SMART_SELECT));
        sectionQuestionDetail.setSelections(asList(getSelectionDetail("Ch1", "Tag2"), getSelectionDetail("Ch2", "Tag3")));
        assertThat(sectionQuestionDetail.getAnswer(), is("Ch1:Tag2, Ch2:Tag3"));
    }

    @Test
    public void shouldGetAnswerStringForSingleResponse() {
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail();
        sectionQuestionDetail.setQuestionDetail(new QuestionDetail("Text", QuestionType.FREETEXT));
        sectionQuestionDetail.setValue("Response");
        assertThat(sectionQuestionDetail.getAnswer(), is("Response"));
        sectionQuestionDetail.setValue(null);
        assertThat(sectionQuestionDetail.getAnswer(), is(""));
    }

    private void assertSelection(SelectionDetail selectionDetail, String choice, String tag) {
        assertThat(selectionDetail.getSelectedChoice(), is(choice));
        if (tag == null) {
            assertThat(selectionDetail.getSelectedTag(), is(nullValue()));
        } else {
            assertThat(selectionDetail.getSelectedTag(), is(tag));
        }
    }

    private SelectionDetail getSelectionDetail(String selectedChoice, String selectedTag) {
        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setSelectedChoice(selectedChoice);
        selectionDetail.setSelectedTag(selectedTag);
        return selectionDetail;
    }
}
