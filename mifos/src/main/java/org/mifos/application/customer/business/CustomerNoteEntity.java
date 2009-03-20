/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.customer.business;

import java.util.Date;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;

public class CustomerNoteEntity extends PersistentObject {

	private final Integer commentId;

	private final Date commentDate;

	private final String comment;

	private final CustomerBO customer;

	private final PersonnelBO personnel;

	public CustomerNoteEntity(String comment, Date commentDate,
			PersonnelBO personnel, CustomerBO customer) {
		this.comment = comment;
		this.commentDate = commentDate;
		this.personnel = personnel;
		this.customer = customer;
		this.commentId = null;
	}

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerNoteEntity() {
		this.commentId = null;
		this.commentDate = null;
		this.comment = null;
		this.personnel = null;
		this.customer = null;
	}
	
	public String getCommentDateStr() {
		return (commentDate != null) ? this.commentDate.toString() : "";
	}

	public Date getCommentDate() {
		return this.commentDate;
	}

	public String getComment() {
		return this.comment;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public String getPersonnelName() {
		return personnel.getDisplayName();
	}
}
