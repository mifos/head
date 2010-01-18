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

package org.mifos.application.reports.persistence;

import java.sql.Connection;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class AddReportUpgradeIntegrationTest extends MifosIntegrationTestCase {

    public AddReportUpgradeIntegrationTest() throws Exception {
        super();
    }

    private static final short ACTIVITY_ID = 1;
    private static final int HIGHER_UPGRADE_VERSION = 185;
    private static final short REPORT_CATEGORY_ID = (short) 6;
    private static final short TEST_REPORT_ID = (short) 4;
    private static final int LOWER_UPGRADE_VERSION = 184;
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

    public void testShouldNotThrowErrorWhenUpgradingForVer184WithActivityIdNull() throws Exception {
        AddReport addReport = createReport(LOWER_UPGRADE_VERSION);

        try {
            addReport.doUpgrade(connection);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Assert.fail("Should not throw error when inserting report:");
        }
    }

    private AddReport createReport(int version) {
        return new AddReport(version, TEST_REPORT_ID, REPORT_CATEGORY_ID, "TestReportForUpgrade",
                "test_report_upgrade", "design string", ACTIVITY_ID);
    }

    public void testShouldUpgradeForDBVersion185OrMoreWithAcivityId() throws Exception {
        AddReport addReport = createReport(HIGHER_UPGRADE_VERSION);
        addReport.doUpgrade(connection);
        ReportsBO report = new ReportsPersistence().getReport(TEST_REPORT_ID);
        Assert.assertNotNull(report.getActivityId());
        Assert.assertNotNull(report.getIsActive());
    }

    @Override
    protected void tearDown() throws Exception {
        transaction.rollback();
        super.tearDown();
    }
}
