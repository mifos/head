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

package org.mifos.customers.group.business.service;

import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.util.UserContext;

/**
 * @deprecated - use {@link CustomerDao}
 */
@Deprecated
public class GroupBusinessService implements BusinessService {

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    public GroupBO findBySystemId(String globalCustNum) throws ServiceException {
        try {
            GroupBO groupBO = new GroupPersistence().findBySystemId(globalCustNum);
            return groupBO;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public GroupBO getGroup(Integer customerId) throws ServiceException {
        try {
            GroupBO groupBO = new GroupPersistence().getGroupByCustomerId(customerId);
            return groupBO;
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public QueryResult search(String searchString, Short userId) throws ServiceException {

        try {
            return new GroupPersistence().search(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public QueryResult searchForAddingClientToGroup(String searchString, Short userId) throws ServiceException {

        try {
            return new GroupPersistence().searchForAddingClientToGroup(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }
}
