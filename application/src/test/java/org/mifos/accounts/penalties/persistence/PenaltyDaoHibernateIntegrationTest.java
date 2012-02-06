/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.penalties.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyCategoryEntity;
import org.mifos.accounts.penalties.business.PenaltyFormulaEntity;
import org.mifos.accounts.penalties.business.PenaltyFrequencyEntity;
import org.mifos.accounts.penalties.business.PenaltyPeriodEntity;
import org.mifos.accounts.penalties.business.PenaltyStatusEntity;
import org.mifos.accounts.penalties.business.RatePenaltyBO;
import org.mifos.accounts.penalties.util.helpers.PenaltyCategory;
import org.mifos.accounts.penalties.util.helpers.PenaltyFormula;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class PenaltyDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private PenaltyDao penaltyDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private AmountPenaltyBO amountPenalty;
    private RatePenaltyBO ratePenalty;
    private PenaltyStatusEntity statusEntity;
    private PenaltyCategoryEntity categoryEntity;
    private PenaltyFrequencyEntity frequencyEntity;
    private PenaltyPeriodEntity periodEntity;
    private PenaltyFormulaEntity formulaEntity;
    private GLCodeEntity glCodeEntity;

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        periodEntity = new PenaltyPeriodEntity(PenaltyPeriod.INSTALLMENTS);
        statusEntity = new PenaltyStatusEntity(PenaltyStatus.ACTIVE);
        categoryEntity = new PenaltyCategoryEntity(PenaltyCategory.LOAN);
        frequencyEntity = new PenaltyFrequencyEntity(PenaltyFrequency.NONE);
        formulaEntity = new PenaltyFormulaEntity(PenaltyFormula.OUTSTANDING_LOAN_AMOUNT);
        glCodeEntity = new GLCodeEntity((short) 42, "31102");

        amountPenalty = new AmountPenaltyBO(TestUtils.makeUser(), "Amount Penalty Test", new PenaltyCategoryEntity(
                PenaltyCategory.SAVING), periodEntity, 6, 1, 14, frequencyEntity, glCodeEntity,
                TestUtils.createMoney(158.5));

        ratePenalty = new RatePenaltyBO(TestUtils.makeUser(), "Rate Penalty Test", categoryEntity, periodEntity, 6, 1,
                10, frequencyEntity, glCodeEntity, formulaEntity, 10.5);

        IntegrationTestObjectMother.createPenalty(amountPenalty);
        IntegrationTestObjectMother.createPenalty(ratePenalty);
    }

    @Test
    public void shouldSavePenalty() throws Exception {
        StaticHibernateUtil.startTransaction();
        penaltyDao.save(amountPenalty);
        StaticHibernateUtil.flushSession();

        assertThat(amountPenalty.getPenaltyId(), is(notNullValue()));
        
        StaticHibernateUtil.startTransaction();
        penaltyDao.save(ratePenalty);
        StaticHibernateUtil.flushSession();

        assertThat(ratePenalty.getPenaltyId(), is(notNullValue()));
    }

    @Test
    public void shouldUpdatePenalty() throws Exception {
        amountPenalty.setAmount(new Money(Money.getDefaultCurrency(), 500.1));

        StaticHibernateUtil.startTransaction();
        penaltyDao.save(amountPenalty);
        StaticHibernateUtil.flushSession();

        assertThat(amountPenalty.getAmount().getAmount().doubleValue(), is(500.1));

        ratePenalty.setRate(7.4);
        ratePenalty.setFormula(new PenaltyFormulaEntity(PenaltyFormula.OVERDUE_PRINCIPAL));

        StaticHibernateUtil.startTransaction();
        penaltyDao.save(ratePenalty);
        StaticHibernateUtil.flushSession();

        assertThat(ratePenalty.getRate(), is(7.4));
        assertThat(ratePenalty.getFormula().getId(), is((short)4));
    }

    @Test
    public void shouldFindPenaltyById() throws Exception {
        PenaltyBO found = penaltyDao.findPenalty(amountPenalty.getPenaltyId());

        assertThat((AmountPenaltyBO) found, is(amountPenalty));

        found = penaltyDao.findPenalty(ratePenalty.getPenaltyId());

        assertThat((RatePenaltyBO) found, is(ratePenalty));
    }

    @Test
    public void shouldFindAllPenaltyForLoans() throws Exception {
        List<PenaltyBO> found = penaltyDao.findAllLoanPenalties();

        assertThat(found, hasItem((PenaltyBO) ratePenalty));
        assertThat(found.size(), is(1));
    }

    @Test
    public void shouldFindAllPenaltyForSavings() throws Exception {
        List<PenaltyBO> found = penaltyDao.findAllSavingPenalties();

        assertThat(found, hasItem((PenaltyBO) amountPenalty));
        assertThat(found.size(), is(1));
    }

    @Test
    public void shouldFindAllPenaltyCategoryEntity() throws Exception {
        List<PenaltyCategoryEntity> found = penaltyDao.getPenaltiesCategories();

        assertThat(found.size(), is(2));
    }

    @Test
    public void shouldFindAllPenaltyPeriodEntity() throws Exception {
        List<PenaltyPeriodEntity> found = penaltyDao.getPenaltiesPeriods();

        assertThat(found.size(), is(2));
    }

    @Test
    public void shouldFindAllPenaltyFormulaEntity() throws Exception {
        List<PenaltyFormulaEntity> found = penaltyDao.getPenaltiesFormulas();

        assertThat(found.size(), is(4));
    }

    @Test
    public void shouldFindAllPenaltyFrequencyEntity() throws Exception {
        List<PenaltyFrequencyEntity> found = penaltyDao.getPenaltiesFrequencies();

        assertThat(found.size(), is(4));
    }

    @Test
    public void shouldFindAllPenaltyStatusEntity() throws Exception {
        List<PenaltyStatusEntity> found = penaltyDao.getPenaltiesStatuses();

        assertThat(found.size(), is(2));
    }

    @Test
    public void shouldFindPenaltyFrequencyEntityByType() throws Exception {
        PenaltyFrequencyEntity found = penaltyDao.findPenaltyFrequencyEntityByType(PenaltyFrequency
                .getPenaltyFrequencyType(frequencyEntity.getId()));

        assertThat(found.getId(), is(frequencyEntity.getId()));
    }

    @Test
    public void shouldFindPenaltyCategoryEntityByType() throws Exception {
        PenaltyCategoryEntity found = penaltyDao.findPenaltyCategoryEntityByType(categoryEntity.getPenaltyCategory());

        assertThat(found.getId(), is(categoryEntity.getId()));
    }

    @Test
    public void shouldFindPenaltyPeriodEntityByType() throws Exception {
        PenaltyPeriodEntity found = penaltyDao.findPenaltyPeriodEntityByType(periodEntity.getPenaltyPeriod());

        assertThat(found.getId(), is(periodEntity.getId()));
    }

    @Test
    public void shouldFindPenaltyFormulaEntityByType() throws Exception {
        PenaltyFormulaEntity found = penaltyDao.findPenaltyFormulaEntityByType(formulaEntity.getPenaltyFormula());

        assertThat(found.getId(), is(formulaEntity.getId()));
    }

    @Test
    public void shouldFindPenaltyStatusEntityByType() throws Exception {
        PenaltyStatusEntity found = penaltyDao.findPenaltyStatusEntityByType(statusEntity.getPenaltyStatus());

        assertThat(found.getId(), is(statusEntity.getId()));
    }
}