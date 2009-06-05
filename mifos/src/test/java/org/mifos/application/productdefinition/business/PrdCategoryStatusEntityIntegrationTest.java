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

package org.mifos.application.productdefinition.business;

import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class PrdCategoryStatusEntityIntegrationTest extends MifosIntegrationTest {

    public PrdCategoryStatusEntityIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private PrdCategoryStatusEntity prdCategoryStatusEntity;
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
        super.tearDown();
    }

    public void testGetNameFailure() {
        prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("0"));
        String name = prdCategoryStatusEntity.getName();
        assertFalse("This should fail, name is Inactive", !("Inactive".equals(name)));
    }

    public void testGetNameSuccess() {
        prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
        String name = prdCategoryStatusEntity.getName();
        assertEquals("Active", name);
    }

    public void testGetNamesSuccess() {
        prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
        Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = prdCategoryStatusEntity.getNames();
        int size = lookUpValueLocaleEntitySet.size();
        assertEquals(1, size);
    }

    public void testGetNamesFailure() {
        prdCategoryStatusEntity = getPrdCategoryStatus(Short.valueOf("1"));
        Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = prdCategoryStatusEntity.getNames();
        int size = lookUpValueLocaleEntitySet.size();
        assertFalse("This should fail, the size is 1", !(size == 1));
    }

    private PrdCategoryStatusEntity getPrdCategoryStatus(Short id) {
        Query query = session
                .createQuery("from org.mifos.application.productdefinition.business.PrdCategoryStatusEntity prdCatStatus "
                        + "where prdCatStatus.id = ?");
        query.setString(0, id.toString());
        PrdCategoryStatusEntity prdCategoryStatusEntity = (PrdCategoryStatusEntity) query.uniqueResult();
        return prdCategoryStatusEntity;
    }

}
