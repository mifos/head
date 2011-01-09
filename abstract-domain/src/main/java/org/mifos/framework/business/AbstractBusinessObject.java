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

import org.joda.time.DateTime;
import org.mifos.security.util.UserContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
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
        setCreatedDate(new DateTime().toDate());
        if (userContext != null) {
            setCreatedBy(userContext.getId());
        } else {
            setCreatedBy((short) 1);
        }
    }

    protected void setCreateDetails(Short userId, Date date) {
        setCreatedDate(date);
        setCreatedBy(userId);
    }

    public void setUpdateDetails() {
        setUpdatedDate(new DateTime().toDate());
        if (userContext != null) {
            setUpdatedBy(userContext.getId());
        } else {
            setUpdatedBy((short) 1);
        }
    }

    public void updateDetails(UserContext userContext) {
        this.updatedDate = new Date();
        this.updatedBy = userContext.getId();
        setUserContext(userContext);
    }

    protected void setUpdateDetails(Short userId) {
        setUpdatedDate(new DateTime().toDate());
        setUpdatedBy(userId);
    }
}