package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
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
		return ((Number) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT,
				queryParameters)).intValue();
	}

	public Integer getProductOfferingShortNameCount(
			String productOfferingShortName) throws PersistenceException {
		prdLogger.debug("getting the product offering short name count for :"
				+ productOfferingShortName);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRDOFFERINGSHORTNAME,
				productOfferingShortName);
		return ((Number) execUniqueResultNamedQuery(
				NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT,
				queryParameters)).intValue();
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
	

	@SuppressWarnings("cast")
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

	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getAllPrdOffringByType(String prdType)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE,
				prdType);
		if(prdType.equals(ProductType.LOAN.getValue().toString()))
			queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
			else if(prdType.equals(ProductType.SAVINGS.getValue().toString()))
				queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
		
		return  executeNamedQuery(
				NamedQueryConstants.PRD_BYTYPE, queryParameters);
			
	}
	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getAllowedPrdOfferingsByType(String prdId,String prdType)
			throws PersistenceException {
				
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE,
				prdType);
		queryParameters.put(ProductDefinitionConstants.PRODUCTID,
				prdId);	
		if(prdType.equals(ProductType.LOAN.getValue().toString()))
			queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
			else if(prdType.equals(ProductType.SAVINGS.getValue().toString()))
				queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
		
		return  executeNamedQuery(
				NamedQueryConstants.ALLOWED_PRD_OFFERING_BYTYPE, queryParameters);
			
	}
	
	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getAllowedPrdOfferingsForMixProduct(String prdId,String prdType)
			throws PersistenceException {
				
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE,
				prdType);
		queryParameters.put(ProductDefinitionConstants.PRODUCTID,
				prdId);	
		if(prdType.equals(ProductType.LOAN.getValue().toString()))
		queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
		else if(prdType.equals(ProductType.SAVINGS.getValue().toString()))
			queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
		return  executeNamedQuery(
				NamedQueryConstants.ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT, queryParameters);
			
	}
	
	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getNotAllowedPrdOfferingsByType(String prdId)
			throws PersistenceException {
				
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTID,
				prdId);	
		return  executeNamedQuery(
				NamedQueryConstants.NOT_ALLOWED_PRD_OFFERING_BYTYPE, queryParameters);
			
	}	
	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getNotAllowedPrdOfferingsForMixProduct(String prdId,String prdType)
			throws PersistenceException {
				
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTID,
				prdId);	
		return  executeNamedQuery(
				NamedQueryConstants.NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT, queryParameters);
			
	}	
	
	
	public PrdOfferingBO getPrdOffering(Short prdofferingId)
			throws PersistenceException {
		return (PrdOfferingBO) getPersistentObject(PrdOfferingBO.class,
				prdofferingId);
	}
	public PrdOfferingBO getPrdOffringByID(Short prdId)
			throws PersistenceException {
		prdLogger.debug("getting the product offering by id :"
				+ prdId);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(ProductDefinitionConstants.PRODUCTID,
				prdId);
		 	PrdOfferingBO prdOffring = (PrdOfferingBO) execUniqueResultNamedQuery(
				NamedQueryConstants.PRD_BYID, queryParameters);
		 	return prdOffring;
	}
	
	
	@SuppressWarnings("cast")
	public ProductTypeEntity getProductTypes(Short prdtype)
			throws PersistenceException {
		return (ProductTypeEntity) getPersistentObject(ProductTypeEntity.class, prdtype);
	}
	
	@SuppressWarnings("cast")
	public List<PrdOfferingBO> getPrdOfferingMix() throws PersistenceException {
		return (List<PrdOfferingBO>) executeNamedQuery(
				NamedQueryConstants.LOAD_PRODUCTS_OFFERING_MIX, null);
	}

	
}
