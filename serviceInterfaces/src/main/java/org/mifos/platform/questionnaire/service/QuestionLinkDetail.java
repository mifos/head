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

public class QuestionLinkDetail implements Serializable {
    private static final long serialVersionUID = 5240884292277900071L;

    private Integer sourceQuestionId;
    private String soureQuestionDisplay;
    private Integer affectedQuestionId;
    private String affectedQuestionDisplay;
    private String value;
    private String additionalValue;
    private String linkType;
    private String linkTypeDisplay;
    
    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public QuestionLinkDetail() {	
    }
    
	public QuestionLinkDetail(Integer sourceQuestionId,
			String soureQuestionDisplay, Integer affectedQuestionId,
			String affectedQuestionDisplay, String value,
			String additionalValue, String linkType, String linkTypeDisplay) {
		super();
		this.sourceQuestionId = sourceQuestionId;
		this.soureQuestionDisplay = soureQuestionDisplay;
		this.affectedQuestionId = affectedQuestionId;
		this.affectedQuestionDisplay = affectedQuestionDisplay;
		this.value = value;
		this.additionalValue = additionalValue;
		this.linkType = linkType;
		this.linkTypeDisplay = linkTypeDisplay;
	}
	public String getSoureQuestionDisplay() {
		return soureQuestionDisplay;
	}
	public void setSoureQuestionDisplay(String soureQuestionDisplay) {
		this.soureQuestionDisplay = soureQuestionDisplay;
	}
	public String getAffectedQuestionDisplay() {
		return affectedQuestionDisplay;
	}
	public void setAffectedQuestionDisplay(String affectedQuestionDisplay) {
		this.affectedQuestionDisplay = affectedQuestionDisplay;
	}
	public String getLinkTypeDisplay() {
		return linkTypeDisplay;
	}
	public void setLinkTypeDisplay(String linkTypeDisplay) {
		this.linkTypeDisplay = linkTypeDisplay;
	}
	public Integer getSourceQuestionId() {
		return sourceQuestionId;
	}
	public void setSourceQuestionId(Integer sourceQuestionId) {
		this.sourceQuestionId = sourceQuestionId;
	}
	public Integer getAffectedQuestionId() {
		return affectedQuestionId;
	}
	public void setAffectedQuestionId(Integer affectedQuestionId) {
		this.affectedQuestionId = affectedQuestionId;
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
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
    
}
