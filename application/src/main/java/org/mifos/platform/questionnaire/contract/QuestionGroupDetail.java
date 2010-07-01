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

package org.mifos.platform.questionnaire.contract;

import java.util.List;

public class QuestionGroupDetail {
    private Integer id;
    private String title;
    private List<SectionDefinition> sectionDefinitions;

    public QuestionGroupDetail(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public QuestionGroupDetail(String title) {
        this(0, title);
    }

    public QuestionGroupDetail(int id, String title, List<SectionDefinition> sectionDefinitions) {
        this(id, title);
        this.sectionDefinitions = sectionDefinitions;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public List<SectionDefinition> getSectionDefinitions() {
        return sectionDefinitions;
    }

}
