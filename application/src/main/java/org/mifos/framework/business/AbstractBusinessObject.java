/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.framework.util.DateTimeService;
import org.mifos.security.util.UserContext;

public abstract class AbstractBusinessObject extends AbstractEntity {

    protected UserContext userContext;

    protected Date createdDate;

    protected Short createdBy;

    protected Date updatedDate;

    protected Short updatedBy;

    protected Integer versionNo;

    public Short getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Short getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public AbstractBusinessObject() {
        this.userContext = null;
    }

    protected AbstractBusinessObject(UserContext userContext) {
        this.userContext = userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }

    public void setCreateDetails() {
        setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
        setCreatedBy(userContext.getId());
    }

    protected void setCreateDetails(Short userId, Date date) {
        setCreatedDate(date);
        setCreatedBy(userId);
    }

    public void setUpdateDetails() {
        setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        if (userContext != null) {
            setUpdatedBy(userContext.getId());
        } else {
            setUpdatedBy((short) 1);
        }
    }

    protected void setUpdateDetails(Short userId) {
        setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        setUpdatedBy(userId);
    }

}
