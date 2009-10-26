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

package org.mifos.framework.persistence;

import java.sql.Connection;

import junit.framework.Assert;
import net.sourceforge.mayfly.Database;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class PersistenceIntegrationTest extends MifosIntegrationTestCase {

    public PersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testConnection() {
        LoanPersistence loanPersistance = new LoanPersistence();
        Assert.assertNotNull(loanPersistance.getConnection());
        StaticHibernateUtil.closeSession();
    }

    public void xtestNonUniqueObjectException() throws Exception {
        /*
         * The question is whether this is the kind of hibernate situation which
         * produces a NonUniqueObjectException. As written, this test doesn't
         * even get that far (having trouble inserting into a table with an
         * auto-increment column).
         */
        SessionFactory factory = StaticHibernateUtil.getSessionFactory();
        Connection connection = StaticHibernateUtil.getSessionTL().connection();
        Session firstSession = factory.openSession(StaticHibernateUtil.getSessionTL().connection());

        GLCodeEntity firstEntity = new GLCodeEntity((short) 77, "code1");
        firstSession.save(firstEntity);

        Session secondSession = factory.openSession(connection);
       Assert.assertEquals("code1", firstEntity.getGlcode());

        GLCodeEntity refetched = (GLCodeEntity) secondSession.get(GLCodeEntity.class, firstEntity.getGlcodeId());
       Assert.assertEquals("code1", refetched.getGlcode());
    }

}
