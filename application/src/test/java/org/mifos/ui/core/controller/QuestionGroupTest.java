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
package org.mifos.ui.core.controller;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionGroupTest {
    @Test
    public void testAddCurrentSection() {
        QuestionGroup questionGroup = new QuestionGroup();
        String title = "title";
        questionGroup.setTitle(title);
        String sectionName = "sectionName";
        questionGroup.setSectionName(sectionName);
        questionGroup.addCurrentSection();
        assertThat(questionGroup.getSections().size(), is(1));
        String nameOfAddedSection = questionGroup.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is(sectionName));
        Assert.assertNotSame(nameOfAddedSection, questionGroup.getSectionName());
    }
    @Test
    public void testAddCurrentSectionWhenSectionNameIsNotProvided() {
        QuestionGroup questionGroup = new QuestionGroup();
        String title = "title";
        questionGroup.setTitle(title);
        questionGroup.addCurrentSection();
        assertThat(questionGroup.getSections().size(), is(1));
        String nameOfAddedSection = questionGroup.getSections().get(0).getName();
        assertThat(nameOfAddedSection, is("Misc"));
        Assert.assertNotSame(nameOfAddedSection, questionGroup.getSectionName());
    }
}
