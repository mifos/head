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

package org.mifos.customers.center.business.service;

import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.util.UserContext;

public class CenterBusinessService implements BusinessService {

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public CenterBO getCenter(Integer customerId) throws ServiceException {
        try {
            return new CenterPersistence().getCenter(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public CenterBO findBySystemId(String globalCustNum) throws ServiceException {
        try {
            return new CenterPersistence().findBySystemId(globalCustNum);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public QueryResult search(String searchString, Short userId) throws ServiceException {

        try {
            return new CenterPersistence().search(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
