/**

 * SavingsProductBusinessProcessor.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.application.customer.client.util.valueobjects.ClientChangeLog;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.productdefinition.dao.SavingsProductDAO;
import org.mifos.application.productdefinition.exceptions.DuplicateProductInstanceException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.MasterObject;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.application.productdefinition.util.valueobjects.PrdStatus;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.application.productdefinition.util.valueobjects.SavingsOffering;
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
 * savings product.
 */
public class SavingsProductBusinessProcessor extends MifosBusinessProcessor {

	public SavingsProductBusinessProcessor() {
	}
	
	private MifosLogger prdSavingsLogger=MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);
	
	//----------------------------------------------Load----------------------------------------
	/**
	 * This would load the master data for prdCategory of type. 
	 * This should not show any product category which is in status inactive.
	 * This master data would be stored in context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {
		prdSavingsLogger.debug("In Load Initial of Savings Product");
		try {
			List<ProductCategory> savingsProductCategoryList = getSavingsProductDAO().getSavingsProductCategories();
			context.addAttribute(getSearchResults(ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST,
					savingsProductCategoryList));
			prdSavingsLogger.debug("Loading Master Data in Savings Product");
			getMasterData(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
	
//----------------------------------------------Preview------------------------------------------
	/**
	 * This method is used to set the objects available in master data into the
	 * value object. This sets all the objects whose Id's are available in Context,
	 * but the entire object is required for saving.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void previewInitial(Context context) throws SystemException,
			ApplicationException {
		prdSavingsLogger.debug("In preview Initial of Savings Product");
		try {
			SavingsOffering savingsOffering = validateValueObject(context);
			
			//setting product category object into value object
			Short productCategoryId = savingsOffering.getPrdCategory()
					.getProductCategoryID();
			SearchResults searchResults = context
					.getSearchResultBasedOnName(ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST);
			checkForNull(searchResults, ProductDefinitionConstants.PRDINVALID);
			List<ProductCategory> list = (List<ProductCategory>) searchResults.getValue();
			for (ProductCategory productCategory : list) {
				if (productCategory.getProductCategoryID().equals(
						productCategoryId)) {
					savingsOffering.setPrdCategory(productCategory);
					savingsOffering.setPrdType(productCategory.getProductType());
				}
			}
			
			//setting product status object into value object
			Short prdStatusId = savingsOffering.getPrdStatus().getOfferingStatusId();
			SearchResults searchResultsPrdStatus = context.getSearchResultBasedOnName(ProductDefinitionConstants.SAVINGSPRDSTATUSLIST);
			checkForNull(searchResultsPrdStatus, ProductDefinitionConstants.PRDINVALID);
			List<MasterObject> prdStatusList = (List<MasterObject>) searchResultsPrdStatus.getValue();
			for (MasterObject masterObject : prdStatusList) {
				if (masterObject.getId().equals(prdStatusId)) {
					PrdStatus status = new PrdStatus();
					status.setOfferingStatusId(masterObject.getId());
					status.setVersionNo(masterObject.getVersionNo());
					savingsOffering.setPrdStatus(status);
				}
			}
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

//----------------------------------------------Create------------------------------------------
	/**
	 * This method is used to set the Office, created By, Created date into the Value Object.
	 * It also creates the  Global Offering Number for the product. This method also sets
	 * not mandatory fields to null, if they are not entered. This method creates a set to save 
	 * the frequency of Interest calculation and Time period for Interest Posting. 
	 * 
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context) throws SystemException,
			ApplicationException {
		try {
			prdSavingsLogger.debug("In Create Initial of Savings Product Business Processor");
			SavingsOffering savingsOffering = validateValueObject(context);
			UserContext userContext = context.getUserContext();
			
			//Setting Office,createdBy and Created Date in Value Object.
			if (null != userContext) {
				savingsOffering.setOffice(new Office(userContext.getBranchId()));
				savingsOffering.setCreatedBy(Integer.valueOf(userContext.getId()));
			} else {
				throw new ApplicationException(ProductDefinitionConstants.PRDINVALID);
			}
			savingsOffering.setCreatedDate((new java.sql.Date(System.currentTimeMillis())));
			
			//Generating Global Offering Number
			StringBuilder globalPrdOfferingNum = new StringBuilder();
			globalPrdOfferingNum.append(userContext.getBranchId());
			globalPrdOfferingNum.append("-");
			savingsOffering.setGlobalPrdOfferingNum(globalPrdOfferingNum.toString());
			
			//Setting recommended amount unit to null, if it is not selected.
			if(savingsOffering.getRecommendedAmntUnit() !=null) {
				if(savingsOffering.getRecommendedAmntUnit().getRecommendedAmntUnitId()==null) {
					savingsOffering.setRecommendedAmntUnit(null);
				}
			}
			
			//Creating a Set for saving frequency of Interest calculation and Time period for Interest Posting.
			PrdOfferingMeeting timePerForInstcalc = savingsOffering.getTimePerForInstcalc();
			PrdOfferingMeeting freqOfPostIntcalc = savingsOffering.getFreqOfPostIntcalc();
			Set<PrdOfferingMeeting> prdofferingMeetingSet=new HashSet<PrdOfferingMeeting>();
			prdofferingMeetingSet.add(timePerForInstcalc);
			prdofferingMeetingSet.add(freqOfPostIntcalc);
			savingsOffering.setPrdOfferingMeetingSet(prdofferingMeetingSet);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * It creates the Savings product Instance.Before creating it needs to do
	 * certain business logic validations like check for duplicacy of 
	 * product instance name and short name. It throws a DuplicateProductInstanceException
	 * if product instance name or short name exists. 
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void create(Context context) throws SystemException,
			ApplicationException {
		try {
			SavingsOffering savingsOffering = validateValueObject(context);
			String savingsProductName = savingsOffering.getPrdOfferingName();
			String savingsProductShortName = savingsOffering.getPrdOfferingShortName();
			prdSavingsLogger.info("Checking for duplicacy of Product Instance name" + savingsProductName);
			//Checking for duplicacy of product instance name.
			if(checkForDuplicateNameForCreate(savingsProductName)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTNAME);
			}
			prdSavingsLogger.info("Checking for duplicacy of Product Short name" + savingsProductShortName);
			//Checking for duplicacy of short name.
			if(checkForDuplicateShortNameForCreate(savingsProductShortName)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
			}
			prdSavingsLogger.info("Creating the Savings Product" + savingsProductShortName);
			//creating the Product
			getSavingsProductDAO().saveSavingsProduct(savingsOffering);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	/**
	 * It checks for duplicacy of savings product instance name.
	 * 
	 * @param SavingsProductInstanceName
	 * @throws DuplicateProductInstanceException
	 */
	private boolean checkForDuplicateNameForCreate(String savingsProductName)
			throws ApplicationException, SystemException {
		return getSavingsProductDAO().ifCreateProductNameExists(savingsProductName);
	}
	
	/**
	 * It checks for duplicacy of loan product short name.
	 * 
	 * @param savingsProductShortName
	 * @throws DuplicateProductInstanceException
	 */
	private boolean checkForDuplicateShortNameForCreate(String savingsProductShortName)
			throws ApplicationException, SystemException {
		return getSavingsProductDAO().ifCreateProductShortNameExists(savingsProductShortName);
}
//----------------------------------------------Get------------------------------------------
	/**
	 * This method retrieves the product based on the system id by calling get on the dao.
	 * This method also retrives the master data.
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void get(Context context) throws SystemException,
			ApplicationException {
		prdSavingsLogger.debug("In get of Savings Product");
		try {
			SavingsOffering savingsOffering = validateValueObject(context);
			Short prdOfferingId = savingsOffering.getPrdOfferingId();
			savingsOffering = getSavingsProductDAO().get(prdOfferingId);
			context.setValueObject(savingsOffering);
			
			getMasterData(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

//----------------------------------------------Manage------------------------------------------
	/**
	 * This would load the master data for prdCategory of type savings. It also
	 * needs to get master list of applicable for and fee types based on product
	 * type. This should not show any product category which is in status
	 * inactive . This would also get a master list of states for the product instance. This master data
	 * would be stored in context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void manageInitial(Context context) throws SystemException,
			ApplicationException {
		
		prdSavingsLogger.debug("In Manage Initial of Savings Product");	
		try {
			SavingsOffering savingsOffering= validateValueObject(context);
			ProductCategory productCategory=savingsOffering.getPrdCategory();
			List<ProductCategory> savingsProductCategoryList = 
				getSavingsProductDAO().getAllSavingsProductCategories(productCategory);
			context.addAttribute(getSearchResults(
					ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST,
					savingsProductCategoryList));
	
			getMasterData(context);
			RecommendedAmntUnit recommendedAmntUnit = savingsOffering.getRecommendedAmntUnit();
			if(null==recommendedAmntUnit) {
				savingsOffering.setRecommendedAmntUnit(new RecommendedAmntUnit());
			}
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

//----------------------------------------------Update------------------------------------------
	/**
	 * This method is used to set the Updated By, updatedd date into the Value Object.
	 * This method also sets not mandatory fields to null, if they are not entered. 
	 * This method creates a set to save the frequency of Interest calculation and Time period 
	 * for Interest Posting.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context) throws SystemException,
			ApplicationException {
		prdSavingsLogger.debug("In Update Initial of Savings Product");
		try {
			SavingsOffering savingsOffering = validateValueObject(context);
			UserContext userContext = context.getUserContext();
			if (null != userContext) {
				savingsOffering.setUpdatedBy(Integer.valueOf(userContext.getId()));
			} else {
				throw new ApplicationException(ProductDefinitionConstants.PRDINVALID);
			}
			savingsOffering.setUpdatedDate((new java.sql.Date(System
					.currentTimeMillis())));
			if(savingsOffering.getRecommendedAmntUnit() !=null) {
				if(savingsOffering.getRecommendedAmntUnit().getRecommendedAmntUnitId()==null) {
					savingsOffering.setRecommendedAmntUnit(null);
				}
			}
			
			PrdOfferingMeeting timePerForInstcalc = savingsOffering.getTimePerForInstcalc();
			PrdOfferingMeeting freqOfPostIntcalc = savingsOffering.getFreqOfPostIntcalc();
			Set<PrdOfferingMeeting> prdofferingMeetingSet=new HashSet<PrdOfferingMeeting>();
			prdofferingMeetingSet.add(timePerForInstcalc);
			prdofferingMeetingSet.add(freqOfPostIntcalc);
			savingsOffering.setPrdOfferingMeetingSet(prdofferingMeetingSet);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * It updates the ValueObject instance passed in the Context object in the
	 * database. Before updating it needs to do certain business logic validations like 
	 * check for duplicacy of product instance name and short name, for other than this product.
	 *  It throws a DuplicateProductInstanceException if product instance name or short name exists.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		prdSavingsLogger.debug("In Update of Savings Product");
		try {
			SavingsOffering savingsOffering = validateValueObject(context);
			String savingsProductName = savingsOffering.getPrdOfferingName();
			String savingsProductShortName = savingsOffering.getPrdOfferingShortName();
			Short prdOfferingId = savingsOffering.getPrdOfferingId();
			if(checkForDuplicateSavingsProductNameForUpdate(savingsProductName,
					prdOfferingId)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTNAME);
			}
			if(checkForDuplicateSavingsProductShortNameForUpdate(savingsProductShortName,
					prdOfferingId)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
			}
			getSavingsProductDAO().updateSavingsProduct(savingsOffering,context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * This method checks if the product savingsProductName already exists for other than the current product.
	 * 
	 * @param savingsProductName -
	 *            Name of the savingsProductName which is being updated.
	 * @return - Returns false if the savingsProductName already exists.
	 * 
	 */
	public boolean checkForDuplicateSavingsProductNameForUpdate(
			String savingsProductName, Short prdOfferingId)
			throws SystemException, ApplicationException {
		return getSavingsProductDAO().ifUpdateProductNameExists(savingsProductName,prdOfferingId);
	}
	
	/**
	 * This method checks if the product savingsProductShortName already exists for other than the current product.
	 * 
	 * @param savingsProductShortName -
	 *            Name of the savingsProductShortName which is being updated.
	 * @return - Returns false if the savingsProductShortName already exists.
	 * 
	 */
	public boolean checkForDuplicateSavingsProductShortNameForUpdate(
			String savingsProductShortName, Short prdOfferingId)
			throws SystemException, ApplicationException {
		return getSavingsProductDAO().ifUpdateProductShortNameExists(savingsProductShortName,prdOfferingId);
	}


