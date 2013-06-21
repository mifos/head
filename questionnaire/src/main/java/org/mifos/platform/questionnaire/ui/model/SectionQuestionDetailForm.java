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

package org.mifos.platform.questionnaire.ui.model;

import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
@SuppressWarnings("PMD")
public class SectionQuestionDetailForm {
    private SectionQuestionDetail sectionQuestionDetail;

    public SectionQuestionDetailForm() {
        //FIXEME: why does spring crib for not having a default constructor
        //for this class, when this class is not hooked onto spring?
        //TODO: Investigate later
        super();
    }
    public SectionQuestionDetailForm(SectionQuestionDetail sectionQuestionDetail) {
        super();
        this.sectionQuestionDetail = sectionQuestionDetail;
    }

    public int getQuestionId() {
        return sectionQuestionDetail.getQuestionId();
    }

    public String getText() {
        return sectionQuestionDetail.getText();
    }

    public boolean isMandatory() {
        return sectionQuestionDetail.isMandatory();
    }

    public boolean isActive() {
        return sectionQuestionDetail.isActive();
    }

    public void setMandatory(boolean mandatory) {
        sectionQuestionDetail.setMandatory(mandatory);
    }
    
    public boolean isShowOnPage() {
    	return sectionQuestionDetail.isShowOnPage();
    }
    
    public void setShowOnPage(boolean showOnPage) {
    	sectionQuestionDetail.setShowOnPage(showOnPage);
    }
    
    public Integer getSequenceNumber() {
        return sectionQuestionDetail.getSequenceNumber();
    }
    
    public QuestionType getType(){
        return sectionQuestionDetail.getQuestionType();
    }
    public void setSequenceNumber(Integer sequenceNumber) {
        sectionQuestionDetail.setSequenceNumber(sequenceNumber);
    }
    
    public SectionQuestionDetail getSectionQuestionDetail() {
        return sectionQuestionDetail;
    }
}
