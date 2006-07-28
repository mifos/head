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

import java.util.List;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.ApplicableAccountsTypeEntity;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeesPersistenceService extends MifosTestCase {
	private FeePersistenceService feePersistenceService = new FeePersistenceService();
	private FeeBO fee1;
	private FeeBO fee2;
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(fee1);
		TestObjectFactory.removeObject(fee2);
	}
	
	public void testGetFees() {
		fee1 = TestObjectFactory.createOneTimeAmountFee("One Time Fee",
				FeeCategory.ALLCUSTOMERS,"100", FeePayment.UPFRONT);
		fee1 = feePersistenceService.getFees(fee1.getFeeId());

		assertEquals("One Time Fee", fee1.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee1
				.getCategoryType().getId());
		assertTrue(fee1.isOneTime());
	}

	public void testGetUpdatedFeesForCustomer() throws Exception{

		// crate periodic fee
		fee1 = TestObjectFactory.createPeriodicAmountFee(
				"ClientPeridoicFee", FeeCategory.CLIENT, "5", MeetingFrequency.WEEKLY, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		//get fee from db 
		fee1 =(FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,fee1.getFeeId());
		fee1.setUserContext(TestObjectFactory.getUserContext());
		fee1.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		fee1.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		List fees = feePersistenceService.getUpdatedFeesForCustomer();
		assertNotNull(fees);
		assertEquals(1,fees.size());
		fee1 = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee1.getFeeId());
	}
	
	public void testGetUpdateTypeEntity(){
		  ApplicableAccountsTypeEntity feeUpdateType=feePersistenceService.getUpdateTypeEntity(Short.valueOf("1"));
		  assertNotNull(feeUpdateType);
		  assertEquals(1,feeUpdateType.getId().intValue());
	}
	
	public void testRetrieveFeesForCustomer()throws Exception{
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200", MeetingFrequency.MONTHLY,Short.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400", MeetingFrequency.MONTHLY,Short.valueOf("2"));
		List<FeeBO> feeList = feePersistenceService.retrieveCustomerFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("CustomerFee1",feeList.get(0).getFeeName());
	}
	
	public void testRetrieveFeesForProduct()throws Exception{
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200", MeetingFrequency.MONTHLY,Short.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400", MeetingFrequency.MONTHLY,Short.valueOf("2"));
		List<FeeBO> feeList = feePersistenceService.retrieveProductFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("ProductFee1",feeList.get(0).getFeeName());
	}
}
