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

import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class AccountStateEntityIntegrationTest extends MifosIntegrationTestCase {

    public AccountStateEntityIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private AccountStateEntity accountStateEntity;
    private Session session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        session = null;
    }

    public void testGetNameFailure() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
        String name = accountStateEntity.getName();
        assertFalse("This should fail, name is Partial Application", !("Partial Application".equals(name)));
    }

    public void testGetNameSuccess() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
        String name = accountStateEntity.getName();
        assertEquals("Partial Application", name);
    }

    public void testGetNamesSuccess() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
        Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = accountStateEntity.getNames();
        int size = lookUpValueLocaleEntitySet.size();
        assertEquals(1, size);
    }

    public void testGetNamesFailure() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
        Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = accountStateEntity.getNames();
        int size = lookUpValueLocaleEntitySet.size();
        assertFalse("This should fail, the size is 1", !(size == 1));
    }

    public void testGetNameWithLocaleSuccess() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("3"));
        String name = accountStateEntity.getName();
        assertEquals(TestConstants.APPROVED, name);
    }

    public void testGetNameWithLocaleFailure() {
        accountStateEntity = getAccountStateEntityObject(Short.valueOf("3"));
        String name = accountStateEntity.getName();
        assertFalse("This should fail, name is Approved", !(TestConstants.APPROVED.equals(name)));
    }

    private AccountStateEntity getAccountStateEntityObject(Short id) {
        Query query = session.createQuery("from org.mifos.application.accounts.business.AccountStateEntity ac_state "
                + "where ac_state.id = ?");
        query.setString(0, id.toString());
        return (AccountStateEntity) query.uniqueResult();
    }

}