//----------------------------------------------Search------------------------------------------
	/**
	 * Performs search and lists down all the product categories in the HO. It
	 * calls search method on the DAO and sets the result in the context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getSavingsProducts(Context context) throws SystemException,
			ApplicationException {
		try {
			List<SavingsOffering> savingsOfferingList = getSavingsProductDAO().search();
			context.addAttribute(getSearchResults(ProductDefinitionConstants.SAVINGSPRODUCTLIST,
					savingsOfferingList));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * This method is used to get the change log details of the product.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getChangeLogDetails(Context context) throws SystemException,ApplicationException {
		SavingsOffering savingsOffering = validateValueObject(context);
		if(null != savingsOffering) {
			Integer prdOfferingId=Integer.valueOf(savingsOffering.getPrdOfferingId());
			
			ClosedAccSearchDAO closedAccSearchDAO=(ClosedAccSearchDAO)getDAO(
					ClosedAccSearchConstants.GETPATHCLOSEDACCSEARCH);
			List<ClientChangeLog> clientChangeLogList=closedAccSearchDAO.getClientChangeLog(
					prdOfferingId,ProductDefinitionConstants.SAVINGSENTITYTYPEID);
			context.addAttribute(new SearchResults(ProductDefinitionConstants.SAVINGSCHANGELOGLIST,
					clientChangeLogList));
		}
	}

//----------------------------------------------Helpers------------------------------------------
	
	/**
	 * This helper method is used to get the Master data and then adds the master data to the 
	 * context. 
	 *  
	 */
	private void getMasterData(Context context) throws SystemException,
			ApplicationException {
		UserContext userContext=context.getUserContext();
		Short localeId =getUserLocaleId(userContext);

		SearchResults searchResultsApplFor = getSavingsProductDAO().getPrdApplFor(localeId);
		context.addAttribute(searchResultsApplFor);
		
		SearchResults searchResultsSavingsType = getSavingsProductDAO().getSavingsTypeSearchResults(localeId);
		context.addAttribute(searchResultsSavingsType);

		SearchResults searchResultsRecAmnt = getSavingsProductDAO().getRecommendedAmntUnitSearchResults(localeId);
		context.addAttribute(searchResultsRecAmnt);

		SearchResults searchResultsIntCalType = getSavingsProductDAO().getInterestCalcTypeSearchResults(localeId);
		context.addAttribute(searchResultsIntCalType);
		
		SearchResults searchResultsPrdStatus = getSavingsProductDAO().getPrdStatus(localeId);
		context.addAttribute(searchResultsPrdStatus);
		
		context.addAttribute(getSavingsProductDAO().getRecurrenceTypes());
		
		context.addAttribute(this.getSearchResults("depositGLCodes",getSavingsProductDAO().getGLCodes(ProductDefinitionConstants.DEPOSITGLCODE)));
		context.addAttribute(this.getSearchResults("interestGLCodes",getSavingsProductDAO().getGLCodes(ProductDefinitionConstants.INTERESTGLCODE)));
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
	private SavingsOffering validateValueObject(Context context)
			throws ApplicationException, SystemException {
		SavingsOffering valueObject = (SavingsOffering) context
				.getValueObject();
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
			throw new ApplicationException(exception);
		}
	}
	
	/**
	 * this method is used to get the user locale id from the UserContext
	 * 
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
	
	
	
	/**
	 * This method returns the DAO associated with SavingsProduct.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private SavingsProductDAO getSavingsProductDAO() throws SystemException,ApplicationException {
		return (SavingsProductDAO)getDAO(ProductDefinitionConstants.GETPATHSAVINGSPRODUCT);
	}

}
