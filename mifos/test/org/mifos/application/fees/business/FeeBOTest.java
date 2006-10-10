/**

 * FeeBOTest.java    version: xxx



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

import java.util.Date;
import java.util.Set;

import org.hibernate.usertype.UserCollectionType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeLevel;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeeBOTest extends MifosTestCase {

	private FeeBO fee;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(fee);
		HibernateUtil.closeSession();
	}

	public void testCreateWithoutFeeName() throws  Exception {
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),"",new CategoryTypeEntity(FeeCategory.CENTER),
					new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 
					TestObjectFactory.getMoneyForMFICurrency("100"), false ,
					new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without fee name", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_NAME);
		}
	}
	
	public void testCreateWithoutFeeCategory() throws  Exception {
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",null, 
					new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 
					TestObjectFactory.getMoneyForMFICurrency("100"), 
					false ,new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without fee category", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_CATEGORY);
		}
	}
	
	public void testCreateFeeWithoutFeeFrequency() throws Exception {
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",
					new CategoryTypeEntity(FeeCategory.CENTER), null, getGLCode("7"), 
					TestObjectFactory.getMoneyForMFICurrency("100"), false, 
					new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without frequency type", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_FREQUENCY_TYPE);
		}
	}
	
	public void testCreateAmountFeeWithoutAmount() throws  Exception {
		try {
			fee = new AmountFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",
					new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), 
					getGLCode("7"), null, false , new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without Amount", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_AMOUNT);
		}
	}
	
	public void testCreateRateFeeWithoutRate() throws  Exception {
		try {
			FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);
			fee = new RateFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",
					new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
					getGLCode("7"), null, feeFormula, false ,new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without Rate", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
		}
	}
	
	public void testCreateRateFeeWithoutFormula() throws  Exception {
		try {
			fee = new RateFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",
					new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
					getGLCode("7"), 2.0, null,  false ,new FeePaymentEntity(FeePayment.UPFRONT));
			assertFalse("Fee is created without Formula", true);
		} catch (FeeException e) {
			assertNull(fee);
			assertEquals(e.getKey(),FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
		}
	}
	
	public void testCreateOneTimeAmountFee() throws Exception {
		String name ="Customer_OneTime_AmountFee";
		fee = createOneTimeAmountFee(name,FeeCategory.CENTER, "100", false, FeePayment.UPFRONT);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals(name, fee.getFeeName());
		assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
	}

	public void testCreateOneTimeRateFee() throws Exception {
		fee = createOneTimeRateFee("Customer_OneTime_RateFee",FeeCategory.CENTER, 100.0, FeeFormula.AMOUNT, false, FeePayment.UPFRONT);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals("Customer_OneTime_RateFee", fee.getFeeName());
		assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
	}
	
	public void testCreatePeriodicAmountFee() throws Exception {
		MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.FEEMEETING);
		fee = createPeriodicAmountFee("Customer_Periodic_AmountFee", FeeCategory.CENTER, "100", false, feefrequency);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals("Customer_Periodic_AmountFee", fee.getFeeName());
		assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
	}
	
	public void testCreatePeriodicRateFee() throws Exception {
		MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.FEEMEETING);
		fee = createPeriodicRateFee ("Customer_Periodic_RateFee",FeeCategory.CENTER, 100.0, FeeFormula.AMOUNT, false, feefrequency);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals("Customer_Periodic_RateFee", fee.getFeeName());
		assertEquals(FeeCategory.CENTER.getValue(), fee.getCategoryType().getId());
	}

	public void testCreateOneTimeDefaultFee()throws Exception{
		fee = createOneTimeAmountFee("Customer_OneTime_DefaultFee",FeeCategory.GROUP, "100", true, FeePayment.UPFRONT);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals("Customer_OneTime_DefaultFee", fee.getFeeName());
		assertEquals(FeeCategory.GROUP.getValue(), fee.getCategoryType().getId());
		assertTrue(fee.isCustomerDefaultFee());
		assertTrue(vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));
	}
	
	
	public void testCreatePeriodicDefaultFee()throws Exception{
		MeetingBO feefrequency = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.FEEMEETING);
		fee = createPeriodicRateFee ("Customer_Periodic_DefaultFee",FeeCategory.ALLCUSTOMERS, 100.0, FeeFormula.AMOUNT, true, feefrequency);
		fee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		fee = (FeeBO) TestObjectFactory.getObject(FeeBO.class, fee.getFeeId());
		assertEquals("Customer_Periodic_DefaultFee", fee.getFeeName());
		assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), fee.getCategoryType().getId());
		assertEquals(true, fee.isCustomerDefaultFee());
		assertTrue(vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));
	}
	public void testSaveFailure() throws Exception {
		
		try{
		FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);

		FeeBO fees = new RateFeeBO(TestObjectFactory.getUserContext(),"Customer Fee",
				new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				getGLCode("7"), 2.0, feeFormula,  false ,new FeePaymentEntity(FeePayment.UPFRONT));;
		TestObjectFactory.simulateInvalidConnection();
		fees.save();
		fail();
		}
		catch (FeeException e) {
			assertTrue(true);
		}
	}
	public void testCreateFeeFailure() throws Exception {
		
		try{
		FeeFormulaEntity feeFormula = new FeeFormulaEntity(FeeFormula.AMOUNT);
		UserContext uc = TestObjectFactory.getUserContext();
		TestObjectFactory.simulateInvalidConnection();
		 new RateFeeBO(uc,"Customer Fee",
				new CategoryTypeEntity(FeeCategory.CENTER), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME),
				getGLCode("7"), 2.0, feeFormula,  false ,new FeePaymentEntity(FeePayment.UPFRONT));;
		fail();
		}
		catch (FeeException e) {
			assertTrue(true);
		}
	}	
	private boolean vaidateDefaultCustomerFee(Set<FeeLevelEntity> defaultCustomers, FeeCategory feeCategory){
		boolean bCenter = false;
		boolean bGroup = false;
		boolean bClient = false;

		for (FeeLevelEntity feeLevel: defaultCustomers){
			if(feeLevel.getLevelId().equals(FeeLevel.CENTERLEVEL.getValue()))
				bCenter = true;
			if(feeLevel.getLevelId().equals(FeeLevel.GROUPLEVEL.getValue()))
				bGroup = true;
			if(feeLevel.getLevelId().equals(FeeLevel.CLIENTLEVEL.getValue()))
				bClient = true;
		}
		
		if(feeCategory.equals(FeeCategory.CENTER))
			return bCenter && !bGroup && !bClient ;
		
		if(feeCategory.equals(FeeCategory.GROUP))
			return !bCenter && bGroup && !bClient ;
		
		if(feeCategory.equals(FeeCategory.CLIENT))
			return !bCenter && !bGroup && bClient ;
		
		if(feeCategory.equals(FeeCategory.ALLCUSTOMERS))
			return bCenter && bGroup && bClient ;
		
		return !bCenter && !bGroup && !bClient ;
	}
	
	private FeeBO createOneTimeRateFee(String feeName, FeeCategory feeCategory,  Double rate, FeeFormula feeFormula, boolean isDefaultFee, FeePayment feePayment)throws Exception  {
		return new RateFeeBO(TestObjectFactory.getUserContext(), feeName, 
				new CategoryTypeEntity(feeCategory), new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), 
				getGLCode("7"), rate, new FeeFormulaEntity(feeFormula), isDefaultFee, 
				new FeePaymentEntity(feePayment));
	}
	
	private FeeBO createPeriodicRateFee(String feeName, FeeCategory feeCategory, Double rate, FeeFormula feeFormula, boolean isDefaultFee, MeetingBO feeFrequency)  throws Exception{
		return new RateFeeBO(TestObjectFactory.getUserContext(), feeName, new CategoryTypeEntity(feeCategory), 
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), getGLCode("7"), rate, 
				new FeeFormulaEntity(feeFormula), isDefaultFee, feeFrequency);
	}
		
	private FeeBO createOneTimeAmountFee(String feeName, FeeCategory feeCategory, String amount, boolean isDefaultFee, FeePayment feePayment)  throws Exception{
		return new AmountFeeBO(TestObjectFactory.getUserContext(), feeName, new CategoryTypeEntity(feeCategory), 
				new FeeFrequencyTypeEntity(FeeFrequencyType.ONETIME), getGLCode("7"), 
				TestObjectFactory.getMoneyForMFICurrency(amount), isDefaultFee, new FeePaymentEntity(feePayment));
	}
	
	private FeeBO createPeriodicAmountFee(String feeName, FeeCategory feeCategory, String amount, boolean isDefaultFee, MeetingBO feeFrequency)throws Exception{
		return new AmountFeeBO(TestObjectFactory.getUserContext(), feeName, new CategoryTypeEntity(feeCategory), 
				new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC), getGLCode("7"), 
				TestObjectFactory.getMoneyForMFICurrency(amount), isDefaultFee, feeFrequency);
	}
	
	private GLCodeEntity getGLCode(String id){
		GLCodeEntity glCode = new GLCodeEntity();
		glCode.setGlcodeId(Short.valueOf(id));
		return glCode;
	}
}
