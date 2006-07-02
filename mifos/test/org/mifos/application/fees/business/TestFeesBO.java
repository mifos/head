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
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesBO extends TestCase {

	public void testCreateOneTimeFees() throws NumberFormatException, Exception {
		FeesBO fee = buildOneTimeFees("Customer One Time", 100.0,
				FeePayment.UPFRONT.getValue(), Short.valueOf(FeeCategory.CLIENT
						.getValue()), false);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The fee name entered :", fee.getFeeName(),
				"Customer One Time");
		assertEquals("The category entered :", fee.getCategoryType()
				.getCategoryId(), Short.valueOf(FeeCategory.CLIENT.getValue()));
		assertEquals("The fee amount entered :", fee.getFeeAmount().toString(),
				"100.0");
		assertEquals("The rate entered :", fee.getRate(), null);
		assertEquals("The frequency of the fees :", fee.isOneTime(), true);
		assertEquals("The size of the fee level is :", fee.isAdminFee(), false);
		assertEquals(fee.isActive(), true);
	}

	public void testCreateOneTimeAdminFees() throws NumberFormatException,
			Exception {
		FeesBO fee = buildOneTimeFees("Customer One Time Admin Fee", 25.0,
				FeePayment.UPFRONT.getValue(), Short.valueOf(FeeCategory.CLIENT
						.getValue()), false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The fee name entered :", fee.getFeeName(),
				"Customer One Time Admin Fee");
		assertEquals("The category entered :", fee.getCategoryType()
				.getCategoryId(), Short.valueOf(FeeCategory.CLIENT.getValue()));
		assertEquals("The fee amount entered :", fee.getFeeAmount().toString(),
				"25.0");
		assertEquals("The rate entered :", fee.getRate(), null);
		assertEquals("The frequency of the fees :", fee.isOneTime(), true);
		assertEquals("The size of the fee level is :", fee.isAdminFee(), true);
		assertEquals(fee.isActive(), true);
	}

	public void testSuccessfulCreatePeriodicFee() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Customer Periodic Fee", 25.0, 1, 2,
				Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The fee name entered :", fee.getFeeName(),
				"Customer Periodic Fee");
		assertEquals("The category entered :", fee.getCategoryType()
				.getCategoryId(), Short.valueOf(FeeCategory.ALLCUSTOMERS
				.getValue()));
		assertEquals("The fee amount entered :", fee.getFeeAmount().toString(),
				"25.0");
		assertEquals("The rate entered :", fee.getRate(), null);
		assertEquals("The frequency of the fees :", fee.isPeriodic(), true);
		assertEquals("The size of the fee level is :", fee.isAdminFee(), true);
		assertEquals(fee.isActive(), true);
	}

	public void testSuccessfulCreatePeriodicFeeWithFormula()
			throws NumberFormatException, Exception {
		FeesBO fee = buildPeriodicFees("Loan Periodic Fee", 20.0, 1, 2, Short
				.valueOf(FeeCategory.LOAN.getValue()), true);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The fee name entered :", fee.getFeeName(),
				"Loan Periodic Fee");
		assertEquals("The category entered :", fee.getCategoryType()
				.getCategoryId(), Short.valueOf(FeeCategory.LOAN.getValue()));
		assertEquals("The rate entered :", fee.getRate(), 20.0);
		assertEquals("The rate flag should be 1:", fee.isRateFlat(), true);
		assertEquals("The formula id should be 1:", fee.getFeeFormula()
				.getFeeFormulaId(), Short.valueOf("1"));
		assertEquals("The frequency of the fees :", fee.isPeriodic(), true);
		assertEquals("The size of the fee level is :", fee.isAdminFee(), false);
		assertEquals(fee.isActive(), true);
	}

	public void testSuccessfulUpdateWithRate() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Loan Periodic Fee", 20.0, 1, 2, Short
				.valueOf(FeeCategory.LOAN.getValue()), true);
		fee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The status of the fees should be active :", fee
				.isActive(), true);
		assertEquals("The rate of the fees:", fee.getRate(), 20.0);

		UserContext userContext = TestObjectFactory.getUserContext();
		fee.setUserContext(userContext);
		fee.modifyStatus(FeeStatus.INACTIVE);
		fee.setRate(25.0);
		fee.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The status of the fees should be inactive :", fee
				.isActive(), false);
		assertEquals("The rate of the fees:", fee.getRate(), 25.0);

	}

	public void testSuccessfulUpdateWithAmount() throws NumberFormatException,
			Exception {
		FeesBO fee = buildPeriodicFees("Customer Periodic Fee", 25.0, 1, 2,
				Short.valueOf(FeeCategory.ALLCUSTOMERS.getValue()), false);
		fee.save(true);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The status of the fees should be active :", fee
				.isActive(), true);
		assertEquals("The amount of the fees:", fee.getFeeAmount().toString(),
				"25.0");

		UserContext userContext = TestObjectFactory.getUserContext();
		fee.setUserContext(userContext);
		fee.modifyStatus(FeeStatus.INACTIVE);
		fee.setAmount("50");
		fee.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fee = (FeesBO) TestObjectFactory
				.getObject(FeesBO.class, fee.getFeeId());
		assertEquals("The status of the fees should be inactive :", fee
				.isActive(), false);
		assertEquals("The amount of the fees:", fee.getFeeAmount().toString(),
				"50.0");

	}

	private FeesBO buildPeriodicFees(String feeName, Double feeRateOrAmnt,
			int frequency, int recurAfter, Short feeCategory, boolean rateFlag)
			throws Exception {
		FeesBO fee = buildFees(feeName, feeCategory, rateFlag, feeRateOrAmnt);
		fee.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.PERIODIC.getValue());
		MeetingBO feeMeetingFrequency = TestObjectFactory.getMeetingHelper(
				frequency, recurAfter, 5);
		fee.getFeeFrequency().setFeeMeetingFrequency(feeMeetingFrequency);
		return fee;
	}

	private FeesBO buildOneTimeFees(String feeName, Double feeRateOrAmnt,
			Short timeOfCharge, Short feeCategory, boolean rateFlag)
			throws Exception {
		FeesBO fee = buildFees(feeName, feeCategory, rateFlag, feeRateOrAmnt);
		fee.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.ONETIME.getValue());
		fee.getFeeFrequency().getFeePayment().setFeePaymentId(timeOfCharge);
		return fee;
	}

	private FeesBO buildFees(String feeName, Short feeCategory,
			boolean rateFlag, Double feeRateOrAmnt) throws Exception {
		FeesBO fee = new FeesBO(TestObjectFactory.getUserContext());
		fee.setFeeName(feeName);
		fee.setFeeFrequency(new FeeFrequencyEntity());
		fee.setCategoryType(new CategoryTypeEntity());
		fee.getCategoryType().setCategoryId(feeCategory);
		fee.setRateFlat(rateFlag);
		if (rateFlag) {
			fee.setRate(feeRateOrAmnt);
			fee.setAmount("");
			fee.setFeeFormula(new FeeFormulaEntity());
			fee.getFeeFormula().setFeeFormulaId(Short.valueOf("1"));
		} else
			fee.setAmount(String.valueOf(feeRateOrAmnt));
		fee.setGlCodeEntity((GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, Short.valueOf("7")));
		return fee;
	}

}
