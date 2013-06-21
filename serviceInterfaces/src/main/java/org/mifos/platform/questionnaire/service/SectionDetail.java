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
package org.mifos.platform.questionnaire.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionDetail implements Serializable {
    private static final long serialVersionUID = -7143251556021871484L;

    private Integer id;
    private String name;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<SectionQuestionDetail> questionDetails;
    private Integer sequenceNumber;

    public SectionDetail() {
        questionDetails = new ArrayList<SectionQuestionDetail>();
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addQuestion(SectionQuestionDetail sectionQuestionDetail) {
        questionDetails.add(sectionQuestionDetail);
    }

    public List<SectionQuestionDetail> getQuestions() {
        return questionDetails;
    }

    public void setQuestionDetails(List<SectionQuestionDetail> questionDetails) {
        this.questionDetails = questionDetails;
    }

    public SectionQuestionDetail getQuestionDetail(int i) {
        if (i >= this.questionDetails.size()) {
            this.questionDetails.add(new SectionQuestionDetail());
        }
        return this.questionDetails.get(i);
    }

    public boolean hasNoActiveQuestions() {
        boolean result = true;
        for (SectionQuestionDetail sectionQuestionDetail : questionDetails) {
            if (sectionQuestionDetail.isActive()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public Integer getCountOfQuestions() {
        return this.questionDetails.size();
    }

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean hasActiveQuestions() {
        return !hasNoActiveQuestions();
    }

    public List<SectionQuestionDetail> getQuestionDetails() {
        return questionDetails;
    }
}
