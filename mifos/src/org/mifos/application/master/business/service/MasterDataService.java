package org.mifos.application.master.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.dao.MasterDAO;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class MasterDataService extends BusinessService {
	private PersonnelPersistence personnelPersistence = new PersonnelPersistence();

	private OfficePersistence officePersistence = new OfficePersistence();

	private CustomerPersistence customerPersistence = new CustomerPersistence();

	private MasterPersistenceService masterPersistenceService = new MasterPersistenceService();

	@Override
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
			Short officeId, Short userId, Short userLevelId)
			throws ServiceException {
		try {
			return personnelPersistence.getActiveLoanOfficersInBranch(levelId,
					officeId, userId, userLevelId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeView> getActiveBranches(Short branchId)
			throws ServiceException {
		try {
			return officePersistence.getActiveOffices(branchId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	public List<CustomerView> getListOfActiveParentsUnderLoanOfficer(
			Short personnelId, Short customerLevel, Short officeId)
			throws ServiceException {
		try {
			return customerPersistence.getActiveParentList(personnelId,
					customerLevel, officeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}

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
			String searchId, Short personnelId) throws ServiceException {
		try {
			return customerPersistence.getLoanProducts(meetingDate, searchId,
					personnelId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PrdOfferingBO> getSavingsProductsAsOfMeetingDate(
			Date meetingDate, String searchId, Short personnelId)
			throws ServiceException {
		try {
			return customerPersistence.getSavingsProducts(meetingDate,
					searchId, personnelId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PaymentTypeEntity> retrievePaymentTypes(Short localeId)
			throws ServiceException {
		try {
			return masterPersistenceService.retrievePaymentTypes(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId,
			Short transactionTypeId) throws ServiceException {
		try {
			return masterPersistenceService.getSupportedPaymentModes(localeId,
					transactionTypeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<MasterDataEntity> retrieveMasterEntities(Class entityName,
			Short localeId) throws ServiceException {
		try {
			return masterPersistenceService.retrieveMasterEntities(entityName,
					localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			EntityType entityType) throws ServiceException {
		try {
			return new MasterPersistence()
					.retrieveCustomFieldsDefinition(entityType);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public String retrieveMasterEntities(Integer entityId, Short localeId)
			throws ServiceException {
		try {
			return new MasterPersistence().retrieveMasterEntities(entityId,
					localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
