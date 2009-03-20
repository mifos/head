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
 
package org.mifos.application.accounts.business;

import java.sql.Date;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the notes associated with account
 */
public class AccountNotesEntity extends PersistentObject {

	private final Integer commentId;

	private final Date commentDate;

	private final String comment;

	private final PersonnelBO personnel;

	private final AccountBO account;

	protected AccountNotesEntity() {
		commentId = null;
		this.commentDate = null;
		this.comment = null;
		this.personnel = null;
		this.account = null;
	}

	public AccountNotesEntity(Date commentDate, String comment,
			PersonnelBO personnel, AccountBO account) {
		commentId = null;
		this.commentDate = commentDate;
		this.comment = comment;
		this.personnel = personnel;
		this.account = account;
	}

	public Integer getCommentId() {
		return commentId;
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

	public AccountBO getAccount() {
		return account;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public String getPersonnelName() {
		return personnel.getDisplayName();
	}

}
