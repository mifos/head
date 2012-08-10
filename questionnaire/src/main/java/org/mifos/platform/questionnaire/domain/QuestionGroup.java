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

package org.mifos.platform.questionnaire.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.security.rolesandpermission.business.RoleBO;

public class QuestionGroup implements Serializable {
    //TODO: For the time being to resolve dependencies
    // should extend AbstractEntity? move AbstractEntity to common module first
    private static final long serialVersionUID = -1324084579942599901L;

    private int id;

    private String title;

    private Date dateOfCreation;

    private QuestionGroupState state;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<Section> sections;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD", justification="Can't map to serializable sets from hibernate. e.g. HashSet - sad but true!")
    private Set<EventSourceEntity> eventSources;

    private boolean editable;

    private boolean ppi;
    
    private Set<RoleBO> allowedRoles = new HashSet<RoleBO>(0);

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor","PMD.UncommentedEmptyConstructor"})
    public QuestionGroup() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="should disable at filter level - not important for us")
    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP2", justification="should disable at filter level - not important for us")
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public QuestionGroupState getState() {
        return state;
    }

    public void setState(QuestionGroupState state) {
        this.state = state;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Set<EventSourceEntity> getEventSources() {
        return eventSources;
    }

    public void setEventSources(Set<EventSourceEntity> eventSources) {
        this.eventSources = eventSources;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isPpi() {
        return ppi;
    }

    public void setPpi(boolean ppi) {
        this.ppi = ppi;
    }

    public List<SectionQuestion> getAllSectionQuestions() {
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        for (Section section : sections) {
            for (SectionQuestion sectionQuestion : section.getQuestions()) {
                sectionQuestions.add(sectionQuestion);
            }
        }
        return sectionQuestions;
    }

	public Set<RoleBO> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(Set<RoleBO> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}
	
	public List<String> getAllowedRolesIds() {
		List<String> ids = new ArrayList<String>();
		for (RoleBO role : allowedRoles) {
			ids.add(role.getId().toString());
		}
		return ids;
	}

}
