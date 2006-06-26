/**

 * PersonnelNotes.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;

/**
 * This class is used as value object for the PersonnelNotesAction. It
 * represents the notes entered for a personnel.
 * 
 * @author ashishsm
 * 
 */
public class PersonnelNotesEntity extends PersistentObject {

	private Integer commentId;

	private Date commentDate;

	private String comment;

	private PersonnelBO officer;

	private Short officerId;

	private Short personnelId;

	public PersonnelNotesEntity() {
		super();
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public String getCommentDateStr() {
		return (commentDate != null) ? this.commentDate.toString() : "";
	}

	public Date getCommentDate() {
		return this.commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public PersonnelBO getOfficer() {
		return officer;
	}

	public void setOfficer(PersonnelBO officer) {
		this.officer = officer;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public String getPersonnelName() {
		return officer.getDisplayName();
	}

	public Short getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Short officerId) {
		this.officerId = officerId;
	}

}
