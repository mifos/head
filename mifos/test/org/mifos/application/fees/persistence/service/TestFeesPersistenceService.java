/**

 * TestFeesPersistenceService.java    version: xxx



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

package org.mifos.application.fees.persistence.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeFrequencyEntity;
import org.mifos.application.fees.business.FeeUpdateTypeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesPersistenceService extends MifosTestCase {

	public void testSave() throws Exception {

		FeesBO fees = buildFees();
		new FeePersistenceService().save(fees);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		fees = (FeesBO) TestObjectFactory.getObject(FeesBO.class, fees
				.getFeeId());
		assertEquals("One time fees", fees.getFeeName());
		assertEquals(FeeCategory.CLIENT.getValue(), fees.getCategoryType()
				.getCategoryId());
		assertFalse(fees.isRateFee());
		assertTrue(fees.isOneTime());
	}

	public void testGetFees() {
		FeesBO fees = TestObjectFactory.createOneTimeFees("One Time Fee",
				100.0, Short.valueOf("1"), 1);
		fees = new FeePersistenceService().getFees(fees.getFeeId());

		assertEquals("One Time Fee", fees.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fees
				.getCategoryType().getCategoryId());
		assertTrue(fees.isOneTime());
	}

	private FeesBO buildFees() throws Exception {
		UserContext userContext = TestObjectFactory.getUserContext();
		FeesBO fees = new FeesBO(userContext);
		fees.setFeeName("One time fees");

		fees.setFeeFrequency(new FeeFrequencyEntity());
		fees.setCategoryType(new CategoryTypeEntity());
		fees.getFeeFrequency().getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.ONETIME.getValue());
		fees.getFeeFrequency().getFeePayment().setFeePaymentId(
				FeePayment.UPFRONT.getValue());
		fees.getCategoryType().setCategoryId(FeeCategory.CLIENT.getValue());
		fees.setRateFee(false);
		fees.setAmount("100.0");
		fees.setGlCodeEntity((GLCodeEntity) HibernateUtil.getSessionTL().get(
				GLCodeEntity.class, Short.valueOf("7")));

		fees.setCreatedDate(new Date());
		fees.setCreatedBy(userContext.getId());
		fees.modifyStatus(FeeStatus.ACTIVE);
		fees.setOffice(TestObjectFactory.getOffice(userContext.getBranchId()));
		fees.getFeeFrequency().buildFeeFrequency();
		return fees;
	}

	public void testGetUpdatedFeesForCustomer() throws Exception{

		// crate periodic fee
		FeesBO periodicFee = TestObjectFactory.createPeriodicFees(
				"ClientPeridoicFee", 5.0, 1, 1, 2);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		//get fee from db 
		periodicFee =(FeesBO) HibernateUtil.getSessionTL().get(FeesBO.class,periodicFee.getFeeId());
		periodicFee.setUserContext(TestObjectFactory.getUserContext());
		periodicFee.setUpdateFlag(Short.valueOf("1"));
		periodicFee.save(false);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		List fees = new FeePersistenceService().getUpdatedFeesForCustomer();
		assertNotNull(fees);
		assertEquals(1,fees.size());

		//cleanup
		periodicFee =(FeesBO) HibernateUtil.getSessionTL().get(FeesBO.class,periodicFee.getFeeId());
		TestObjectFactory.removeObject(periodicFee);
		
	}
	
	public void testGetUpdateTypeEntity(){
		
		  FeeUpdateTypeEntity feeUpdateType=	new FeePersistenceService().getUpdateTypeEntity(Short.valueOf("1"));
		  assertNotNull(feeUpdateType);
		  assertEquals(1,feeUpdateType.getId().intValue());
			
		}
}
