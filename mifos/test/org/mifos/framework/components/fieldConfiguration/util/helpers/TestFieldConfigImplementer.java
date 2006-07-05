package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mifos.framework.MifosTestCase;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.service.FieldConfigurationPersistenceService;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;
import org.mifos.framework.util.helpers.FilePaths;

public class TestFieldConfigImplementer extends MifosTestCase{
	
	private static FieldConfigurationPersistenceService fieldConfigurationPersistenceService=new FieldConfigurationPersistenceService();
	
	private static FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
	
	public void testIsFieldHidden() throws HibernateProcessException {
		EntityMasterData.getInstance().init();
		List<EntityMaster> entityMasterList=fieldConfigurationPersistenceService.getEntityMasterList();
		for(EntityMaster entityMaster : entityMasterList){
			fieldConfigItf.getEntityFieldMap().put(entityMaster.getId(),fieldConfigurationPersistenceService.getListOfFields(entityMaster.getId()));
		}
		assertEquals(fieldConfigItf.isFieldHidden("Loan.PurposeOfLoan"),false);
		assertEquals(fieldConfigItf.isFieldHidden("Group.City"),true);
		fieldConfigItf.getEntityFieldMap().clear();
	}

	public void testIsFieldMandatory() throws HibernateProcessException {
		EntityMasterData.getInstance().init();
		List<EntityMaster> entityMasterList=fieldConfigurationPersistenceService.getEntityMasterList();
		for(EntityMaster entityMaster : entityMasterList){
			fieldConfigItf.getEntityFieldMap().put(entityMaster.getId(),fieldConfigurationPersistenceService.getListOfFields(entityMaster.getId()));
		}
		assertEquals(fieldConfigItf.isFieldManadatory("Loan.PurposeOfLoan"),true);
		assertEquals(fieldConfigItf.isFieldManadatory("Center.PostalCode"),true);
		fieldConfigItf.getEntityFieldMap().clear();
	}
	
	
	public void testInit() throws HibernateProcessException{
		EntityMasterData.getInstance().init();
		fieldConfigItf.init();
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryMap=fieldConfigItf.getEntityMandatoryFieldMap();
		assertEquals(entityMandatoryMap.size(),23);
		
		List<FieldConfigurationEntity> listOfMandatoryFields=entityMandatoryMap.get(Short.valueOf("22"));
		assertEquals(listOfMandatoryFields.size(),1);
		
		List<FieldConfigurationEntity> listOfFields=fieldConfigItf.getEntityFieldMap().get(Short.valueOf("22"));
		assertEquals(listOfFields.size(),5);
		
		for(FieldConfigurationEntity fieldConfigurationEntity : listOfMandatoryFields){
			assertEquals(fieldConfigurationEntity.getFieldName(),"PurposeOfLoan");
		}
		
		assertEquals(fieldConfigItf.isFieldHidden("Loan.PurposeOfLoan"),false);
		assertEquals(fieldConfigItf.isFieldManadatory("Loan.PurposeOfLoan"),true);

		fieldConfigItf.getEntityFieldMap().clear();
		fieldConfigItf.getEntityMandatoryFieldMap().clear();
	}
	
}
