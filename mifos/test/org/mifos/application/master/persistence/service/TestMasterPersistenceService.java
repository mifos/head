package org.mifos.application.master.persistence.service;

import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestMasterPersistenceService extends MifosTestCase {
	MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testRetrieveMasterData() throws Exception {
		EntityMaster paymentTypes = masterPersistenceService
				.retrieveMasterData(
						MasterConstants.PAYMENT_TYPE,
						Short.valueOf("1"),
						"org.mifos.application.productdefinition.util.valueobjects.PaymentType",
						"paymentTypeId");
		List<LookUpMaster> paymentValues = paymentTypes.getLookUpMaster();
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentValues.size());

	}
	
	public void testRetrievePaymentTypes()throws Exception{
		List<PaymentTypeEntity> paymentTypeList = masterPersistenceService.retrievePaymentTypes(Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
	
	public void testGetSupportedPaymentModes()throws Exception{
		List<PaymentTypeEntity> paymentTypeList = masterPersistenceService.getSupportedPaymentModes(Short.valueOf("1"),Short.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER,paymentTypeList.size());
	}
}
