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

package org.mifos.accounts.penalties.business;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.penalties.exceptions.PenaltyException;
import org.mifos.accounts.penalties.util.helpers.PenaltyCategory;
import org.mifos.accounts.penalties.util.helpers.PenaltyConstants;
import org.mifos.accounts.penalties.util.helpers.PenaltyFormula;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PenaltyBOIntegrationTest extends MifosIntegrationTestCase {

    private PenaltyBO penalty;

    @After
    public void tearDown() throws Exception {
        penalty = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testCreateWithoutPenaltyName() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "", new PenaltyCategoryEntity(PenaltyCategory.LOAN),
                    new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 1.0, 15.0, new PenaltyFrequencyEntity(
                            PenaltyFrequency.MONTHLY), getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without name", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_NAME);
        }
    }

    @Test
    public void testCreateWithoutPenaltyCategory() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", null, new PenaltyPeriodEntity(
                    PenaltyPeriod.DAYS), 10, 1.0, 15.0, new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY),
                    getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without category", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_CATEGORY);
        }
    }

    @Test
    public void testCreateWithoutPenaltyPeriod() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), null, 10, 1.0, 15.0, new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY),
                    getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without period", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_PERIOD);
        }
    }

    @Test
    public void testCreateWithoutMinimumLimit() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, null, 15.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without minimum limit", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_MINIMUM);
        }
    }

    @Test
    public void testCreateWithoutMaximumLimit() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 1.0, null,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without maximum limit", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_MAXIMUM);
        }
    }

    @Test
    public void testCreateWithoutPenaltyFrequency() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 1.0, 15.0, null,
                    getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without frequency", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_FREQUENCY);
        }
    }

    @Test
    public void testCreateWithoutGlCode() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 1.0, 15.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), null, TestUtils.createMoney(258.7));
            Assert.assertFalse("Penalty is created without gl code", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_GLCODE);
        }
    }

    @Test
    public void testCreateMaximumCanNotBeGreaterThatMinimum() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 15.0, 1.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), TestUtils.createMoney(258.7));
            Assert.assertFalse("penalty is created with maximum limit greater than minimum limit", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_MAX_GREATER_MIN);
        }
    }

    @Test
    public void testCreateAmountPenaltyWithoutAmount() throws Exception {
        try {
            penalty = new AmountPenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 10, 1.0, 15.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), null);
            Assert.assertFalse("Penalty is created without amount", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_AMOUNT);
        }
    }

    @Test
    public void testCreateRatePenaltyWithoutRate() throws Exception {
        try {
            penalty = new RatePenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 1, 1.0, 15.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), new PenaltyFormulaEntity(
                            PenaltyFormula.OVERDUE_AMOUNT_DUE), null);
            Assert.assertFalse("Penalty is created without rate", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_RATE_OR_FORMULA);
        }
    }

    @Test
    public void testCreateRatePenaltyWithoutFormula() throws Exception {
        try {
            penalty = new RatePenaltyBO(TestUtils.makeUser(), "Penalty Test", new PenaltyCategoryEntity(
                    PenaltyCategory.LOAN), new PenaltyPeriodEntity(PenaltyPeriod.DAYS), 1, 1.0, 15.0,
                    new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY), getGLCode("42"), null, 7.5);
            Assert.assertFalse("Penalty is created without formula", true);
        } catch (PenaltyException e) {
            Assert.assertNull(penalty);
            Assert.assertEquals(e.getKey(), PenaltyConstants.INVALID_PENALTY_RATE_OR_FORMULA);
        }
    }

    @Test
    public void testCreateAmountFee() throws Exception {
        String name = "Amount Penalty Test";
        PenaltyCategoryEntity categoryEntity = new PenaltyCategoryEntity(PenaltyCategory.LOAN);
        PenaltyPeriodEntity periodEntity = new PenaltyPeriodEntity(PenaltyPeriod.DAYS);
        int duration = 10;
        double min = 1.0;
        double max = 15.0;
        PenaltyFrequencyEntity frequencyEntity = new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY);
        GLCodeEntity glCode = getGLCode("42");
        Money amount = TestUtils.createMoney(587.6);

        penalty = new AmountPenaltyBO(TestUtils.makeUser(), name, categoryEntity, periodEntity, duration, min, max,
                frequencyEntity, glCode, amount);

        penalty.save();
        StaticHibernateUtil.flushSession();

        penalty = (PenaltyBO) TestObjectFactory.getObject(PenaltyBO.class, penalty.getPenaltyId());

        Assert.assertEquals(name, penalty.getPenaltyName());
        Assert.assertEquals(categoryEntity.getId(), penalty.getCategoryType().getId());
        Assert.assertEquals(duration, penalty.getPeriodDuration().intValue());
        Assert.assertEquals(min, penalty.getMinimumLimit().intValue());
        Assert.assertEquals(max, penalty.getMaximumLimit().intValue());
        Assert.assertEquals(frequencyEntity.getId(), penalty.getPenaltyFrequency().getId());
        Assert.assertEquals(glCode.getGlcodeId(), penalty.getGlCode().getGlcodeId());
        Assert.assertEquals(amount.getAmount(), ((AmountPenaltyBO) penalty).getAmount().getAmount());

        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testCreateOneTimeRateFee() throws Exception {
        String name = "Rate Penalty Test";
        PenaltyCategoryEntity categoryEntity = new PenaltyCategoryEntity(PenaltyCategory.LOAN);
        PenaltyPeriodEntity periodEntity = new PenaltyPeriodEntity(PenaltyPeriod.DAYS);
        int duration = 2;
        double min = 1.0;
        double max = 15.0;
        PenaltyFrequencyEntity frequencyEntity = new PenaltyFrequencyEntity(PenaltyFrequency.MONTHLY);
        GLCodeEntity glCode = getGLCode("42");
        PenaltyFormulaEntity formulaEntity = new PenaltyFormulaEntity(PenaltyFormula.OVERDUE_AMOUNT_DUE);
        double rate = 7.5;

        penalty = new RatePenaltyBO(TestUtils.makeUser(), name, categoryEntity, periodEntity, duration, min, max,
                frequencyEntity, glCode, formulaEntity, rate);

        penalty.save();
        StaticHibernateUtil.flushSession();

        penalty = (PenaltyBO) TestObjectFactory.getObject(PenaltyBO.class, penalty.getPenaltyId());

        Assert.assertEquals(name, penalty.getPenaltyName());
        Assert.assertEquals(categoryEntity.getId(), penalty.getCategoryType().getId());
        Assert.assertEquals(duration, penalty.getPeriodDuration().intValue());
        Assert.assertEquals(min, penalty.getMinimumLimit().intValue());
        Assert.assertEquals(max, penalty.getMaximumLimit().intValue());
        Assert.assertEquals(frequencyEntity.getId(), penalty.getPenaltyFrequency().getId());
        Assert.assertEquals(glCode.getGlcodeId(), penalty.getGlCode().getGlcodeId());
        Assert.assertEquals(formulaEntity.getId(), ((RatePenaltyBO) penalty).getFormula().getId());
        Assert.assertEquals(rate, ((RatePenaltyBO) penalty).getRate());

        StaticHibernateUtil.flushSession();
    }

    private GLCodeEntity getGLCode(String id) {
        return (GLCodeEntity) TestObjectFactory.getObject(GLCodeEntity.class, Short.valueOf(id));

    }
}
