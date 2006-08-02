/**

 * LoanProductDAO.java    version: 1.0

 

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

package org.mifos.application.productdefinition.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.InterestCalcRule;
import org.mifos.application.master.util.valueobjects.InterestTypes;
import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.penalty.util.valueobjects.Penalty;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.GracePeriodType;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.application.productdefinition.util.valueobjects.LoanOfferingFund;
import org.mifos.application.productdefinition.util.valueobjects.MasterObject;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingFees;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.application.productdefinition.util.valueobjects.PrdStatus;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is used to make the databse related calls in the creation and
 * update of loan product.
 * 
 * @author mohammedn
 * 
 */
public class LoanProductDAO extends PrdOfferingDAO {

	/**
	 * default constructor
	 */
	public LoanProductDAO() {
	}

	/**
	 * This method gets all the active product product categories
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<ProductCategory> getLoanProductCategories()
			throws ApplicationException, SystemException {
		return getProductCategories(ProductDefinitionConstants.LOANID,
				ProductDefinitionConstants.ACTIVE);
	}

	/**
	 * This method gets all the product categories and the product category
	 * associated with the product instance if the product category is inactive.
	 * 
	 * @param productCategory
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<ProductCategory> getAllLoanProductCategories(
			ProductCategory productCategory) throws ApplicationException,
			SystemException {
		List<ProductCategory> productCategoryList = getProductCategories(
				ProductDefinitionConstants.LOANID,
				ProductDefinitionConstants.ACTIVE);
		for (Iterator<ProductCategory> iter = productCategoryList.iterator(); iter
				.hasNext();) {
			if (productCategory.getProductCategoryID().equals(
					iter.next().getProductCategoryID())) {
				return productCategoryList;
			}
		}
		productCategoryList.add(productCategory);
		return productCategoryList;
	}

	/**
	 * This method is used to get the master data applicable for associated with
	 * the loan products.
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPrdApplFor(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDAPPLFORLOAN,
					ProductDefinitionConstants.LOANAPPLFORLIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.OFFERINGAPPLICENTERSID,
					ProductDefinitionConstants.OFFERINGAPPLICABLETOCENTERS);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get all the product status associated with loan
	 * products.
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPrdStatus(Short localeId)
			throws ApplicationException, SystemException {
		return getPrdStatus(ProductDefinitionConstants.LOANID, localeId,
				ProductDefinitionConstants.ACTIVE,
				ProductDefinitionConstants.LOANPRDSTATUSLIST);
	}

	/**
	 * This method gets the master data of loan cycle counter for loan product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getYesNoMaster(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.YESNOMASTER,
					ProductDefinitionConstants.YESNOMASTERLIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for grace period types of loan
	 * product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getGracePeriodTypes(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.GRACEPERIODTYPE,
					ProductDefinitionConstants.LOANGRACEPERIODTYPELIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for interest types of loan product
	 * 
	 * @param productTypeID
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getInterestTypes(Short productTypeID, Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.INTERESTTYPES,
					ProductDefinitionConstants.INTERESTTYPESLIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.PRODUCTTYPEID, productTypeID);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for interest calculation rules of
	 * loan product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getInterestCalcRule(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.INTERESTCACLRULES,
					ProductDefinitionConstants.INTERESTCALCRULELIST);
			masterDataRetriever.setParameter(
					ProductDefinitionConstants.LOCALEID, localeId);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for penalty types of loan product
	 * 
	 * @param productTypeID
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPenaltyTypes(Short productTypeID, Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PENALTYTYPES,
					ProductDefinitionConstants.PENALTYTYPESLIST);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for sources of funds of loan
	 * product
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getSourcesOfFund() throws ApplicationException,
			SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.PRDSRCFUNDS,
					ProductDefinitionConstants.SRCFUNDSLIST);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get master data for fees of loan product
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getLoanFees() throws ApplicationException,
			SystemException {
		Session session = HibernateUtil.getSession();
		try {
			Query query = session
					.getNamedQuery(NamedQueryConstants.PRDLOANFEES);
			query.setShort(ProductDefinitionConstants.LOANCATEGORYID,
					ProductDefinitionConstants.LOANCATEGORYIDVALUE);
			query.setShort(ProductDefinitionConstants.LOANFEESSTATUS,
					ProductDefinitionConstants.LOANFEESINACTIVEID);
			List<Fees> prdFees = (List<Fees>) query.list();
			for (Fees fees : prdFees) {
				if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
						FeeFrequencyType.PERIODIC.getValue())) {
					fees.getFeeFrequency().getFeeMeetingFrequency()
							.getMeetingDetails().getRecurrenceType()
							.getRecurrenceId();
				}
			}
			SearchResults searchResults = new SearchResults();
			searchResults.setValue(prdFees);
			searchResults
					.setResultName(ProductDefinitionConstants.LOANFEESLIST);
			return searchResults;
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * Creates the loan product instance.
	 * 
	 * @param loanOffering
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void saveLoanProduct(LoanOffering loanOffering)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session
					.getNamedQuery(NamedQueryConstants.MAXLOANPRODUCTID);
			int maxPrdID = query.uniqueResult() != null ? (Short) query
					.uniqueResult() : ProductDefinitionConstants.DEFAULTMAX;

			StringBuilder globalPrdOfferingNum = new StringBuilder();
			globalPrdOfferingNum.append(loanOffering.getGlobalPrdOfferingNum());
			globalPrdOfferingNum.append((StringUtils.lpad(String
					.valueOf(++maxPrdID), '0', 3)));
			loanOffering.setGlobalPrdOfferingNum(globalPrdOfferingNum
					.toString());

			loanOffering.setOffice((Office) session.get(Office.class,
					loanOffering.getOffice().getOfficeId()));
			session.save(loanOffering);
			transaction.commit();
		} catch (StaleObjectStateException sse) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new ConcurrencyException(sse);
		} catch (HibernateProcessException hbe) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new SystemException(hbe);
		} catch (Exception exception) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This will get the product category value object from the database using
	 * hibernate.
	 * 
	 * @return
	 */
	public LoanOffering get(Short prdOfferingId) throws ApplicationException,
			SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			LoanOffering loanOffering = (LoanOffering) session.get(
					LoanOffering.class, prdOfferingId);

