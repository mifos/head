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
 
package org.mifos.framework.business;

import java.util.Date;

import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;

public abstract class BusinessObject extends PersistentObject {

	protected UserContext userContext;

	public BusinessObject() {
		this.userContext = null;
	}

	protected BusinessObject(UserContext userContext) {
		this.userContext = userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public UserContext getUserContext() {
		return this.userContext;
	}

	protected void setCreateDetails() {
		setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
		setCreatedBy(userContext.getId());
	}

	protected void setCreateDetails(Short userId, Date date) {
		setCreatedDate(date);
		setCreatedBy(userId);
	}

	protected void setUpdateDetails() {
		setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
		if (userContext != null)
			setUpdatedBy(userContext.getId());
		else
			setUpdatedBy((short) 1);
	}

	protected void setUpdateDetails(Short userId) {
		setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
		setUpdatedBy(userId);
	}

}
