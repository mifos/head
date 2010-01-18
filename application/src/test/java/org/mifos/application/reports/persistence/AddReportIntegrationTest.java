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

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Ignore;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;

@Ignore
public class AddReportIntegrationTest extends MifosIntegrationTestCase {
    
    public AddReportIntegrationTest() throws Exception {
        super();
    }

    private Session session;
    
    @Override
    public void setUp() {
        session = StaticHibernateUtil.getSessionTL(); 
    }
    
    @Override
    public void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
    }

    public void testStartFromStandardStore() throws Exception {
        short newId = 17032;
        AddReport upgrade = new AddReport(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId,
                ReportsCategoryBO.ANALYSIS, "Detailed Aging of Portfolio at Risk", "aging_portfolio_at_risk",
                "DetailedAgingPortfolioAtRisk.rptdesign");
        upgrade.upgrade(session.connection());
        ReportsBO fetched = (ReportsBO) session.get(ReportsBO.class, newId);
       Assert.assertEquals(newId, (int) fetched.getReportId());
       Assert.assertEquals(ReportsBO.ACTIVE, fetched.getIsActive());
       Assert.assertEquals(null, fetched.getActivityId());
       Assert.assertEquals("Detailed Aging of Portfolio at Risk", fetched.getReportName());
       Assert.assertEquals("aging_portfolio_at_risk", fetched.getReportIdentifier());
       Assert.assertEquals(ReportsCategoryBO.ANALYSIS, (int) fetched.getReportsCategoryBO().getReportCategoryId());

        ReportsJasperMap map = fetched.getReportsJasperMap();
       Assert.assertEquals("DetailedAgingPortfolioAtRisk.rptdesign", map.getReportJasper());
    }
}
