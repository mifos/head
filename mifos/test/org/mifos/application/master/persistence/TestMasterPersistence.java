package org.mifos.application.master.persistence;

import java.util.List;

import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestMasterPersistence extends MifosTestCase {
	
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
	
	public void testRetrieveMasterEntities() throws NumberFormatException, PersistenceException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<BusinessActivityEntity> masterEntity = masterPersistence.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES,Short.valueOf("1"));
		assertEquals(129,masterEntity.size());
	}
	
	public void testGetMasterEntityName() throws NumberFormatException, PersistenceException {
		MasterPersistence masterPersistence = new MasterPersistence();
		assertEquals("Partial Application",masterPersistence.retrieveMasterEntities(1,Short.valueOf("1")));
	}
	
	public void testRetrieveMasterDataEntity() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<MasterDataEntity> masterDataList = masterPersistence
				.retrieveMasterDataEntity("org.mifos.application.accounts.business.AccountStateEntity");
		assertEquals(18, masterDataList.size());
		for (MasterDataEntity masterDataEntity : masterDataList) {
			for (LookUpValueLocaleEntity lookUpValueLocaleEntity : masterDataEntity
					.getLookUpValue().getLookUpValueLocales()) {
				assertEquals(Short.valueOf("1"), lookUpValueLocaleEntity
						.getLocaleId());
			}
		}
	}
	
	
}
