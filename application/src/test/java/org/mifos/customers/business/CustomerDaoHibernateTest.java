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
package org.mifos.customers.business;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.argThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerDaoHibernateTest {

    private CustomerDao customerDao;

    // collaborators
    @Mock
    private GenericDao genericDao;

    @Mock
    private CustomerBO customer;

    @Mock
    private ClientBO client;

    @Before
    public void setupAndInjectCollaborators() {

        customerDao = new CustomerDaoHibernate(genericDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCustomerIdIsNull() {

        customerDao.findCustomerById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCustomerIsNull() {

        customerDao.findActiveClientsUnderGroup(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenCustomerIsNotAGroup() {

        // stubbing
        when(customer.isGroup()).thenReturn(false);

        // exercise test
        customerDao.findActiveClientsUnderGroup(customer);
    }

    @Test
    public void shouldValidateClientForDuplicateNameOrGovtId() {
        when(client.getGovernmentId()).thenReturn("    GovID ");
        when(client.getDisplayName()).thenReturn(" abc     ");
        when(client.getDateOfBirth()).thenReturn(new Date(System.currentTimeMillis()));
        setUpGenericDAOExpectationsForGovtLookup("GovID");
        setUpGenericDAOExpectationsForNameLookup("abc");
        try {
            customerDao.validateClientForDuplicateNameOrGovtId(client);
        } catch (CustomerException e) {
            Assert.fail("An exception should not have occured here");
        }
    }

    private void setUpGenericDAOExpectationsForNameLookup(String name) {
        List resultSet = new LinkedList();
        resultSet.add(Long.valueOf(0));
        when(genericDao.executeNamedQuery(anyString(), argThat(new IsMapOfGivenElements(name)))).thenReturn(resultSet);
    }

    private void setUpGenericDAOExpectationsForGovtLookup(String govtId) {
        List resultSet = new LinkedList();
        resultSet.add(Long.valueOf(0));
        when(genericDao.executeNamedQuery(anyString(), argThat(new IsMapOfGivenElements(govtId)))).thenReturn(resultSet);
    }

    private class IsMapOfGivenElements extends ArgumentMatcher<Map> {

        private final Object[] arguments;

        public IsMapOfGivenElements(Object... arguments) {
            this.arguments = arguments;
        }

        @Override
        public boolean matches(Object argument) {
            if (!(argument instanceof Map)) {
                return false;
            }
            Map map = (Map)argument;
            for(Object arg: arguments) {
                if (!map.containsValue(arg)) {
                    return false;
                }
            }
            return true;
        }

    }
}
