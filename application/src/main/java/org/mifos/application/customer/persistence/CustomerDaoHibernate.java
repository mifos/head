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
package org.mifos.application.customer.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;

/**
 *
 */
public class CustomerDaoHibernate implements CustomerDao {

    private final GenericDao genericDao;

    public CustomerDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public CustomerBO findCustomerById(final Integer customerId) {
        
        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null");
        }

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        return (CustomerBO) genericDao.executeUniqueResultNamedQuery("customer.findById", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClientBO> findActiveClientsUnderGroup(final CustomerBO customer) {

        if (customer == null) {
            throw new IllegalArgumentException("customer cannot be null");
        }

        if (!customer.isGroup()) {
            throw new IllegalArgumentException("customer must be a group");
        }

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("GROUP_ID", customer.getCustomerId());
        return (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.ACTIVE_CLIENTS_UNDER_GROUP,
                queryParameters);
    }
}
