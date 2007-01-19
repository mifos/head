package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.List;
import java.util.Map;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.service.FieldConfigurationPersistenceService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;

public class TestFieldConfigImplementer extends MifosTestCase{
	
	private static FieldConfigurationPersistenceService fieldConfigurationPersistenceService=new FieldConfigurationPersistenceService();
	
	private static FieldConfig fieldConfig =
		FieldConfig.getInstance();
	
	public void testIsFieldHidden() throws HibernateProcessException, PersistenceException {
		EntityMasterData.getInstance().init();
		List<EntityMaster> entityMasterList=fieldConfigurationPersistenceService.getEntityMasterList();
		for(EntityMaster entityMaster : entityMasterList){
			fieldConfig.getEntityFieldMap().put(entityMaster.getId(),fieldConfigurationPersistenceService.getListOfFields(entityMaster.getId()));
		}
		assertEquals(fieldConfig.isFieldHidden("Loan.PurposeOfLoan"),false);
		assertEquals(fieldConfig.isFieldHidden("Group.City"),true);
		fieldConfig.getEntityFieldMap().clear();
	}

	public void testIsFieldMandatory() throws HibernateProcessException, PersistenceException {
		EntityMasterData.getInstance().init();
		List<EntityMaster> entityMasterList=fieldConfigurationPersistenceService.getEntityMasterList();
		for(EntityMaster entityMaster : entityMasterList){
			fieldConfig.getEntityFieldMap().put(entityMaster.getId(),fieldConfigurationPersistenceService.getListOfFields(entityMaster.getId()));
		}
		assertEquals(fieldConfig.isFieldManadatory("Loan.PurposeOfLoan"),true);
		assertEquals(fieldConfig.isFieldManadatory("Center.PostalCode"),true);
		fieldConfig.getEntityFieldMap().clear();
	}
	
	
	public void testInit() throws HibernateProcessException, ApplicationException{
		EntityMasterData.getInstance().init();
		fieldConfig.init();
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryMap=fieldConfig.getEntityMandatoryFieldMap();
		assertEquals(22, entityMandatoryMap.size());
		
		List<FieldConfigurationEntity> listOfMandatoryFields=entityMandatoryMap.get(Short.valueOf("22"));
		assertEquals(1, listOfMandatoryFields.size());
		
		List<FieldConfigurationEntity> listOfFields=fieldConfig.getEntityFieldMap().get(Short.valueOf("22"));
		assertEquals(5, listOfFields.size());
		
		for(FieldConfigurationEntity fieldConfigurationEntity : listOfMandatoryFields){
			assertEquals(fieldConfigurationEntity.getFieldName(),"PurposeOfLoan");
		}
		
		assertEquals(fieldConfig.isFieldHidden("Loan.PurposeOfLoan"),false);
		assertEquals(fieldConfig.isFieldManadatory("Loan.PurposeOfLoan"),true);

		fieldConfig.getEntityFieldMap().clear();
		fieldConfig.getEntityMandatoryFieldMap().clear();
	}
	
}
