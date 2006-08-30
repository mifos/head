/**

 * LoanProductBusinessProcessor.java    version: 1.0

 

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.application.customer.client.util.valueobjects.ClientChangeLog;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.penalty.util.valueobjects.Penalty;
import org.mifos.application.productdefinition.dao.LoanProductDAO;
import org.mifos.application.productdefinition.exceptions.DuplicateProductInstanceException;
import org.mifos.application.productdefinition.exceptions.RepmntFeeFreqMisMatchException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.GracePeriodType;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.application.productdefinition.util.valueobjects.LoanOfferingFund;
import org.mifos.application.productdefinition.util.valueobjects.MasterObject;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingFees;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.application.productdefinition.util.valueobjects.PrdStatus;
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
 * laon product.
 */
public class LoanProductBusinessProcessor extends MifosBusinessProcessor {

	public LoanProductBusinessProcessor() {
	}
	
	private MifosLogger prdLoanLogger=MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	// ---------------------------------Update--------------------------------------------
	/**
	 * It updates the ValueObject instance passed in the Context object in the
	 * database. Before updating it checks for the duplicacy of the product instance name
	 * and short name.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		prdLoanLogger.debug("In Update method of Loan Product Business Procesor");
		try {
			LoanOffering loanOffering = validateValueObject(context);
			String loanProductName = loanOffering.getPrdOfferingName();
			String loanProductShortName = loanOffering
					.getPrdOfferingShortName();
			Short prdOfferingId = loanOffering.getPrdOfferingId();
			prdLoanLogger.info("Checking for Duplicacy of product instance name" + loanProductName);
			if (checkForDuplicateLoanNameForUpdate(loanProductName,prdOfferingId)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTNAME);
			}
			prdLoanLogger.info("Checking for Duplicacy of product short name" + loanProductShortName);
			if (checkForDuplicateShortLoanNameForUpdate(loanProductShortName,prdOfferingId)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
			}
			prdLoanLogger.info("Updating product" + loanProductName);
			getLoanProductDAO().updateLoanProduct(loanOffering,context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * This method sets the Updated By, Updated Date into the Value Object.
	 * Before Updating it also sets all the not mandatory fields whic are not entered to null.
	 * 
	 * @param context
	 * @throws RepmntFeeFreqMisMatchException
	 * @throws DuplicateProductInstanceException
	 */
	public void updateInitial(Context context) throws ApplicationException,
			SystemException {
		prdLoanLogger.debug("In Update Initial of Loan Product");
		try {
			LoanOffering loanOffering = validateValueObject(context);
			UserContext userContext = context.getUserContext();
			if (null != userContext) {
				loanOffering.setUpdatedBy(Integer.valueOf(userContext.getId()));
			} else {
				throw new ApplicationException(
						ProductDefinitionConstants.PRDINVALID);
			}
	
			if (null != loanOffering.getGracePeriodType()) {
				if (loanOffering.getGracePeriodType().getGracePeriodTypeId() == null) {
					GracePeriodType gracePeriodType=new GracePeriodType();
					gracePeriodType.setGracePeriodTypeId(ProductDefinitionConstants.DEFAULTLOANGRACEPERIODTYPE);
					loanOffering.setGracePeriodType(gracePeriodType);
				}
			}
	
			if (null != loanOffering.getInterestCalcRule()) {
				if (loanOffering.getInterestCalcRule().getInterestCalcRuleId() == null) {
					loanOffering.setInterestCalcRule(null);
				}
			}
			if (null != loanOffering.getPenalty()) {
				if (loanOffering.getPenalty().getPenaltyID() == null) {
					loanOffering.setPenalty(null);
				}
			}
			loanOffering.setUpdatedDate((new java.sql.Date(System
					.currentTimeMillis())));
	
			Set<PrdOfferingFees> prdOfferingFeesSet = loanOffering
					.getPrdOfferingFeesSet();
			if(null != prdOfferingFeesSet) {
				for (PrdOfferingFees prdOfferingFees : prdOfferingFeesSet) {
					prdOfferingFees.setLoanOffering(loanOffering);
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

	/**
	 * This method checks if the product loanProductName already exists.
	 * 
	 * @param loanProductName -
	 *            Name of the loanProductName which is being updated.
	 * @return - Returns false if the loanProductName already exists.
	 * 
	 */
	public boolean checkForDuplicateLoanNameForUpdate(String loanProductName,
			Short prdOfferingId) throws SystemException,ApplicationException {
		return getLoanProductDAO().ifUpdateProductNameExists(
				loanProductName, prdOfferingId);
	}

	/**
	 * This method checks if the product loanProductShortName already exists.
	 * 
	 * @param loanProductShortName -
	 *            Name of the loanProductShortName which is being updated.
	 * @return - Returns false if the loanProductShortName already exists.
	 * 
	 */
	public boolean checkForDuplicateShortLoanNameForUpdate(
			String loanProductShortName, Short prdOfferingId)
			throws SystemException, ApplicationException {
		return getLoanProductDAO().ifUpdateProductShortNameExists(
				loanProductShortName, prdOfferingId);
	}

	// ---------------------------------Load-------------------------------------------------
	/**
	 * Retrieves the master data required for the page. Master data is retrieved
	 * using MasterDataRetriever. The master data required is list of loan
	 * product categories. Drop down for applicable for, this should show group
	 * option only if allowed. List of fees applicable for loan product
	 * instances.
	 * 
	 * @param context
	 */

	public void loadInitial(Context context) throws SystemException,
			ApplicationException {
		prdLoanLogger.debug("In Load Initial of Loan Product");
		try {
			List<ProductCategory> loanProductCategoryList = getLoanProductDAO().getLoanProductCategories();
			context.addAttribute(getSearchResults(
					ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
					loanProductCategoryList));

			getMasterData(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * Retrieves the master data required for the page. Master data is retrieved
	 * using MasterDataRetriever. The master data required is list of loan
	 * product categories. Drop down for applicable for, this should show group
	 * option only if allowed. List of fees applicable for loan product
	 * instances.
	 * 
	 * @param context
	 */

	// ---------------------------------Mangae--------------------------------------------
	public void manageInitial(Context context) throws SystemException,
			ApplicationException {
		prdLoanLogger.debug("In Manage Initial of Loan Product");
		try {
			LoanOffering loanOffering = validateValueObject(context);
			ProductCategory productCategory = loanOffering.getPrdCategory();

			List<ProductCategory> loanProductCategoryList = getLoanProductDAO().getAllLoanProductCategories(productCategory);
			context.addAttribute(getSearchResults(
					ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
					loanProductCategoryList));

			getMasterData(context);
			
			GracePeriodType gracePeriodType = loanOffering.getGracePeriodType();
			if (null == gracePeriodType) {
				loanOffering.setGracePeriodType(new GracePeriodType());
			}

			Penalty penalty = loanOffering.getPenalty();
			if (null == penalty) {
				loanOffering.setPenalty(new Penalty());
			}

			InterestCalcRule interestCalcRule = loanOffering
					.getInterestCalcRule();
			if (null == interestCalcRule) {
				loanOffering.setInterestCalcRule(new InterestCalcRule());
			}

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	// ---------------------------------Preview--------------------------------------------
	/**
	 * This method is used to set the objects available in master data into the
	 * value object.
	 * 
	 * @param context
	 */
	public void previewInitial(Context context) throws SystemException,
			ApplicationException {
		prdLoanLogger.debug("In preview Initial of Loan Product");
		try {
			LoanOffering loanOffering = validateValueObject(context);
			Short productCategoryId = loanOffering.getPrdCategory()
					.getProductCategoryID();
			SearchResults searchResults = context
					.getSearchResultBasedOnName(ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST);
			checkForNull(searchResults,
					ProductDefinitionConstants.PRDINVALID);
			List<ProductCategory> list = (List<ProductCategory>) searchResults
					.getValue();
			for (Iterator<ProductCategory> iter = list.iterator(); iter
					.hasNext();) {
				ProductCategory productCategory = iter.next();
				if (productCategory.getProductCategoryID().equals(
						productCategoryId)) {
					loanOffering.setPrdCategory(productCategory);
					loanOffering.setPrdType(productCategory.getProductType());
				}
			}

			Short prdStatusId = loanOffering.getPrdStatus()
					.getOfferingStatusId();
			SearchResults searchResultsPrdStatus = context
					.getSearchResultBasedOnName(ProductDefinitionConstants.LOANPRDSTATUSLIST);
			checkForNull(searchResultsPrdStatus,
					ProductDefinitionConstants.PRDINVALID);
			List<MasterObject> prdStatusList = (List<MasterObject>) searchResultsPrdStatus
					.getValue();
			for (Iterator<MasterObject> iterPrdStatus = prdStatusList
					.iterator(); iterPrdStatus.hasNext();) {
				MasterObject masterObject = iterPrdStatus.next();
				if (masterObject.getId().equals(prdStatusId)) {
					PrdStatus status = new PrdStatus();
					status.setOfferingStatusId(masterObject.getId());
					status.setVersionNo(masterObject.getVersionNo());
					loanOffering.setPrdStatus(status);
				}
			}

			Set<LoanOfferingFund> loanOfferingFundSet = loanOffering
					.getLoanOffeingFundSet();
			Set<LoanOfferingFund> loanFundSet = new HashSet<LoanOfferingFund>();
			SearchResults searchResultsLoanSrcFunds = context
					.getSearchResultBasedOnName(ProductDefinitionConstants.SRCFUNDSLIST);
			checkForNull(searchResultsPrdStatus,
					ProductDefinitionConstants.PRDINVALID);
			List<Fund> prdSrcFundList = (List<Fund>) searchResultsLoanSrcFunds
					.getValue();
			//bug id 26418.added condition to check for null 
			if(null != loanOfferingFundSet) {
				for (LoanOfferingFund loanOfferingFund : loanOfferingFundSet) {
	
					for (Fund fund : prdSrcFundList) {
						if (loanOfferingFund.getFund().getFundId().equals(
								fund.getFundId())) {
							loanOfferingFund.setFund(fund);
							loanFundSet.add(loanOfferingFund);
						}
					}
				}
				loanOffering.setLoanOffeingFundSet(loanFundSet);
			}
			Set<PrdOfferingFees> loanOfferingFeesSet = loanOffering
					.getPrdOfferingFeesSet();
			Set<PrdOfferingFees> loanFeesSet = new HashSet<PrdOfferingFees>();
			SearchResults searchResultsLoanFees = context
					.getSearchResultBasedOnName(ProductDefinitionConstants.LOANFEESLIST);
			checkForNull(searchResultsLoanFees,
					ProductDefinitionConstants.PRDINVALID);
			List<Fees> prdFees = (List<Fees>) searchResultsLoanFees.getValue();
			//bug id 26418.added condition to check for null
			if(null != loanOfferingFeesSet) {
				for (PrdOfferingFees prdOfferingFees : loanOfferingFeesSet) {
					for (Fees fees : prdFees) {
						if (prdOfferingFees.getFees().getFeeId().equals(
								fees.getFeeId())) {
							prdOfferingFees.setFees(fees);
							loanFeesSet.add(prdOfferingFees);
						}
					}
				}
				loanOffering.setPrdOfferingFeesSet(loanFeesSet);
			}

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	// ---------------------------------Create--------------------------------------------
	/**
	 * Before creating it needs to do certain business logic validations like
	 * check for duplicacy of loan product instance name and short name. Check
	 * for loan repayment and fee freq match by calling
	 * checkForRepaymentFeeFreqMatch method.
	 * 
	 * @param context
	 * @throws RepmntFeeFreqMisMatchException
	 * @throws DuplicateProductInstanceException
	 */
	public void createInitial(Context context)
			throws RepmntFeeFreqMisMatchException, ApplicationException,
			SystemException {
		prdLoanLogger.debug("In Create Initial of Loan Product");
		try {
			LoanOffering loanOffering = validateValueObject(context);
	
			UserContext userContext = context.getUserContext();
			if (null != userContext) {
				loanOffering.setOffice(new Office(userContext.getBranchId()));
				loanOffering.setCreatedBy(Integer.valueOf(userContext.getId()));
			}
			loanOffering.setCreatedDate((new java.sql.Date(System
					.currentTimeMillis())));
	
			StringBuilder globalPrdOfferingNum = new StringBuilder();
			globalPrdOfferingNum.append(userContext.getBranchId());
			globalPrdOfferingNum.append("-");
			loanOffering.setGlobalPrdOfferingNum(globalPrdOfferingNum.toString());
	
			if (null != loanOffering.getGracePeriodType()) {
				if (loanOffering.getGracePeriodType().getGracePeriodTypeId() == null) {
					GracePeriodType gracePeriodType=new GracePeriodType();
					gracePeriodType.setGracePeriodTypeId(ProductDefinitionConstants.DEFAULTLOANGRACEPERIODTYPE);
					loanOffering.setGracePeriodType(gracePeriodType);
				}
			}
	
			if (null != loanOffering.getInterestCalcRule()) {
				if (loanOffering.getInterestCalcRule().getInterestCalcRuleId() == null) {
					loanOffering.setInterestCalcRule(null);
				}
			}
	
			if (null != loanOffering.getPenalty()) {
				if (loanOffering.getPenalty().getPenaltyID() == null) {
					loanOffering.setPenalty(null);
				}
			}
	
			Set<PrdOfferingFees> prdOfferingFeesSet = loanOffering
					.getPrdOfferingFeesSet();
			for (PrdOfferingFees prdOfferingFees : prdOfferingFeesSet) {
				prdOfferingFees.setLoanOffering(loanOffering);
			}
	
			PrdOfferingMeeting prdOfferingMeeting = loanOffering
					.getPrdOfferingMeeting();
			prdOfferingMeeting.setPrdOffering(loanOffering);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * It creates the Loan product Instance.Before creating it needs to do
	 * certain business logic validations like check for duplicacy of loan
	 * product instance name and short name. Check for loan repayment and fee
	 * freq match by calling checkForRepaymentFeeFreqMatch method.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void create(Context context) throws SystemException,
			ApplicationException {
		try {
			LoanOffering loanOffering = validateValueObject(context);
			String loanProductName = loanOffering.getPrdOfferingName();
			String loanProductShortName = loanOffering
					.getPrdOfferingShortName();
			prdLoanLogger.info("Checking for Duplicacy of product instance name" + loanProductName);
			if (checkForDuplicateNameForCreate(loanProductName)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTNAME);
			}
			prdLoanLogger.info("Checking for Duplicacy of product short name" + loanProductShortName);
			if (checkForDuplicateShortNameForCreate(loanProductShortName)) {
				throw new DuplicateProductInstanceException(
						ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
			}
			prdLoanLogger.info("Creating product" + loanProductName);
			getLoanProductDAO().saveLoanProduct(loanOffering);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * It checks for duplicacy of loan product instance names.
	 * 
	 * @param loanProductInstanceName
	 * @throws DuplicateProductInstanceException
	 */
	private boolean checkForDuplicateNameForCreate(
			String loanProductInstanceName)
			throws ApplicationException, SystemException {
		return getLoanProductDAO().ifCreateProductNameExists(loanProductInstanceName);
	}

	/**
	 * It checks for duplicacy of loan short product instance names.
	 * 
	 * @param loanProductInstanceName
	 * @throws DuplicateProductInstanceException
	 */
	private boolean checkForDuplicateShortNameForCreate(
			String loanProductInstanceShortName)
			throws ApplicationException, SystemException {
		return getLoanProductDAO().ifCreateProductShortNameExists(loanProductInstanceShortName);
	}

	// ---------------------------------Search--------------------------------------------
	/**
	 * Performs search and lists down all the product categories in the HO. It
	 * calls search method on the DAO and sets the result in the context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getLoanProducts(Context context) throws SystemException,
			ApplicationException {
		try {
			List<LoanOffering> loanOfferingList =getLoanProductDAO().search();
			context.addAttribute(getSearchResults(
					ProductDefinitionConstants.LOANPRODUCTLIST,
					loanOfferingList));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * this is overridden because we need to retrieve the object based on the
	 * categoryid which might not be the primary key.
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void get(Context context) throws SystemException,
			ApplicationException {
		prdLoanLogger.debug("In get of Loan Product");
		try {
			LoanOffering loanOffering = validateValueObject(context);
			Short prdOfferingId = loanOffering.getPrdOfferingId();
			loanOffering = getLoanProductDAO().get(prdOfferingId);
			context.setValueObject(loanOffering);
			
			getMasterData(context);

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * To get the change log of the product
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getChangeLogDetails(Context context) throws SystemException,ApplicationException {
		LoanOffering loanOffering = validateValueObject(context);
		if(null != loanOffering) {
			Integer prdOfferingId=Integer.valueOf(loanOffering.getPrdOfferingId());
			
			ClosedAccSearchDAO closedAccSearchDAO=(ClosedAccSearchDAO)getDAO(ClosedAccSearchConstants.GETPATHCLOSEDACCSEARCH);
			List<ClientChangeLog> clientChangeLogList=closedAccSearchDAO.getClientChangeLog(
					prdOfferingId,ProductDefinitionConstants.LOANENTITYTYPEID);
			context.addAttribute(new SearchResults(ProductDefinitionConstants.LOANCHANGELOGLIST,clientChangeLogList));
		}
	}
	
	// ---------------------------------helpers--------------------------------------------
	
	/**
	 * This helper method is used to get the Master data and then adds the master data to the 
	 * context. 
	 *  
	 */
	private void getMasterData(Context context) throws SystemException,
	ApplicationException {
		UserContext userContext = context.getUserContext();
		Short localeId = getUserLocaleId(userContext);

		SearchResults searchResultsApplFor = getLoanProductDAO().getPrdApplFor(localeId);
		context.addAttribute(searchResultsApplFor);

		SearchResults searchResultsYesNoMaster = getLoanProductDAO().getYesNoMaster(localeId);
		context.addAttribute(searchResultsYesNoMaster);

		SearchResults searchResultsGracePeriod = getLoanProductDAO().getGracePeriodTypes(localeId);
		context.addAttribute(searchResultsGracePeriod);

		SearchResults searchResultsInterestTypes =getLoanProductDAO().getInterestTypes(
				ProductDefinitionConstants.LOANID, localeId);
		context.addAttribute(searchResultsInterestTypes);

		SearchResults searchResultsInterestCalcRule = getLoanProductDAO().getInterestCalcRule(localeId);
		context.addAttribute(searchResultsInterestCalcRule);

		SearchResults searchResultsPenaltyTypes = getLoanProductDAO().getPenaltyTypes(
				ProductDefinitionConstants.LOANID, localeId);
		context.addAttribute(searchResultsPenaltyTypes);

		SearchResults searchResultsPrdStatus = getLoanProductDAO().getPrdStatus(localeId);
		context.addAttribute(searchResultsPrdStatus);

		SearchResults searchResultsSrcFunds = getLoanProductDAO().getSourcesOfFund();
		context.addAttribute(searchResultsSrcFunds);

		SearchResults searchResultsFees = getLoanProductDAO().getLoanFees();
		context.addAttribute(searchResultsFees);
		context.addAttribute(this.getSearchResults("principalGLCodes",getLoanProductDAO().getGLCodes(ProductDefinitionConstants.PRICIPALGLCODE)));
		context.addAttribute(this.getSearchResults("interestGLCodes",getLoanProductDAO().getGLCodes(ProductDefinitionConstants.INTERESTGLCODE)));
		context.addAttribute(this.getSearchResults("penaltyGLCodes",getLoanProductDAO().getGLCodes(ProductDefinitionConstants.PENALTYGLCODE)));

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
	private LoanOffering validateValueObject(Context context)
			throws ApplicationException, SystemException {
		LoanOffering valueObject = (LoanOffering) context.getValueObject();
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
	 * This method returns the DAO associated with LoanProduct.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private LoanProductDAO getLoanProductDAO() throws SystemException,ApplicationException {
		return (LoanProductDAO)getDAO(ProductDefinitionConstants.GETPATHLOANPRODUCT);
	}
	
}
