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

package org.mifos.framework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.hibernate.jmx.StatisticsService;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 *  All classes extending this class must be names as <b>*IntegrationTest.java</b> to support maven-surefire-plugin autofind
 * feature.
 * <br />
 * <br />
 * Inheriting from this instead of TestCase is deprecated, generally speaking. The reason is that TestCaseInitializer
 * (a) runs too soon (it is more graceful for a long delay to happen in setUp), and (b) initializes too much (most tests
 * don't need everything which is there).
 *
 * This base class initializes the database and various other things and so any class derived from this is an
 * integration test. If a test is not an integration test and does not need the database, then it should not derive from
 * this class.
 */
public class MifosIntegrationTestCase extends TestCase {

    protected MifosIntegrationTestCase() throws Exception {
        new TestCaseInitializer().initialize();
    }

    private StatisticsService statisticsService;

    protected void assertEquals(String s, Money one, Money two) {
        if (one.equals(two)) {
            return;
        }
        throw new ComparisonFailure(s, one.toString(), two.toString());
    }

    protected Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.parse(date);
    }

    protected StatisticsService getStatisticsService() {
        return this.statisticsService;
    }

    protected void setStatisticsService(StatisticsService service) {
        this.statisticsService = service;
    }

    protected void initializeStatisticsService() {
        statisticsService = new StatisticsService();
        statisticsService.setSessionFactory(StaticHibernateUtil.getSessionFactory());
        statisticsService.setStatisticsEnabled(true);
    }

    /*
     * Gets the test data office with office_id == 1
     */
    protected OfficeBO getHeadOffice() {
        try {
            return new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data office with office_id == 3
     */
    protected OfficeBO getBranchOffice() {
        try {
            return new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 1
     */
    protected PersonnelBO getSystemUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 3
     */
    protected PersonnelBO getTestUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    public MifosCurrency getCurrency() {
        // TODO: will be replaced by a better way to get currency for integration tests
        // NOTE: TestObjectFactory.getCurrency also exists
        return Money.getDefaultCurrency();
    }
}