			ProductCategory productCategory = loanOffering.getPrdCategory();
			if (null != productCategory) {
				productCategory.getProductCategoryName();
			}

			PrdApplicableMaster prdApplicableMaster = loanOffering
					.getPrdApplicableMaster();
			if (null != prdApplicableMaster) {
				prdApplicableMaster.getPrdApplicableMasterId();
			}

			InterestTypes interestTypes = loanOffering.getInterestTypes();
			if (null != interestTypes) {
				interestTypes.getInterestTypeId();
			}

			InterestCalcRule interestCalcRule = loanOffering
					.getInterestCalcRule();
			if (null != interestCalcRule) {
				interestCalcRule.getInterestCalcRuleId();
			} else {
				loanOffering.setInterestCalcRule(new InterestCalcRule());
			}

			Penalty penalty = loanOffering.getPenalty();
			if (null != penalty) {
				penalty.getPenaltyID();
				penalty.getPenaltyType();
			} else {
				loanOffering.setPenalty(new Penalty());
			}

			GracePeriodType gracePeriodType = loanOffering.getGracePeriodType();
			if (null != gracePeriodType) {
				gracePeriodType.getGracePeriodTypeId();
			} else {
				loanOffering.setGracePeriodType(new GracePeriodType());
			}

			Set<LoanOfferingFund> loanOfferingFundSet = loanOffering
					.getLoanOffeingFundSet();
			if (null != loanOfferingFundSet) {
				for (LoanOfferingFund loanOfferingFund : loanOfferingFundSet) {
					Fund fund = loanOfferingFund.getFund();
					fund.getFundName();
				}
			} else {
				loanOffering
						.setLoanOffeingFundSet(new HashSet<LoanOfferingFund>());
			}

