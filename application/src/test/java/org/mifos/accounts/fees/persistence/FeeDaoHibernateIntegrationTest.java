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

package org.mifos.accounts.fees.persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.Localization;
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
                                    "/org/mifos/config/resources/messageSourceBean.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class FeeDaoHibernateIntegrationTest {

    @Autowired
    private FeeDao feeDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private AmountFeeBO weeklyOneTimeForLoans;

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

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                               .withFeeAmount("100.0")
                                                               .withName("Center Weekly Periodic Fee")
                                                               .withSameRecurrenceAs(weeklyMeeting)
                                                               .with(sampleBranchOffice())
                                                               .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        weeklyOneTimeForLoans = new FeeBuilder().appliesToLoans().oneTime()
                        .withFeeAmount("100.0")
                        .withName("Center Weekly Periodic Fee")
                        .withSameRecurrenceAs(weeklyMeeting)
                        .with(sampleBranchOffice())
                        .build();
        IntegrationTestObjectMother.saveFee(weeklyOneTimeForLoans);
    }

    @Test
    public void shouldReturnNullWhenNoFeeIsFound() {

        // setup
        databaseCleaner.clean();

        Short feeId = Short.valueOf("-1");

        // exercise test
        FeeBO fee = this.feeDao.findById(feeId);

        // verification
        assertThat(fee, is(nullValue()));
    }

    @Test
    public void shouldReturnExistingFee() {

        // exercise test
        FeeBO fee = this.feeDao.findById(weeklyPeriodicFeeForCenterOnly.getFeeId());

        // verification
        assertThat(fee, is((FeeBO)weeklyPeriodicFeeForCenterOnly));
    }

    @Test
    public void shouldReturnExistingFeeDto() {

        // exercise test
        FeeDto feeDto = this.feeDao.findDtoById(weeklyPeriodicFeeForCenterOnly.getFeeId());

        // verification
        assertThat(feeDto.getName(), is(weeklyPeriodicFeeForCenterOnly.getFeeName()));
    }

    @Test
    public void shouldReturnAllProductApplicableFees() {

        // exercise test
        List<FeeDto> feeDtos = this.feeDao.retrieveAllProductFees();

        // verification
        assertThat(feeDtos.size(), is(1));
    }

    @Test
    public void shouldReturnAllCustomerApplicableFees() {

        // exercise test
        List<FeeDto> feeDtos = this.feeDao.retrieveAllCustomerFees();

        // verification
        assertThat(feeDtos.size(), is(1));
    }

    @Test
    public void shouldReturnAllFeeCategories() {

        // exercise test
        List<CategoryTypeEntity> categories = this.feeDao.retrieveFeeCategories();

        // verification
        assertThat(categories.size(), is(5));
    }

    @Test
    public void shouldReturnAllFeeFormula() {

        // exercise test
        List<FeeFormulaEntity> formulae = this.feeDao.retrieveFeeFormulae();

        // verification
        assertThat(formulae.size(), is(3));
    }

    @Test
    public void shouldReturnAllFeeFrequencyTypes() {

        // exercise test
        List<FeeFrequencyTypeEntity> feeFrequencies = this.feeDao.retrieveFeeFrequencies();

        // verification
        assertThat(feeFrequencies.size(), is(2));
    }

    @Test
    public void shouldReturnAllFeePayments() {

        // exercise test
        List<FeePaymentEntity> feeFrequencies = this.feeDao.retrieveFeePayments();

        // verification
        assertThat(feeFrequencies.size(), is(3));
    }

    @Test
    public void shouldReturnAllFeeStatuses() {

        // exercise test
        List<FeeStatusEntity> feeFrequencies = this.feeDao.retrieveFeeStatuses();

        // verification
        assertThat(feeFrequencies.size(), is(2));
    }

    @Test
    public void shouldFindOneTimeFeeFrequencyType() {

        // exercise test
        FeeFrequencyTypeEntity entity = this.feeDao.findFeeFrequencyEntityByType(FeeFrequencyType.ONETIME);

        // verification
        assertThat(entity, is(notNullValue()));
    }

    @Test
    public void shouldFindFeeCategoryType() {

        // exercise test
        CategoryTypeEntity entity = this.feeDao.findFeeCategoryTypeEntityByType(FeeCategory.ALLCUSTOMERS);

        // verification
        assertThat(entity, is(notNullValue()));
    }

    @Test
    public void shouldFindFeeFormulaType() {

        // exercise test
        FeeFormulaEntity entity = this.feeDao.findFeeFormulaEntityByType(FeeFormula.AMOUNT_AND_INTEREST);

        // verification
        assertThat(entity, is(notNullValue()));
    }

    @Test
    public void shouldFindFeePaymentType() {

        // exercise test
        FeePaymentEntity entity = this.feeDao.findFeePaymentEntityByType(FeePayment.TIME_OF_DISBURSEMENT);

        // verification
        assertThat(entity, is(notNullValue()));
    }
}