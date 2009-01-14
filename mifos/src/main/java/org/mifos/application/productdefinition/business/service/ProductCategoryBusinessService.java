package org.mifos.application.productdefinition.business.service;

import java.util.List;

import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class ProductCategoryBusinessService implements BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<ProductTypeEntity> getProductTypes() throws ServiceException {
		try {
			return new ProductCategoryPersistence().getProductTypes();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public ProductCategoryBO findByGlobalNum(String globalNum)
			throws ServiceException {
		try {
			return new ProductCategoryPersistence().findByGlobalNum(globalNum);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<PrdCategoryStatusEntity> getProductCategoryStatusList()
			throws ServiceException {
		try {
			return new ProductCategoryPersistence()
					.getProductCategoryStatusList();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<ProductCategoryBO> getAllCategories() throws ServiceException {
		try {
			return new ProductCategoryPersistence().getAllCategories();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
