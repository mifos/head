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

package org.mifos.framework.hibernate;

import junit.framework.Assert;
import junitx.framework.ObjectAssert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HibernateIntegrationTest extends MifosIntegrationTestCase {

    public HibernateIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testHibernateSessionFactoryForNullConfig() {
        try {
            HibernateSessionFactory.setConfiguration(null);
            Assert.fail();
        } catch (HibernateStartUpException outer) {
           Assert.assertEquals("exception.framework.SystemException", outer.getKey());
            ObjectAssert.assertInstanceOf(NullPointerException.class, outer.getCause());
        }
    }

    public void testHibernateUtilIsSessionOpen() {
        StaticHibernateUtil.getSessionTL();
       Assert.assertTrue(StaticHibernateUtil.isSessionOpen());
        StaticHibernateUtil.closeSession();
    }

    public void testHibernateUtilIsSessionOpenForClosedSession() {
        StaticHibernateUtil.closeSession();
        Assert.assertFalse(StaticHibernateUtil.isSessionOpen());
    }

}