			Set<PrdOfferingFees> prdOfferingFeesSet = loanOffering
					.getPrdOfferingFeesSet();
			if (null != prdOfferingFeesSet) {
				for (PrdOfferingFees prdOfferingFees : prdOfferingFeesSet) {
					Fees fees = prdOfferingFees.getFees();
					fees.getFeeName();
					if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
							FeeFrequencyType.PERIODIC.getValue())) {
						fees.getFeeFrequency().getFeeMeetingFrequency()
								.getMeetingDetails().getRecurrenceType()
								.getRecurrenceId();
					}
				}
			} else {
				loanOffering
						.setPrdOfferingFeesSet(new HashSet<PrdOfferingFees>());
			}

			PrdOfferingMeeting prdOfferingMeeting = loanOffering
					.getPrdOfferingMeeting();
			if (null != prdOfferingMeeting) {

				Meeting meeting = prdOfferingMeeting.getMeeting();
				MeetingDetails meetingDetails = meeting.getMeetingDetails();
				meetingDetails.getRecurAfter();
				meetingDetails.getRecurrenceType().getRecurrenceId();
				meetingDetails.getRecurrenceType().getRecurrenceName();
			} else {
				PrdOfferingMeeting prdOfferingMeetings = new PrdOfferingMeeting();
				Meeting meeting = new Meeting();
				MeetingDetails meetingDetails = new MeetingDetails();
				RecurrenceType recurrenceType = new RecurrenceType();
				meetingDetails.setRecurrenceType(recurrenceType);
				meetingDetails.setMeetingRecurrence(null);
				meeting.setMeetingDetails(meetingDetails);
				prdOfferingMeetings.setMeeting(meeting);
				loanOffering.setPrdOfferingMeeting(prdOfferingMeetings);
			}
			return loanOffering;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception exception) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * Updates the loan product instance.
	 * 
	 * @param loanOffering
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateLoanProduct(LoanOffering loanOffering, Context context)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;
		try {
			// Bug id 26783 & 27351 . Closed the session
			LogValueMap mapValue = new LogValueMap();
			mapValue.put(AuditConstants.REALOBJECT, new LoanOffering());
			mapValue.put("gracePeriodType", AuditConstants.REALOBJECT);
			mapValue.put("interestCalcRule", AuditConstants.REALOBJECT);
			mapValue.put("interestTypes", AuditConstants.REALOBJECT);
			mapValue.put("penalty", AuditConstants.REALOBJECT);
			mapValue.put("prdApplicableMaster", AuditConstants.REALOBJECT);
			mapValue.put("prdCategory", AuditConstants.REALOBJECT);
			mapValue.put("prdOfferingMeeting", AuditConstants.REALOBJECT);
			mapValue.put("meeting", "prdOfferingMeeting");
			mapValue.put("meetingDetails", "meeting");
			mapValue.put("recurrenceType", "meetingDetails");
			mapValue.put("prdStatus", AuditConstants.REALOBJECT);
			mapValue.put("prdOfferingFeesSet", AuditConstants.REALOBJECT);
			mapValue.put("fees", "prdOfferingFeesSet");

			mapValue.put("principalGLCode", AuditConstants.REALOBJECT);
			mapValue.put("interestGLCode", AuditConstants.REALOBJECT);
			mapValue.put("penaltyGLCode", AuditConstants.REALOBJECT);
			mapValue.put("loanOffeingFundSet", AuditConstants.REALOBJECT);
			mapValue.put("fund", "loanOffeingFundSet");

			session = HibernateUtil.getSessionWithInterceptor(new LogInfo(
					loanOffering.getPrdOfferingId(), "LoanProduct", context,
					mapValue));

			transaction = session.beginTransaction();
			Calendar currentCalendar = new GregorianCalendar();
			int year = currentCalendar.get(Calendar.YEAR);
			int month = currentCalendar.get(Calendar.MONTH);
			int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year, month, day);
			java.sql.Date currentDate = new java.sql.Date(currentCalendar
					.getTimeInMillis());
			LoanOffering databaseLoanOffering = (LoanOffering) session.get(
					LoanOffering.class, loanOffering.getPrdOfferingId());
			databaseLoanOffering.setPrdStatus(loanOffering.getPrdStatus());
			if (!databaseLoanOffering.getStartDate().equals(
					loanOffering.getStartDate())) {
				if (databaseLoanOffering.getStartDate().compareTo(currentDate) <= 0) {
					throw new ApplicationException(
							ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
				}
				if (loanOffering.getStartDate().compareTo(currentDate) < 0) {
					throw new ApplicationException(
							ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
				}
				if (loanOffering.getStartDate().compareTo(currentDate) == 0) {
					Short prdStatusId = ProductDefinitionConstants.LOANACTIVE;
					SearchResults searchResultsPrdStatus = context
							.getSearchResultBasedOnName(ProductDefinitionConstants.LOANPRDSTATUSLIST);
					List<MasterObject> prdStatusList = (List<MasterObject>) searchResultsPrdStatus
							.getValue();
					for (MasterObject masterObject : prdStatusList) {
						if (masterObject.getId().equals(prdStatusId)) {
							PrdStatus status = new PrdStatus();
							status.setOfferingStatusId(masterObject.getId());
							status.setVersionNo(masterObject.getVersionNo());
							databaseLoanOffering.setPrdStatus(status);
							loanOffering.setPrdStatus(status);
						}
					}
				}
			}
			databaseLoanOffering.setDefaultLoanAmount(loanOffering
					.getDefaultLoanAmount());
			databaseLoanOffering.setDefaultPeriod(loanOffering
					.getDefaultPeriod());
			databaseLoanOffering.setDefInterestRate(loanOffering
					.getDefInterestRate());
			databaseLoanOffering.setDefNoInstallments(loanOffering
					.getDefNoInstallments());
			databaseLoanOffering.setDescription(loanOffering.getDescription());
			databaseLoanOffering.setStartDate(loanOffering.getStartDate());
			databaseLoanOffering.setEndDate(loanOffering.getEndDate());
			databaseLoanOffering.setGracePeriodDuration(loanOffering
					.getGracePeriodDuration());
			// bug id 26342 . Added the graceperiod type to the valueobject
			databaseLoanOffering.setGracePeriodType(loanOffering
					.getGracePeriodType());
			databaseLoanOffering.setIntDedDisbursementFlag(loanOffering
					.getIntDedDisbursementFlag());
			databaseLoanOffering.setInterestCalcRule(loanOffering
					.getInterestCalcRule());
			databaseLoanOffering.setInterestTypes(loanOffering
					.getInterestTypes());
			databaseLoanOffering.setLateness(loanOffering.getLateness());
			databaseLoanOffering.setLoanCounterFlag(loanOffering
					.getLoanCounterFlag());
			Set<LoanOfferingFund> databaseLoanOfferingFundSet = databaseLoanOffering
					.getLoanOffeingFundSet();
			if (null != databaseLoanOfferingFundSet) {
				databaseLoanOfferingFundSet.clear();
				databaseLoanOfferingFundSet.addAll(loanOffering
						.getLoanOffeingFundSet());
			}
			Set<PrdOfferingFees> databasePrdOfferingFeesSet = databaseLoanOffering
					.getPrdOfferingFeesSet();
			if (null != databasePrdOfferingFeesSet) {
				databasePrdOfferingFeesSet.clear();
				databasePrdOfferingFeesSet.addAll(loanOffering
						.getPrdOfferingFeesSet());
			}
			databaseLoanOffering.setMaxInterestRate(loanOffering
					.getMaxInterestRate());
			databaseLoanOffering.setMaxLoanAmount(loanOffering
					.getMaxLoanAmount());
			databaseLoanOffering.setMaxNoInstallments(loanOffering
					.getMaxNoInstallments());
			databaseLoanOffering.setMaxPeriod(loanOffering.getMaxPeriod());
			databaseLoanOffering.setMinInterestRate(loanOffering
					.getMinInterestRate());
			databaseLoanOffering.setMinLoanAmount(loanOffering
					.getMinLoanAmount());
			databaseLoanOffering.setMinNoInstallments(loanOffering
					.getMinNoInstallments());
			databaseLoanOffering.setMinPeriod(loanOffering.getMinPeriod());
			databaseLoanOffering.setPenalty(loanOffering.getPenalty());
			databaseLoanOffering
					.setPenaltyGrace(loanOffering.getPenaltyGrace());
			databaseLoanOffering.setPenaltyRate(loanOffering.getPenaltyRate());
			databaseLoanOffering.setPrdApplicableMaster(loanOffering
					.getPrdApplicableMaster());
			databaseLoanOffering.setPrdCategory(loanOffering.getPrdCategory());
			databaseLoanOffering.setPrdOfferingName(loanOffering
					.getPrdOfferingName());
			databaseLoanOffering.setPrdOfferingShortName(loanOffering
					.getPrdOfferingShortName());
			// Bug id 27752. Added code to get the meeting id from databse.
			databaseLoanOffering.getPrdOfferingMeeting().getMeeting()
					.getMeetingDetails().setRecurAfter(
							loanOffering.getPrdOfferingMeeting().getMeeting()
									.getMeetingDetails().getRecurAfter());
			databaseLoanOffering.getPrdOfferingMeeting().getMeeting()
					.getMeetingDetails().setRecurrenceType(
							loanOffering.getPrdOfferingMeeting().getMeeting()
									.getMeetingDetails().getRecurrenceType());
			// bug id 26342 . Added the PrinDueLastInstFlag to the valueobject
			databaseLoanOffering.setPrinDueLastInstFlag(loanOffering
					.getPrinDueLastInstFlag());
			databaseLoanOffering.setUpdatedBy(loanOffering.getUpdatedBy());
			databaseLoanOffering.setUpdatedDate(loanOffering.getUpdatedDate());
			databaseLoanOffering.setPrincipalGLCode(loanOffering
					.getPrincipalGLCode());
			databaseLoanOffering.setInterestGLCode(loanOffering
					.getInterestGLCode());
			databaseLoanOffering.setPenaltyGLCode(loanOffering
					.getPenaltyGLCode());

			session.update(databaseLoanOffering);
			transaction.commit();

		} catch (StaleObjectStateException sse) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new ConcurrencyException(sse);
		} catch (HibernateProcessException hbe) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new SystemException(hbe);
		} catch (ApplicationException ae) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw ae;
		} catch (Exception exception) {
			if (null != transaction) {
				transaction.rollback();
			}
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This searches the loan products irrespective of their status. This
	 * obtains a collection of loan products.
	 * 
	 * @param
	 * @throws SystemException-
	 *             this is thrown if there is any hibernate exception
	 * @throws ApplicationException
	 */
	public List<LoanOffering> search() throws SystemException,
			ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.LOANPRODUCT_SEARCH);
			List<LoanOffering> loadProductList = query.list();

			return loadProductList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List getGLCodes(String entityType) throws FinancialException {
		List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
		if (entityType.equals(ProductDefinitionConstants.PRICIPALGLCODE)) {
			FinancialActionBO finActionPrincipal = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
			Set<COABO> applicableCreditCategory = finActionPrincipal
					.getApplicableCreditCOA();
			for (COABO coabo : applicableCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
		} else if (entityType.equals(ProductDefinitionConstants.INTERESTGLCODE)) {
			FinancialActionBO finActionInterest = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.INTERESTPOSTING);
			Set<COABO> applicableCreditCategory = finActionInterest
					.getApplicableCreditCOA();
			for (COABO coabo : applicableCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
		}
		if (entityType.equals(ProductDefinitionConstants.PENALTYGLCODE)) {
			FinancialActionBO finActionPenalty = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.PENALTYPOSTING);
			Set<COABO> applicableCreditCategory = finActionPenalty
					.getApplicableCreditCOA();
			for (COABO coabo : applicableCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
		}
		return glCodeList;
	}
}
