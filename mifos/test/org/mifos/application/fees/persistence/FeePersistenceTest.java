package org.mifos.application.fees.persistence;

import java.util.List;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.ApplicableAccountsTypeEntity;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FeePersistenceTest extends MifosTestCase {

	private FeePersistence feePersistence = new FeePersistence();

	private FeeBO fee1;

	private FeeBO fee2;

	FeeBO periodicFee;

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(fee1);
		TestObjectFactory.removeObject(fee2);
		TestObjectFactory.removeObject(periodicFee);
		super.tearDown();
	}
	
	public void testGetUpdatedFeesForCustomer() throws Exception{
		
		// crate periodic fee
		periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"ClientPeridoicFee", FeeCategory.CLIENT, "5", RecurrenceType.WEEKLY, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		//get fee from db 
		periodicFee =(FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,periodicFee.getFeeId());
		periodicFee.setUserContext(TestObjectFactory.getUserContext());
		periodicFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		periodicFee.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		List fees = feePersistence.getUpdatedFeesForCustomer();
		assertNotNull(fees);
		assertEquals(1,fees.size());

		//cleanup
		periodicFee =(FeeBO) TestObjectFactory.getObject(FeeBO.class,periodicFee.getFeeId());
	}
	
	public void testGetUpdateTypeEntity() throws NumberFormatException, PersistenceException{
		
	  ApplicableAccountsTypeEntity feeUpdateType=	feePersistence.getUpdateTypeEntity(Short.valueOf("1"));
	  assertNotNull(feeUpdateType);
	  assertEquals(1,feeUpdateType.getId().intValue());
		
	}
	
	public void testRetrieveFeesForCustomer()throws Exception{
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200", RecurrenceType.MONTHLY,Short.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400", RecurrenceType.MONTHLY,Short.valueOf("2"));
		List<FeeBO> feeList = feePersistence.retrieveCustomerFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("CustomerFee1",feeList.get(0).getFeeName());
	}
	
	public void testRetrieveFeesForProduct()throws Exception{
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1", FeeCategory.CENTER, "200", RecurrenceType.MONTHLY,Short.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1", FeeCategory.LOAN, "400", RecurrenceType.MONTHLY,Short.valueOf("2"));
		List<FeeBO> feeList = feePersistence.retrieveProductFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("ProductFee1",feeList.get(0).getFeeName());
	}
}
