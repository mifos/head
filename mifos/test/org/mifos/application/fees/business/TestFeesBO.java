/**

 * TestFeesBO.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesBO extends TestCase {

	public TestFeesBO() {
		super();
	}

	public TestFeesBO(String names) {
		super(names);
	}

	public void testCreateOneTimeFees() throws NumberFormatException, Exception {
		FeesBO fees = buildOneTimeFees("Customer One Time", Double
				.valueOf("100"), FeesConstants.UPFRONT, Short
				.valueOf(FeesConstants.CLIENT), false);
		fees.save(FeesConstants.NO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer One Time");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.CLIENT));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The fee amount entered :",
				fees.getFeeAmount().toString(), "100.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 0);
	}

	public void testCreateOneTimeAdminFees() throws NumberFormatException,
			Exception {
		FeesBO fees = buildOneTimeFees("Customer One Time Admin Fee", Double
				.valueOf("25"), FeesConstants.UPFRONT, Short
				.valueOf(FeesConstants.CLIENT), false);
		fees.save(FeesConstants.YES);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer One Time Admin Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.CLIENT));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("25"));
		assertEquals("The fee amount entered :",
				fees.getFeeAmount().toString(), "25.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 1);
	}

	public void testSuccessfulCreatePeriodicFee() throws NumberFormatException,
			Exception {
		FeesBO fees = buildPeriodicFees("Customer Periodic Fee", Double
				.valueOf("25"), 1, 2,
				Short.valueOf(FeesConstants.ALLCUSTOMERS), false);
		fees.save(FeesConstants.YES);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Customer Periodic Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("25"));
		assertEquals("The fee amount entered :",
				fees.getFeeAmount().toString(), "25.0");
		assertEquals("The rate entered :", fees.getRate(), null);
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.PERIODIC);
		assertEquals("The size of the fee level is :", fees.getFeeLevels()
				.size(), 3);

	}

	public void testSuccessfulCreatePeriodicFeeWithFormula()
			throws NumberFormatException, Exception {
		FeesBO fees = buildPeriodicFees("Loan Periodic Fee", Double
				.valueOf("20"), 1, 2, Short.valueOf(FeesConstants.LOAN), true);
		fees.save(FeesConstants.NO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"Loan Periodic Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.LOAN));
		assertEquals("The rate entered :", fees.getRateOrAmount(), Double
				.valueOf("20"));
		assertEquals("The rate entered :", fees.getRate(), Double.valueOf("20"));
		assertEquals("The rate flag should be 1:", fees.getRateFlatFlag(),
				Short.valueOf("1"));
		assertEquals("The formula id should be 1:", fees.getFeeFormula()
				.getFeeFormulaId(), Short.valueOf("1"));
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.PERIODIC);
	}

	public void testSuccessfulUpdateWithRate() throws NumberFormatException,
			Exception {
		FeesBO fees = buildPeriodicFees("Loan Periodic Fee", Double
				.valueOf("20"), 1, 2, Short.valueOf(FeesConstants.LOAN), true);
		fees.save(FeesConstants.NO);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The status of the fees should be active :", fees
				.getFeeStatus().getStatusId(), FeesConstants.STATUS_ACTIVE);
		assertEquals("The rate of the fees:", fees.getRate(), Double
				.valueOf("20"));

		UserContext userContext = TestObjectFactory.getUserContext();
		fees.setUserContext(userContext);
		fees.setFeeStatus(new FeeStatusEntity(FeesConstants.STATUS_INACTIVE));
		fees.setRate(Double.valueOf("25"));
		fees.setRateOrAmount(Double.valueOf("25"));
		fees.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The status of the fees should be inactive :", fees
				.getFeeStatus().getStatusId(), FeesConstants.STATUS_INACTIVE);
		assertEquals("The rate of the fees:", fees.getRate(), Double
				.valueOf("25"));

	}

	public void testSuccessfulUpdateWithAmount() throws NumberFormatException,
			Exception {
		FeesBO fees = buildPeriodicFees("Customer Periodic Fee", Double
				.valueOf("25"), 1, 2,
				Short.valueOf(FeesConstants.ALLCUSTOMERS), false);
		fees.save(FeesConstants.YES);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The status of the fees should be active :", fees
				.getFeeStatus().getStatusId(), FeesConstants.STATUS_ACTIVE);
		assertEquals("The amount of the fees:", fees.getFeeAmount().toString(),
				"25.0");

		UserContext userContext = TestObjectFactory.getUserContext();
		fees.setUserContext(userContext);
		fees.setFeeStatus(new FeeStatusEntity(FeesConstants.STATUS_INACTIVE));
		fees.setAmount("50");
		fees.setRateOrAmount(Double.valueOf("50"));
		fees.update();

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The status of the fees should be inactive :", fees
				.getFeeStatus().getStatusId(), FeesConstants.STATUS_INACTIVE);
		assertEquals("The amount of the fees:", fees.getFeeAmount().toString(),
				"50.0");

	}

	private FeesBO buildPeriodicFees(String feeName, Double feeRateOrAmnt,
			int frequency, int recurAfter, Short feeCategory, boolean rateFlag)
			throws Exception {
		FeesBO fees = new FeesBO(TestObjectFactory.getUserContext());
		fees.setFeeName(feeName);
		FeeFrequencyEntity feeFrequency = new FeeFrequencyEntity();
		fees.setFeeFrequency(feeFrequency);
		fees.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeesConstants.PERIODIC);
		MeetingBO feeMeetingFrequency = TestObjectFactory.getMeetingHelper(
				frequency, recurAfter, 5);
		fees.getFeeFrequency().setFeeMeetingFrequency(feeMeetingFrequency);
		CategoryTypeEntity categoryType = new CategoryTypeEntity();
		fees.setCategoryType(categoryType);
		fees.getCategoryType().setCategoryId(feeCategory);
		fees.setRateFlat(rateFlag);
		if (rateFlag) {
			fees.setRate(feeRateOrAmnt);
			fees.setAmount("");
			FeeFormulaEntity feeFormulaEntity = new FeeFormulaEntity();
			fees.setFeeFormula(feeFormulaEntity);
			fees.getFeeFormula().setFeeFormulaId(Short.valueOf("1"));
		} else
			fees.setAmount(String.valueOf(feeRateOrAmnt));
		fees.setRateOrAmount(feeRateOrAmnt);
		GLCodeEntity glCodeFee = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, Short.valueOf("24"));
		fees.setGlCodeEntity(glCodeFee);

		return fees;
	}

	private FeesBO buildOneTimeFees(String feeName, Double feeRateOrAmnt,
			Short timeOfCharge, Short feeCategory, boolean rateFlag)
			throws Exception {
		FeesBO fees = new FeesBO(TestObjectFactory.getUserContext());
		FeeFrequencyEntity feeFrequency = new FeeFrequencyEntity();
		fees.setFeeFrequency(feeFrequency);
		fees.setFeeName(feeName);
		fees.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeesConstants.ONETIME);
		fees.getFeeFrequency().getFeePayment().setFeePaymentId(timeOfCharge);
		CategoryTypeEntity categoryType = new CategoryTypeEntity();
		fees.setCategoryType(categoryType);
		fees.getCategoryType().setCategoryId(feeCategory);
		fees.setRateFlat(rateFlag);
		if (rateFlag) {
			fees.setRate(feeRateOrAmnt);
			fees.setAmount("");
			FeeFormulaEntity feeFormulaEntity = new FeeFormulaEntity();
			fees.setFeeFormula(feeFormulaEntity);
			fees.getFeeFormula().setFeeFormulaId(Short.valueOf("1"));
		} else
			fees.setAmount(String.valueOf(feeRateOrAmnt));
		fees.setRateOrAmount(feeRateOrAmnt);
		GLCodeEntity glCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, Short.valueOf("7"));
		fees.setGlCodeEntity(glCodeEntity);
		return fees;
	}

}
