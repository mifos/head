package org.mifos.framework.business;

import java.util.Date;

import org.mifos.framework.security.util.UserContext;

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
		setCreatedDate(new Date());
		setCreatedBy(userContext.getId());
	}

	protected void setCreateDetails(Short userId, Date date) {
		setCreatedDate(date);
		setCreatedBy(userId);
	}

	protected void setUpdateDetails() {
		setUpdatedDate(new Date());
		if (userContext != null)
			setUpdatedBy(userContext.getId());
		else
			setUpdatedBy((short) 1);
	}

	protected void setUpdateDetails(Short userId) {
		setUpdatedDate(new Date());
		setUpdatedBy(userId);
	}

}
