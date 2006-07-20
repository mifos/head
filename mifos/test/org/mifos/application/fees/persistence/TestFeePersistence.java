package org.mifos.application.fees.persistence;

import java.util.List;

import org.mifos.application.fees.business.FeeUpdateTypeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeePersistence extends MifosTestCase {

	FeePersistence feePersistence = new FeePersistence();

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
		List fees = feePersistence.getUpdatedFeesForCustomer();
		assertNotNull(fees);
		assertEquals(1,fees.size());

		//cleanup
		periodicFee =(FeesBO) HibernateUtil.getSessionTL().get(FeesBO.class,periodicFee.getFeeId());
		TestObjectFactory.removeObject(periodicFee);
		
	}
	
	public void testGetUpdateTypeEntity(){
		
	  FeeUpdateTypeEntity feeUpdateType=	feePersistence.getUpdateTypeEntity(Short.valueOf("1"));
	  assertNotNull(feeUpdateType);
	  assertEquals(1,feeUpdateType.getId().intValue());
		
	}

}
