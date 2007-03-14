package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class ProductCategoryPersistence extends Persistence {

	public Short getMaxPrdCategoryId() throws PersistenceException {
		return (Short) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_MAX, null);
	}

	public Integer getProductCategory(String productCategoryName)
			throws PersistenceException {
		Map<Object, Object> queryParameters = new HashMap<Object, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYNAME,
				productCategoryName);
		return ((Number) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_COUNT_CREATE,
				queryParameters)).intValue();

	}

	public Integer getProductCategory(String productCategoryName,
			Short productCategoryId) throws PersistenceException {
		Map<Object, Object> queryParameters = new HashMap<Object, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYNAME,
				productCategoryName);
		queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYID,
				productCategoryId);
		return ((Number) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_COUNT_UPDATE,
				queryParameters)).intValue();
	}

	@SuppressWarnings("cast")
	public List<ProductTypeEntity> getProductTypes()
			throws PersistenceException {
		return (List<ProductTypeEntity>) executeNamedQuery(
				NamedQueryConstants.GET_PRD_TYPES, null);
	}

	public ProductCategoryBO findByGlobalNum(String globalNum)
			throws PersistenceException {
		Map<Object, Object> queryParameters = new HashMap<Object, Object>();
		queryParameters.put("globalNum", globalNum);
		return (ProductCategoryBO) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_PRODUCTCATEGORY, queryParameters);

	}

	@SuppressWarnings("cast")
	public List<PrdCategoryStatusEntity> getProductCategoryStatusList()
			throws PersistenceException {
		return (List<PrdCategoryStatusEntity>) executeNamedQuery(
				NamedQueryConstants.GET_PRDCATEGORYSTATUS, null);

	}

	@SuppressWarnings("cast")
	public List<ProductCategoryBO> getAllCategories()
			throws PersistenceException {
		return (List<ProductCategoryBO>) executeNamedQuery(
				NamedQueryConstants.PRODUCTCATEGORIES_SEARCH, null);

	}

}
