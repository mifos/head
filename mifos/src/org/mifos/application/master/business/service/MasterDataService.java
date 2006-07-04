package org.mifos.application.master.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.dao.MasterDAO;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class MasterDataService extends BusinessService {
	private PersonnelPersistenceService personnelPersistenceService = new PersonnelPersistenceService();

	private OfficePersistenceService officePersistenceService = new OfficePersistenceService();

	private CustomerPersistenceService customerPersistenceService = new CustomerPersistenceService();

	private MasterPersistenceService masterPersistenceService = new MasterPersistenceService();

	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public EntityMaster retrieveMasterData(String entityName, Short localeId)
			throws SystemException, ApplicationException {
		return new MasterDAO().getLookUpEntity(entityName, localeId);
	}

	public EntityMaster retrieveMasterData(String entityName, Short localeId,
			String classPath, String column) throws SystemException,
			ApplicationException {
		return new MasterDAO().getLookUpEntity(entityName, localeId, classPath,
				column);
	}

	public List<PersonnelView> getListOfActiveLoanOfficers(Short levelId,
			Short officeId, Short userId, Short userLevelId) {
		return personnelPersistenceService.getActiveLoanOfficersInBranch(
				levelId, officeId, userId, userLevelId);
	}

	public List<OfficeView> getActiveBranches(Short branchId) {
		return officePersistenceService.getActiveBranches(branchId);

	}

	public List<CustomerView> getListOfActiveParentsUnderLoanOfficer(
			Short personnelId, Short customerLevel, Short officeId) {
		return customerPersistenceService
				.getListOfActiveParentsUnderLoanOfficer(personnelId,
						customerLevel, officeId);

	}

	public EntityMaster getMasterData(String entityName, Short localeId)
			throws ApplicationException, SystemException {
		return masterPersistenceService
				.retrieveMasterData(entityName, localeId);

	}

	public EntityMaster getMasterData(String entityName, Short localeId,
			String classPath, String column) throws ApplicationException,
			SystemException {
		return masterPersistenceService.retrieveMasterData(entityName,
				localeId, classPath, column);

	}

	public List<PrdOfferingBO> getLoanProductsAsOfMeetingDate(Date meetingDate,
			String searchId, Short personnelId) throws SystemException,
			ApplicationException {
		return customerPersistenceService.getLoanProductsAsOfMeetingDate(
				meetingDate, searchId, personnelId);
	}

	public List<PrdOfferingBO> getSavingsProductsAsOfMeetingDate(
			Date meetingDate, String searchId, Short personnelId) {
		return customerPersistenceService.getSavingsProductsAsOfMeetingDate(
				meetingDate, searchId, personnelId);
	}
	
	public List<PaymentTypeEntity> retrievePaymentTypes(Short localeId)throws SystemException{
		return masterPersistenceService.retrievePaymentTypes(localeId);
	}
	
	public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId, Short transactionTypeId)throws SystemException{
		return masterPersistenceService.getSupportedPaymentModes(localeId, transactionTypeId);
	}

}
