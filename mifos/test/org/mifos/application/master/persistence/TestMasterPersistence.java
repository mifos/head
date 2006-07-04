package org.mifos.application.master.persistence;

import java.util.List;

import junit.framework.TestCase;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestMasterPersistence extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testEntityMasterRetrieval() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		EntityMaster paymentTypes = masterPersistence
				.getLookUpEntity(
						MasterConstants.PAYMENT_TYPE,
						Short.valueOf("1"),
						"org.mifos.application.productdefinition.util.valueobjects.PaymentType",
						"paymentTypeId");
		List<LookUpMaster> paymentValues = paymentTypes.getLookUpMaster();
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentValues.size());

	}
	
	public void testRetrievePaymentTypes()throws Exception{
		MasterPersistence masterPersistence = new MasterPersistence();
		List<PaymentTypeEntity> paymentTypeList = masterPersistence.retrievePaymentTypes(Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
	
	public void testGetSupportedPaymentModes()throws Exception{
		MasterPersistence masterPersistence = new MasterPersistence();				
		List<PaymentTypeEntity> paymentTypeList = masterPersistence.getSupportedPaymentModes(Short.valueOf("1"),Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
}
