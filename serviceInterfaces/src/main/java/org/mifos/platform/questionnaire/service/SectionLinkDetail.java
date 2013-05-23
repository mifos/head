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

package org.mifos.platform.questionnaire.service;

import java.io.Serializable;

public class SectionLinkDetail implements Serializable {
    private static final long serialVersionUID = 5240884292277900071L;

    private SectionQuestionDetail sourceQuestion;
    private SectionDetail affectedSection;
    private String value;
    private String additionalValue;
    private Integer linkType;
    private String linkTypeDisplay;
    
    public SectionQuestionDetail getSourceQuestion() {
        return sourceQuestion;
    }
    public void setSourceQuestion(SectionQuestionDetail sourceQuestion) {
        this.sourceQuestion = sourceQuestion;
    }
    public SectionDetail getAffectedSection() {
        return affectedSection;
    }
    public void setAffectedSection(SectionDetail affectedSection) {
        this.affectedSection = affectedSection;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getAdditionalValue() {
        return additionalValue;
    }
    public void setAdditionalValue(String additionalValue) {
        this.additionalValue = additionalValue;
    }
    public Integer getLinkType() {
        return linkType;
    }
    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }
    public String getLinkTypeDisplay() {
        return linkTypeDisplay;
    }
    public void setLinkTypeDisplay(String linkTypeDisplay) {
        this.linkTypeDisplay = linkTypeDisplay;
    }
}
