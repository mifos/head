package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class PrdOfferingPersistence extends Persistence {
	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	public Short getMaxPrdOffering() throws PersistenceException {
		prdLogger.debug("getting the max prd offering id");
		return (Short) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTOFFERING_MAX, null);
	}

	public PrdStatusEntity getPrdStatus(PrdStatus prdStatus)
			throws PersistenceException {
		prdLogger.debug("getting the product status for :" + prdStatus);
		return (PrdStatusEntity) getPersistentObject(PrdStatusEntity.class,
				prdStatus.getValue());
	}

	public Integer getProductOfferingNameCount(String productOfferingName)
			throws PersistenceException {
		prdLogger.debug("getting the product offering name count for :"
				+ productOfferingName);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRDOFFERINGNAME,
				productOfferingName);
		return (Integer) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT,
				queryParameters);
	}

	public Integer getProductOfferingShortNameCount(
			String productOfferingShortName) throws PersistenceException {
		prdLogger.debug("getting the product offering short name count for :"
				+ productOfferingShortName);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRDOFFERINGSHORTNAME,
				productOfferingShortName);
		return (Integer) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT,
				queryParameters);
	}

	public List<ProductCategoryBO> getApplicableProductCategories(
			ProductType productType, PrdCategoryStatus prdCategoryStatus)
			throws PersistenceException {
		prdLogger.debug("getting the applicable product categories");
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTTYPEID,
				productType.getValue());
		queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID,
				prdCategoryStatus.getValue());
		List<ProductCategoryBO> queryResult = executeNamedQuery(
				NamedQueryConstants.PRDAPPLICABLE_CATEGORIES, queryParameters);

		if (null != queryResult && queryResult.size() > 0) {
			for (ProductCategoryBO productCategory : queryResult) {
				productCategory.getProductType();
			}
		}
		prdLogger.debug("getting the applicable product categories Done and : "
				+ queryResult);
		return queryResult;
	}

	public List<PrdStatusEntity> getApplicablePrdStatus(
			ProductType productType, Short localeId)
			throws PersistenceException {
		prdLogger.debug("getting the applicable product Status");
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTTYPEID,
				productType.getValue());
		List<PrdStatusEntity> prdStatusList = (List<PrdStatusEntity>) executeNamedQuery(
				NamedQueryConstants.PRODUCT_STATUS, queryParameters);
		for (PrdStatusEntity prdStatus : prdStatusList) {
			initialize(prdStatus);
			initialize(prdStatus.getPrdState());
			prdStatus.getPrdState().setLocaleId(localeId);
		}
		prdLogger.debug("getting the applicable product Status Done and : "
				+ prdStatusList);
		return prdStatusList;
	}
}
