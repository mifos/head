/**

 * ProductCategoryBusinessProcessor.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.productdefinition.business.handlers;

import java.util.List;

import org.mifos.application.master.util.valueobjects.PrdCategoryStatus;
import org.mifos.application.productdefinition.dao.ProductCategoryDAO;
import org.mifos.application.productdefinition.exceptions.DuplicateProductCategoryException;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class contains the business logic related to creation,updation of the 
 * product category.
 * 
 * @author mohammedn
 * 
 */
public class ProductCategoryBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * Default Constructor
	 */
	public ProductCategoryBusinessProcessor() {
	}
	
	private MifosLogger prdDefLogger=MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);
	
	/**
	 * It has to use MasterDataRetriever to retrieve product types based on the
	 * locale and set it in the context object.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {
		try {
			UserContext userContext = context.getUserContext();
			Short localeId = getUserLocaleId(userContext);
			prdDefLogger.info("Loading Product Types for Loacle with Id "+localeId);
			SearchResults productTypeSearchResults = getProductCategoryDAO().getProductTypeSearchResults(localeId);
			context.addAttribute(productTypeSearchResults);
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * Creates the product category definition.(Adds a row in the database). It
	 * also checks for duplicacy of category name in the HO only because
	 * categories can be defined only at the HO level.
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#create(org.mifos.framework.util.valueobjects.Context)
	 */
	public void create(Context context) throws SystemException,
			ApplicationException {
		try {
			ProductCategory productCategory = validateValueObject(context);
			String productCategoryName = productCategory.getProductCategoryName();
			boolean isDuplicatePrdCategoryExists=checkForDuplicateProductCategory(productCategoryName);
			prdDefLogger.info("The Product Category Name "+productCategoryName+ 
					" existing in the system is "+isDuplicatePrdCategoryExists);
			if (!isDuplicatePrdCategoryExists) {
				getProductCategoryDAO().saveProductCategory(productCategory);
				prdDefLogger.info("The Product Category "+productCategoryName+
						" has been assigned id "+productCategory.getProductCategoryID());
			} else {
				throw new DuplicateProductCategoryException(
						ProductDefinitionConstants.DUPLCATEGORYNAME);
			}
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * Add status value to the value object as active which should be default on
	 * creation. It has to add created by,created date to  the value object.
	 * 
	 * @param context
	 * @throws DuplicateProductCategoryException
	 */
	public void createInitial(Context context)
			throws ApplicationException, SystemException {
		try {
			ProductCategory productCategory = validateValueObject(context);
			PrdCategoryStatus status = new PrdCategoryStatus();
			status.setPrdCategoryStatusId(ProductDefinitionConstants.ACTIVE);
			productCategory.setPrdCategoryStatus(status);
			UserContext userContext = context.getUserContext();
			if (null != userContext) {
				productCategory.setOfficeId(userContext.getBranchId());
				productCategory.setCreatedBy(userContext.getId());
			}
			productCategory.setCreatedDate((new java.sql.Date(System.currentTimeMillis())));
	
			StringBuilder globalPrdOfferingNum = new StringBuilder();
			globalPrdOfferingNum.append(userContext.getBranchId());
			globalPrdOfferingNum.append("-");
			productCategory.setGlobalPrdOfferingNum(globalPrdOfferingNum.toString());
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method checks if the product category name already exists.
	 * 
	 * @param categoryName -
	 *            Name of the category which is being created afresh.
	 * @param path -path of the DAO 
	 * 
	 * @return - Returns false if the product category name already exists.
	 * 
	 */
	public boolean checkForDuplicateProductCategory(String productCategoryName) 
			throws SystemException, ApplicationException {
		return getProductCategoryDAO().ifProductCategoryExists(productCategoryName);
	}

	/**
	 * this is overridden because we need to retrieve the object based on the
	 * categoryid which might not be the primary key.
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void get(Context context) throws SystemException,ApplicationException {
		try {
			
			ProductCategory productCategory = validateValueObject(context);
			Short productCategoryID = productCategory.getProductCategoryID();
			prdDefLogger.info("Retriving data for the Product Category with Product Category Id "+productCategoryID);
			productCategory = getProductCategoryDAO().get(productCategoryID);
			context.setValueObject(productCategory);
			UserContext userContext = context.getUserContext();
			Short localeId = getUserLocaleId(userContext);
			SearchResults productTypeSearchResults = getProductCategoryDAO().getProductTypeSearchResults(localeId);
			context.addAttribute(productTypeSearchResults);
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * Retrieves master data required for manage page and sets it in the
	 * context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void manageInitial(Context context) throws SystemException,
			ApplicationException {
		try {
			UserContext userContext = context.getUserContext();
			Short localeId = getUserLocaleId(userContext);
			SearchResults productTypeSearchResults = getProductCategoryDAO().getProductTypeSearchResults(localeId);
			context.addAttribute(productTypeSearchResults);
			SearchResults prdCategoryStatusSearchResults = getProductCategoryDAO().getPrdCategoryStatusSearchResults(localeId);
			context.addAttribute(prdCategoryStatusSearchResults);
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method checks if the product category name already exists.
	 * 
	 * @param categoryName -
	 *            Name of the category which is being created afresh.
	 * @return - Returns false if the product category name already exists.
	 * 
	 */
	public boolean checkForDuplicateProductCategoryForUpdate(
			String productCategoryName, Short productCategoryID)
			throws SystemException, ApplicationException {
		return getProductCategoryDAO().ifProductCategoryForUpdateExists(
				productCategoryName,productCategoryID);
	}

	/**
	 * This method is used to update the product category
	 * 
	 * @throws DuplicateProductCategoryException -
	 *             Throws this exception whenever the name matches an existing
	 *             product category name.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		try {
			ProductCategory productCategory = validateValueObject(context);
			String productCategoryName = productCategory.getProductCategoryName();
			Short productCategoryID = productCategory.getProductCategoryID();
			boolean isDuplicatePrdCategoryExists=checkForDuplicateProductCategoryForUpdate(productCategoryName,
					productCategoryID);
			prdDefLogger.info("The Product Category Name "+productCategoryName+ 
					" existing in the system is "+isDuplicatePrdCategoryExists);
			if (!(isDuplicatePrdCategoryExists)) {
				getProductCategoryDAO().updateProductCategory(productCategory);
			} else {
				throw new DuplicateProductCategoryException(
						ProductDefinitionConstants.DUPLCATEGORYNAME);
			}
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * this method is used to set update by and updated date in the value object.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException 
	 * 
	 */
	public void updateInitial(Context context) throws SystemException,
			ApplicationException {
		try {
			ProductCategory productCategory = validateValueObject(context);
			UserContext userContext = context.getUserContext();
			if (null != userContext) {
				productCategory.setUpdatedBy(userContext.getId());
			}
			productCategory.setUpdatedDate((new java.sql.Date(System.currentTimeMillis())));
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * Performs search and lists down all the product categories in the HO. It
	 * calls search method on the DAO and sets the result in the context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getProductCategories(Context context) throws SystemException,
			ApplicationException {
		try {
			List<ProductCategory> producCategoryList = getProductCategoryDAO().search();
			context.addAttribute(getSearchResults(
					ProductDefinitionConstants.PRODUCTCATEGORYLIST,
					producCategoryList));
	
			UserContext userContext = context.getUserContext();
			Short localeId = getUserLocaleId(userContext);
			SearchResults productTypeSearchResults = getProductCategoryDAO().getProductTypeSearchResults(localeId);
			context.addAttribute(productTypeSearchResults);
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to obtain SearchResults Object by passing the
	 * resultName and value
	 * 
	 * @param resultName
	 * @param value
	 * @return
	 */
	private SearchResults getSearchResults(String resultName, Object value) {
		SearchResults searchResults = new SearchResults();
		searchResults.setResultName(resultName);
		searchResults.setValue(value);
		return searchResults;
	}

	/**
	 * This method is used to get the ValueObject from the Context and checks
	 * for null.
	 * 
	 * @param context
	 * @return MifosLoginValueObject
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private ProductCategory validateValueObject(Context context)
			throws ApplicationException, SystemException {
		ProductCategory valueObject = (ProductCategory) context.getValueObject();
		checkForNull(valueObject, ProductDefinitionConstants.PRDINVALID);
		return valueObject;
	}

	/**
	 * This method checks, if the given object is null. If Object is null, it
	 * throws an ApplicationException and Logs the message.
	 * 
	 * @param obj
	 *            to be checked for null
	 * @param exception
	 *            to be thrown
	 * @param message
	 *            to be logged
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void checkForNull(Object obj, String exception)
			throws ApplicationException, SystemException {
		if (null == obj) {
			throw new ProductDefinitionException(ProductDefinitionConstants.PRDINVALID);
		}
	}
	/**
	 * This method returns the DAO associated with ProductCategory.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private ProductCategoryDAO getProductCategoryDAO() throws SystemException,
			ApplicationException {
		return (ProductCategoryDAO)getDAO(ProductDefinitionConstants.GETPATHPRODUCTCATEGORY);
	}
	
	/**
	 * this method is used to get the user locale id from the UserContext
	 * @param userContext
	 * @return--userLocaleId
	 */
	private Short getUserLocaleId(UserContext userContext) {
		Short localeId = null;
		if (null != userContext) {
			localeId = userContext.getLocaleId();
		}
		if (null == localeId) {
			localeId = userContext.getMfiLocaleId();
		}
		return localeId;
	}

}
