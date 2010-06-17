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

package org.mifos.reports.persistence;

import java.sql.Connection;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.business.ReportsCategoryBO;

public class AddReportUpgradeIntegrationTest extends MifosIntegrationTestCase {

    public AddReportUpgradeIntegrationTest() throws Exception {
        super();
    }

    private Session session;
    private Transaction transaction;
    private Connection connection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
        connection = session.connection();
        transaction = session.beginTransaction();
    }

    private AddReport createReport(int version) {
        return new AddReport(version, ReportsCategoryBO.ANALYSIS, "TestReportForUpgrade", "XYZ.rptdesign");
    }

    public void testShouldUpgrade() throws Exception {
        AddReport addReport = createReport(DatabaseVersionPersistence.APPLICATION_VERSION + 1);
        addReport.upgrade(connection);
        ReportsBO report = new ReportsPersistence().getReport(ReportsCategoryBO.ANALYSIS);
        Assert.assertNotNull(report.getActivityId());
        Assert.assertTrue(report.getIsActive() == (short)1);
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }
}
