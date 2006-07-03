/**

 * TestFeesBO.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.application.fees.business;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesBO extends TestCase {

	private final Short formulaId = (short) 1;

	private final Short glCodeId = (short) 7;

	public void testCreateOneTimeFees() throws NumberFormatException, Exception {
		FeesBO fee = buildOneTimeFees("Customer One Time", 100.0,
				FeePayment.UPFRONT, FeeCategory.CLIENT, false);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer One Time", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.CLIENT.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("100.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isOneTime());
		assertFalse(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testCreateOneTimeAdminFees() throws NumberFormatException,
			Exception {
		FeesBO fee = buildOneTimeFees("Customer One Time Admin Fee", 25.0,
				FeePayment.UPFRONT, FeeCategory.CLIENT, false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer One Time Admin Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.CLIENT.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("25.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isOneTime());
		assertTrue(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFee() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Customer Periodic Fee", 25.0, 1, 2,
				FeeCategory.ALLCUSTOMERS, false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Customer Periodic Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals("25.0", fee.getFeeAmount().toString());
		assertNull(fee.getRate());
		assertTrue(fee.isPeriodic());
		assertTrue(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulCreatePeriodicFeeWithFormula()
			throws NumberFormatException, Exception {
		FeesBO fee = buildPeriodicFees("Loan Periodic Fee", 20.0, 1, 2,
				FeeCategory.LOAN, true);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("Loan Periodic Fee", fee.getFeeName());
		assertEquals(Short.valueOf(FeeCategory.LOAN.getValue()), fee
				.getCategoryType().getCategoryId());
		assertEquals(20.0, fee.getRate());
		assertTrue(fee.isRateFlat());
		assertEquals(formulaId, fee.getFeeFormula().getFeeFormulaId());
		assertTrue(fee.isPeriodic());
		assertFalse(fee.isAdminFee());
		assertTrue(fee.isActive());
	}

	public void testSuccessfulUpdateWithRate() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Loan Periodic Fee", 20.0, 1, 2,
				FeeCategory.LOAN, true);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertTrue(fee.isActive());
		assertEquals(20.0, fee.getRate());

		UserContext userContext = TestObjectFactory.getUserContext();
		fee.setUserContext(userContext);
		fee.modifyStatus(FeeStatus.INACTIVE);
		fee.setRate(25.0);
		fee.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertFalse(fee.isActive());
		assertEquals(25.0, fee.getRate());

	}

	public void testSuccessfulUpdateWithAmount() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Customer Periodic Fee", 25.0, 1, 2,
				FeeCategory.ALLCUSTOMERS, false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertTrue(fee.isActive());
		assertEquals("25.0", fee.getFeeAmount().toString());

		fee.setUserContext(TestObjectFactory.getUserContext());
		fee.modifyStatus(FeeStatus.INACTIVE);
		fee.setAmount("50");
		fee.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertFalse(fee.isActive());
		assertEquals("50.0", fee.getFeeAmount().toString());

	}

	private FeesBO buildPeriodicFees(String feeName, Double feeRateOrAmnt,
			int frequency, int recurAfter, FeeCategory feeCategory,
			boolean rateFlag) throws Exception {
		FeesBO fee = buildFees(feeName, feeCategory, rateFlag, feeRateOrAmnt);
		fee.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.PERIODIC.getValue());
		MeetingBO feeMeetingFrequency = TestObjectFactory.getMeetingHelper(
				frequency, recurAfter, 5);
		fee.getFeeFrequency().setFeeMeetingFrequency(feeMeetingFrequency);
		return fee;
	}

	private FeesBO buildOneTimeFees(String feeName, Double feeRateOrAmnt,
			FeePayment timeOfCharge, FeeCategory feeCategory, boolean rateFlag)
			throws Exception {
		FeesBO fee = buildFees(feeName, feeCategory, rateFlag, feeRateOrAmnt);
		fee.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.ONETIME.getValue());
		fee.getFeeFrequency().getFeePayment().setFeePaymentId(
				timeOfCharge.getValue());
		return fee;
	}

	private FeesBO buildFees(String feeName, FeeCategory feeCategory,
			boolean rateFlag, Double feeRateOrAmnt) throws Exception {
		FeesBO fee = new FeesBO(TestObjectFactory.getUserContext());
		fee.setFeeName(feeName);
		fee.setFeeFrequency(new FeeFrequencyEntity());
		fee.setCategoryType(new CategoryTypeEntity());
		fee.getCategoryType().setCategoryId(
				Short.valueOf(feeCategory.getValue()));
		fee.setRateFlat(rateFlag);
		if (rateFlag) {
			fee.setRate(feeRateOrAmnt);
			fee.setAmount("");
			fee.setFeeFormula(new FeeFormulaEntity());
			fee.getFeeFormula().setFeeFormulaId(formulaId);
		} else
			fee.setAmount(String.valueOf(feeRateOrAmnt));
		fee.setGlCodeEntity((GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, glCodeId));
		return fee;
	}

}
