/**

 * TestFeesPersistenceService.java    version: xxx



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

package org.mifos.application.fees.persistence.service;

import java.util.Date;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeFrequencyEntity;
import org.mifos.application.fees.business.FeeStatusEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesPersistenceService extends TestCase {

	public void testSave() throws Exception {

		FeesBO fees = buildFees();
		new FeePersistenceService().save(fees);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("The fee name entered :", fees.getFeeName(),
				"One time fees");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.CLIENT));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The rate flag should be 1:", fees.getRateFlatFlag(),
				Short.valueOf("0"));
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
	}

	public void testGetFees() {
		FeesBO fees = TestObjectFactory.createOneTimeFees("One Time Fee",
				100.0, Short.valueOf("1"), 1);
		fees = new FeePersistenceService().getFees(fees.getFeeId());

		assertEquals("The fee name entered :", fees.getFeeName(),
				"One Time Fee");
		assertEquals("The category entered :", fees.getCategoryType()
				.getCategoryId(), Short.valueOf(FeesConstants.ALLCUSTOMERS));
		assertEquals("The amount entered :", fees.getRateOrAmount(), Double
				.valueOf("100"));
		assertEquals("The frequency of the fees :", fees.getFeeFrequency()
				.getFeeFrequencyType().getFeeFrequencyTypeId(),
				FeesConstants.ONETIME);
	}

	private FeesBO buildFees() throws Exception {
		UserContext userContext = TestObjectFactory.getUserContext();
		FeesBO fees = new FeesBO(userContext);
		FeeFrequencyEntity feeFrequency = new FeeFrequencyEntity();
		fees.setFeeFrequency(feeFrequency);
		fees.setFeeName("One time fees");
		fees.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeesConstants.ONETIME);
		fees.getFeeFrequency().getFeePayment().setFeePaymentId(
				FeesConstants.UPFRONT);
		CategoryTypeEntity categoryType = new CategoryTypeEntity();
		fees.setCategoryType(categoryType);
		fees.getCategoryType().setCategoryId(
				Short.valueOf(FeesConstants.CLIENT));
		fees.setRateFlat(false);
		fees.setAmount("100.0");
		fees.setRateOrAmount(Double.valueOf(100));
		GLCodeEntity glCodeEntity = (GLCodeEntity) HibernateUtil.getSessionTL()
				.get(GLCodeEntity.class, Short.valueOf("7"));
		fees.setGlCodeEntity(glCodeEntity);

		fees.setCreatedDate(new Date());
		fees.setCreatedBy(userContext.getId());
		fees.modifyStatus(FeesConstants.STATUS_ACTIVE);
		fees.setOffice(TestObjectFactory.getOffice(userContext.getBranchId()));
		fees.getFeeFrequency().buildFeeFrequency();
		return fees;
	}

}
