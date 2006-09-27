package org.mifos.application.productdefinition.business.service;

import java.util.Iterator;
import java.util.List;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.persistence.LoansPrdPersistence;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class LoanPrdBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<ProductCategoryBO> getActiveLoanProductCategories()
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getApplicableProductCategories(
					ProductType.LOAN, PrdCategoryStatus.ACTIVE);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<MasterDataEntity> getLoanApplicableCustomerTypes(Short localeId)
			throws ServiceException {
		try {
			List<MasterDataEntity> applList = new MasterPersistence()
					.retrieveMasterEntities(PrdApplicableMasterEntity.class,
							localeId);
			if (applList != null && applList.size() > 0) {
				for (Iterator<MasterDataEntity> iter = applList.iterator(); iter
						.hasNext();) {
					MasterDataEntity masterData = iter.next();
					if (masterData.getId().equals(
							PrdApplicableMaster.CENTERS.getValue()))
						iter.remove();
				}
			}
			return applList;
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<FundBO> getSourcesOfFund() throws ServiceException {
		try {
			return new LoansPrdPersistence().getSourcesOfFund();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public LoanOfferingBO getLoanOffering(Short prdofferingId)
			throws ServiceException {
		try {
			return new LoansPrdPersistence().getLoanOffering(prdofferingId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PrdStatusEntity> getApplicablePrdStatus(Short localeId)
			throws ServiceException {
		try {
			return new PrdOfferingPersistence().getApplicablePrdStatus(
					ProductType.LOAN, localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public LoanOfferingBO getLoanOffering(Short loanOfferingId, Short localeId)
			throws ServiceException {
		try {
			return new LoansPrdPersistence().getLoanOffering(loanOfferingId,
					localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<LoanOfferingBO> getAllLoanOfferings(Short localeId)
			throws ServiceException {
		try {
			return new LoansPrdPersistence().getAllLoanOfferings(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
