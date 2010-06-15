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

package org.mifos.customers.office.persistence;

import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class OfficeDaoHibernateIntegrationTest {

    // class under test
    @Autowired
    private OfficeDao officeDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    // data
    private OfficeBO headOffice;
    private OfficeBO regionalOffice;
    private OfficeBO areaOffice;
    private OfficeBO branch1;
    private OfficeBO branch2;
    private OfficeBO branch3;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

        createOfficeHierarchy();
    }

    @Test
    public void givenAnOfficeExistsShouldReturnItAsOfficeDto() {

        OfficeDto office = officeDao.findOfficeDtoById(headOffice.getOfficeId());

        assertThat(office.getName(), is("Mifos HO"));
    }

    @Test
    public void givenActiveLevelsExistsShouldReturnThemAsOfficeViews() {

        List<OfficeDetailsDto> offices = officeDao.findActiveOfficeLevels();

        assertThat(offices.isEmpty(), is(false));
    }

    @Test
    public void givenAnOfficeHierarchyExistsShouldReturnItAsOfficeHierarchyDto() {

        OfficeHierarchyDto officeHierarchy = officeDao.headOfficeHierarchy();

        assertThat(officeHierarchy.getOfficeName(), is("Mifos HO"));
        assertThat(officeHierarchy.isActive(), is(true));
        assertThat(officeHierarchy.getChildren().size(), is(2));
    }

    @Test
    public void shouldReturnOfficeNamesOfHighestLevelOfficeInAOfficeHierarchyFromOfficeIdList() {

        Collection<Short> officeIds = Arrays.asList(this.areaOffice.getOfficeId(), this.branch2.getOfficeId(), this.branch3.getOfficeId());

        List<String> topLevelOfficeNames = officeDao.topLevelOfficeNames(officeIds);

        assertThat(topLevelOfficeNames.isEmpty(), is(false));
        assertThat(topLevelOfficeNames, hasItem(areaOffice.getOfficeName()));
        assertThat("branch2 is a branch of the area office and should not be returned", topLevelOfficeNames, not(hasItem(branch2.getOfficeName())));
        assertThat(topLevelOfficeNames, hasItem(branch3.getOfficeName()));
    }

    @Test(expected=OfficeException.class)
    public void shouldThrowOfficeExceptionWhenActiveChildrenExistUnderOffice() throws Exception {

        officeDao.validateNoActiveChildrenExist(headOffice.getOfficeId());
    }

    @Test
    public void shouldNotThrowOfficeExceptionWhenNoActiveChildrenExist() throws Exception {

        officeDao.validateNoActiveChildrenExist(branch3.getOfficeId());
    }

    @Test(expected=OfficeException.class)
    public void shouldThrowOfficeExceptionWhenActivePersonnelExist() throws Exception {

        officeDao.validateNoActivePeronnelExist(headOffice.getOfficeId());
    }

    @Test
    public void shouldNotThrowOfficeExceptionWhenNoActivePersonnelExist() throws Exception {

        officeDao.validateNoActivePeronnelExist(branch3.getOfficeId());
    }

    @Test
    public void shouldNotThrowOfficeExceptionWhenOfficeNameDoesNotExist() throws Exception {

        officeDao.validateOfficeNameIsNotTaken("officeNameThatDoesNotAlreadyExist");
    }

    @Test(expected=OfficeException.class)
    public void shouldThrowOfficeExceptionWhenOfficeNameDoesExist() throws Exception {

        officeDao.validateOfficeNameIsNotTaken(headOffice.getOfficeName());
    }

    @Test
    public void shouldNotThrowOfficeExceptionWhenOfficeShortNameDoesNotExist() throws Exception {

        officeDao.validateOfficeShortNameIsNotTaken("shortNameThatDoesNotAlreadyExist");
    }

    @Test(expected=OfficeException.class)
    public void shouldThrowOfficeExceptionWhenOfficeShortNameDoesExist() throws Exception {

        officeDao.validateOfficeShortNameIsNotTaken(headOffice.getShortName());
    }

    public void createOfficeHierarchy() {

        // A default head office is added as seed data for integration tests along with a 'TestAreaOffice' as child
        headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        regionalOffice = new OfficeBuilder().withGlobalOfficeNum("002").withName("region1").regionalOffice().withParentOffice(headOffice).build();
        IntegrationTestObjectMother.createOffice(regionalOffice);

        OfficeBO subRegionalOffice = new OfficeBuilder().withGlobalOfficeNum("003").withName("sub1-of-region1").subRegionalOffice().withParentOffice(regionalOffice).build();
        IntegrationTestObjectMother.createOffice(subRegionalOffice);

        areaOffice = new OfficeBuilder().withGlobalOfficeNum("004").withName("area-of-sub1-regional").areaOffice().withParentOffice(subRegionalOffice).build();
        IntegrationTestObjectMother.createOffice(areaOffice);

        branch1 = new OfficeBuilder().withGlobalOfficeNum("005").withName("branch1-of-area").branchOffice().withParentOffice(areaOffice).build();
        IntegrationTestObjectMother.createOffice(branch1);

        branch2 = new OfficeBuilder().withGlobalOfficeNum("006").withName("branch2-of-area").branchOffice().withParentOffice(areaOffice).build();
        IntegrationTestObjectMother.createOffice(branch2);

        branch3 = new OfficeBuilder().withGlobalOfficeNum("007").withName("branch1-of-regional").branchOffice().withParentOffice(regionalOffice).build();
        IntegrationTestObjectMother.createOffice(branch3);
    }
}