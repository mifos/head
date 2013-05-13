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
package org.mifos.platform.questionnaire.domain;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Section implements Serializable {
    //TODO: For the time being to resolve dependencies
    // should extend AbstractEntity? move AbstractEntity to common module first
    private static final long serialVersionUID = -6805203646344424230L;

    private Integer id;
    private String name;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<SectionQuestion> questions;
    private Integer sequenceNumber;

    private List<SectionLink> sectionLinks;
    
	public List<SectionLink> getSectionLinks() {
		return sectionLinks;
	}

	public void setSectionLinks(List<SectionLink> sectionLinks) {
		this.sectionLinks = sectionLinks;
	}

	@SuppressWarnings({"UnusedDeclaration", "PMD.UncommentedEmptyConstructor"})
    public Section() {
    }

    public Section(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<SectionQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SectionQuestion> questions) {
        this.questions = questions;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isNewSection() {
        return id == null || id == 0;
    }
}
