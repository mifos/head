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
package org.mifos.platform.questionnaire.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.service.QuestionDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionForm implements Serializable {
    private static final long serialVersionUID = 4707282409987816335L;
    private String name;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void trimName() {
        name = StringUtils.trim(name);
    }

    public List<QuestionDetail> getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(List<QuestionDetail> questionDetails) {
        this.questionDetails = questionDetails;
    }
}
