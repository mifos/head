/**

 * SavingsProductDAO.java    version: xxx

 

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
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
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
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.master.util.valueobjects.RecommendedAmntUnit;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.service.SavingsPrdPersistenceService;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.MasterObject;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.application.productdefinition.util.valueobjects.PrdStatus;
import org.mifos.application.productdefinition.util.valueobjects.ProductCategory;
import org.mifos.application.productdefinition.util.valueobjects.SavingsOffering;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.IllegalStateException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is used to make the databse related calls in the creation and
 * update of savings product.
 */
public class SavingsProductDAO extends PrdOfferingDAO {

	public SavingsProductDAO() {
	}

	/**
	 * This method gets all the active product product categories
	 * 
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<ProductCategory> getSavingsProductCategories()
			throws ApplicationException, SystemException {
		return getProductCategories(ProductDefinitionConstants.SAVINGSID,
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
	public List<ProductCategory> getAllSavingsProductCategories(
			ProductCategory productCategory) throws ApplicationException,
			SystemException {
		List<ProductCategory> productCategoryList = getProductCategories(
				ProductDefinitionConstants.SAVINGSID,
				ProductDefinitionConstants.ACTIVE);
		for (ProductCategory prdCategory : productCategoryList) {
			if (productCategory.getProductCategoryID().equals(
					prdCategory.getProductCategoryID())) {
				return productCategoryList;
			}
		}
		productCategoryList.add(productCategory);
		return productCategoryList;

	}

	/**
	 * This method is used to get the master data applicable for associated with
	 * the savings products.
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPrdApplFor(Short localeId)
			throws ApplicationException, SystemException {
		return getPrdApplFor(localeId,
				ProductDefinitionConstants.SAVINGSAPPLFORLIST);
	}
	
	public SearchResults getRecurrenceTypes() throws SystemException, ProductDefinitionException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.SAVINGSPRDRECURRENCETYPES,
					ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST);
			return masterDataRetriever.retrieve();
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		}
	}

	/**
	 * This method is used to get all the product status associated with savings
	 * products.
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getPrdStatus(Short localeId)
			throws ApplicationException, SystemException {
		return getPrdStatus(ProductDefinitionConstants.SAVINGSID, localeId,
				ProductDefinitionConstants.ACTIVE,
				ProductDefinitionConstants.SAVINGSPRDSTATUSLIST);
	}

	/**
	 * This method gets the master data of savings types for savings product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getSavingsTypeSearchResults(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.SAVINGSTYPES,
					ProductDefinitionConstants.SAVINGSTYPELIST);
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
	 * This method gets the master data of recommended unit for savings product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getRecommendedAmntUnitSearchResults(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.RECAMNTUNITS,
					ProductDefinitionConstants.RECAMNTUNITLIST);
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
	 * This method gets the master data of interest calculation types for
	 * savings product
	 * 
	 * @param localeId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getInterestCalcTypeSearchResults(Short localeId)
			throws ApplicationException, SystemException {
		try {
			MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
			masterDataRetriever.prepare(NamedQueryConstants.INTCALCTYPESTYPES,
					ProductDefinitionConstants.INTCALCTYPESLIST);
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
	 * Creates the savings product instance. Before saving it generates the
	 * global product offering number
	 * 
	 * @param savingsOffering
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void saveSavingsProduct(SavingsOffering savingsOffering)
			throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session
					.getNamedQuery(NamedQueryConstants.MAXSAVINGSPRODUCTID);
			int maxPrdID = query.uniqueResult() != null ? (Short) query
					.uniqueResult() : ProductDefinitionConstants.DEFAULTMAX;

			StringBuilder globalPrdOfferingNum = new StringBuilder();
			globalPrdOfferingNum.append(savingsOffering
					.getGlobalPrdOfferingNum());
			globalPrdOfferingNum.append(StringUtils.lpad(String
					.valueOf(++maxPrdID), '0', 3));
			savingsOffering.setGlobalPrdOfferingNum(globalPrdOfferingNum
					.toString());

			savingsOffering.setOffice((Office) session.get(Office.class,
					savingsOffering.getOffice().getOfficeId()));
			session.save(savingsOffering);
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
	 * This searches the savings products irrespective of their status. This
	 * obtains a collection of savings products.
	 * 
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<SavingsOffering> search() throws SystemException,
			ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.SAVINGSPRODUCT_SEARCH);
			List<SavingsOffering> savingsOfferingList = query.list();
			return savingsOfferingList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException(hbe);
		} catch (Exception e) {
			throw new ProductDefinitionException(
					ProductDefinitionConstants.PRDINVALID);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This will get the savings product value object from the database using
	 * hibernate.
	 * 
	 * @return
	 */
	public SavingsOffering get(Short prdOfferingId)
			throws ApplicationException, SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			SavingsOffering savingsOffering = (SavingsOffering) session.get(
					SavingsOffering.class, prdOfferingId);
			ProductCategory productCategory = savingsOffering.getPrdCategory();
			if (null != productCategory) {
				productCategory.getProductCategoryName();
			}

			PrdApplicableMaster prdApplicableMaster = savingsOffering
					.getPrdApplicableMaster();
			if (null != prdApplicableMaster) {
				prdApplicableMaster.getPrdApplicableMasterId();
			}

			InterestCalcType interestCalcType = savingsOffering
					.getInterestCalcType();
			if (null != interestCalcType) {
				interestCalcType.getInterestCalculationTypeID();
			}

			SavingsType savingsType = savingsOffering.getSavingsType();
			if (null != savingsType) {
				savingsType.getSavingsTypeId();
			}

			RecommendedAmntUnit recommendedAmntUnit = savingsOffering
					.getRecommendedAmntUnit();
			if (null != recommendedAmntUnit) {
				recommendedAmntUnit.getRecommendedAmntUnitId();
			} else {
				savingsOffering
						.setRecommendedAmntUnit(new RecommendedAmntUnit());
			}
			Set<PrdOfferingMeeting> prdOfferingMeetingSet = savingsOffering
					.getPrdOfferingMeetingSet();
			for (PrdOfferingMeeting prdOfferingMeeting : prdOfferingMeetingSet) {
				if (null != prdOfferingMeeting) {

					Meeting meeting = prdOfferingMeeting.getMeeting();
					if (null != meeting) {
						MeetingDetails meetingDetails = meeting
								.getMeetingDetails();
						meetingDetails.getRecurAfter();
						meetingDetails.getRecurrenceType().getRecurrenceId();
						meetingDetails.getRecurrenceType().getRecurrenceName();
					} else {
						meeting = new Meeting();
						meeting.setMeetingDetails(new MeetingDetails());
					}
					if (prdOfferingMeeting.getMeetingType().equals(
							MeetingType.SAVINGSTIMEPERFORINTCALC.getValue())) {
						savingsOffering
								.setTimePerForInstcalc(prdOfferingMeeting);
					} else if (prdOfferingMeeting.getMeetingType().equals(
							MeetingType.SAVINGSFRQINTPOSTACC.getValue())) {
						savingsOffering
								.setFreqOfPostIntcalc(prdOfferingMeeting);
					}
				}
			}

			PrdOfferingMeeting timePerForInstcalc = savingsOffering
					.getTimePerForInstcalc();
			if (null == timePerForInstcalc) {
				PrdOfferingMeeting prdOfferingMeetings = new PrdOfferingMeeting();
				Meeting meeting = new Meeting();
				MeetingDetails meetingDetails = new MeetingDetails();
				RecurrenceType recurrenceType = new RecurrenceType();
				meetingDetails.setRecurrenceType(recurrenceType);
				meetingDetails.setMeetingRecurrence(null);
				meeting.setMeetingDetails(meetingDetails);
				prdOfferingMeetings.setMeeting(meeting);
				savingsOffering.setTimePerForInstcalc(prdOfferingMeetings);
			}

			PrdOfferingMeeting freqOfPostIntcalc = savingsOffering
					.getFreqOfPostIntcalc();
			if (null == freqOfPostIntcalc) {
				PrdOfferingMeeting prdOfferingMeetings = new PrdOfferingMeeting();
				Meeting meeting = new Meeting();
				MeetingDetails meetingDetails = new MeetingDetails();
				RecurrenceType recurrenceType = new RecurrenceType();
				meetingDetails.setRecurrenceType(recurrenceType);
				meetingDetails.setMeetingRecurrence(null);
				meeting.setMeetingDetails(meetingDetails);
				prdOfferingMeetings.setMeeting(meeting);
				savingsOffering.setFreqOfPostIntcalc(prdOfferingMeetings);
			}
			return savingsOffering;
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
	 * Updates the savings product instance. Before updating, it checks for the
	 * validity of the start date. If the start date is changed to less than
	 * current date or if the product is active and the start date is changed,
	 * it will throw an exception
	 * 
	 * @param savingsOffering
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateSavingsProduct(SavingsOffering savingsOffering,
			Context context) throws SystemException, ApplicationException {
		Session session = null;
		Session updateSession = null;
		Transaction transaction = null;
		SavingsOffering databaseSavingsOffering = null;
		try {
			session = HibernateUtil.getSession();
			databaseSavingsOffering = (SavingsOffering) session.get(
					SavingsOffering.class, savingsOffering.getPrdOfferingId());
			Calendar currentCalendar = new GregorianCalendar();
			int year = currentCalendar.get(Calendar.YEAR);
			int month = currentCalendar.get(Calendar.MONTH);
			int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year, month, day);
			java.sql.Date currentDate = new java.sql.Date(currentCalendar
					.getTimeInMillis());
			if (!databaseSavingsOffering.getStartDate().equals(
					savingsOffering.getStartDate())) {
				if (databaseSavingsOffering.getStartDate().compareTo(
						currentDate) <= 0) {
					throw new ApplicationException(
							ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
				}
				if (savingsOffering.getStartDate().compareTo(currentDate) < 0)
					throw new ApplicationException(
							ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
				if (savingsOffering.getStartDate().compareTo(currentDate) == 0) {
					Short prdStatusId = ProductDefinitionConstants.SAVINGSACTIVE;
					SearchResults searchResultsPrdStatus = context
							.getSearchResultBasedOnName(ProductDefinitionConstants.SAVINGSPRDSTATUSLIST);
					List<MasterObject> prdStatusList = (List<MasterObject>) searchResultsPrdStatus
							.getValue();
					for (MasterObject masterObject : prdStatusList) {
						if (masterObject.getId().equals(prdStatusId)) {
							PrdStatus status = new PrdStatus();
							status.setOfferingStatusId(masterObject.getId());
							status.setVersionNo(masterObject.getVersionNo());
							savingsOffering.setPrdStatus(status);
						}
					}
				}
			}
			// Bug id 27752. Added code to get the meeting id from databse.
			Set<PrdOfferingMeeting> prdOfferingMeetingSet = databaseSavingsOffering
					.getPrdOfferingMeetingSet();
			for (PrdOfferingMeeting prdOfferingMeeting : prdOfferingMeetingSet) {
				if (null != prdOfferingMeeting) {
					Meeting meeting = prdOfferingMeeting.getMeeting();
					if (null != meeting) {
						MeetingDetails meetingDetails = meeting
								.getMeetingDetails();
						meetingDetails.getRecurAfter();
						meetingDetails.getRecurrenceType().getRecurrenceId();
						meetingDetails.getRecurrenceType().getRecurrenceName();
					} else {
						meeting = new Meeting();
						meeting.setMeetingDetails(new MeetingDetails());
					}
					if (prdOfferingMeeting.getMeetingType().equals(
							MeetingType.SAVINGSTIMEPERFORINTCALC.getValue())) {
						databaseSavingsOffering
								.setTimePerForInstcalc(prdOfferingMeeting);
					} else if (prdOfferingMeeting.getMeetingType().equals(
							MeetingType.SAVINGSFRQINTPOSTACC.getValue())) {
						databaseSavingsOffering
								.setFreqOfPostIntcalc(prdOfferingMeeting);
					}
				}
			}

			Set<PrdOfferingMeeting> prdOfferMeetingSet = savingsOffering
					.getPrdOfferingMeetingSet();
			for (PrdOfferingMeeting prdOfferingMeeting : prdOfferMeetingSet) {
				if (null != prdOfferingMeeting) {
					Meeting meeting = prdOfferingMeeting.getMeeting();
					if (null != meeting) {
						if (prdOfferingMeeting
								.getMeetingType()
								.equals(
										MeetingType.SAVINGSTIMEPERFORINTCALC.getValue())) {
							meeting.setMeetingId(databaseSavingsOffering
									.getTimePerForInstcalc().getMeeting()
									.getMeetingId());
							meeting.setVersionNo(databaseSavingsOffering
									.getTimePerForInstcalc().getMeeting()
									.getVersionNo());
							meeting.getMeetingDetails().setDetailsId(
									databaseSavingsOffering
											.getTimePerForInstcalc()
											.getMeeting().getMeetingDetails()
											.getDetailsId());
							meeting.getMeetingDetails().setVersionNo(
									databaseSavingsOffering
											.getTimePerForInstcalc()
											.getMeeting().getMeetingDetails()
											.getVersionNo());
							meeting.getMeetingDetails().getMeetingRecurrence()
									.setDetailsId(
											databaseSavingsOffering
													.getTimePerForInstcalc()
													.getMeeting()
													.getMeetingDetails()
													.getMeetingRecurrence()
													.getDetailsId());
							meeting.getMeetingDetails().getMeetingRecurrence()
									.setVersionNo(
											databaseSavingsOffering
													.getTimePerForInstcalc()
													.getMeeting()
													.getMeetingDetails()
													.getMeetingRecurrence()
													.getVersionNo());
						} else if (prdOfferingMeeting
								.getMeetingType()
								.equals(
										MeetingType.SAVINGSFRQINTPOSTACC.getValue())) {
							meeting.setMeetingId(databaseSavingsOffering
									.getFreqOfPostIntcalc().getMeeting()
									.getMeetingId());
							meeting.setVersionNo(databaseSavingsOffering
									.getFreqOfPostIntcalc().getMeeting()
									.getVersionNo());
							meeting.getMeetingDetails().setDetailsId(
									databaseSavingsOffering
											.getFreqOfPostIntcalc()
											.getMeeting().getMeetingDetails()
											.getDetailsId());
							meeting.getMeetingDetails().setVersionNo(
									databaseSavingsOffering
											.getFreqOfPostIntcalc()
											.getMeeting().getMeetingDetails()
											.getVersionNo());
							meeting.getMeetingDetails().getMeetingRecurrence()
									.setDetailsId(
											databaseSavingsOffering
													.getFreqOfPostIntcalc()
													.getMeeting()
													.getMeetingDetails()
													.getMeetingRecurrence()
													.getDetailsId());
							meeting.getMeetingDetails().getMeetingRecurrence()
									.setVersionNo(
											databaseSavingsOffering
													.getFreqOfPostIntcalc()
													.getMeeting()
													.getMeetingDetails()
													.getMeetingRecurrence()
													.getVersionNo());
						}
					}
				}
			}
			// Bug id 26783 & 27351 . Closed the session
			HibernateUtil.closeSession(session);

			LogValueMap mapValue = new LogValueMap();
			mapValue.put(AuditConstants.REALOBJECT, new SavingsOffering());
			mapValue.put("prdApplicableMaster", AuditConstants.REALOBJECT);
			mapValue.put("prdCategory", AuditConstants.REALOBJECT);
			mapValue.put("prdStatus", AuditConstants.REALOBJECT);
			mapValue.put("recommendedAmntUnit", AuditConstants.REALOBJECT);
			mapValue.put("savingsType", AuditConstants.REALOBJECT);
			mapValue.put("interestCalcType", AuditConstants.REALOBJECT);
			mapValue.put("prdOfferingMeetingSet", AuditConstants.REALOBJECT);
			mapValue.put("meeting", "prdOfferingMeetingSet");
			mapValue.put("meetingDetails", "meeting");
			updateSession = HibernateUtil
					.getSessionWithInterceptor(new LogInfo(savingsOffering
							.getPrdOfferingId(), "SavingsProduct", context,
							mapValue));
			transaction = updateSession.beginTransaction();
			updateSession.update(savingsOffering);
			transaction.commit();

			// bug id 27479 Added check for null to transaction before calling
			// rollback
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
			HibernateUtil.closeSession(updateSession);
		}
	}


	public List getGLCodes(String entityType) throws FinancialException {
		List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
		if (entityType.equals(ProductDefinitionConstants.DEPOSITGLCODE)) {
			FinancialActionBO finActionMandDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.MANDATORYDEPOSIT);
			Set<COABO> applicableMandCreditCategory = finActionMandDeposit
					.getApplicableCreditCOA();
			for (COABO coabo : applicableMandCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
			FinancialActionBO finActionVolDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.VOLUNTORYDEPOSIT);
			Set<COABO> applicableVolCreditCategory = finActionVolDeposit
					.getApplicableCreditCOA();
			for (COABO coabo : applicableVolCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
		} else if (entityType.equals(ProductDefinitionConstants.INTERESTGLCODE)) {
			FinancialActionBO finActionInterest = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.SAVINGS_INTERESTPOSTING);
			Set<COABO> applicableCreditCategory = finActionInterest
					.getApplicableDebitCOA();
			for (COABO coabo : applicableCreditCategory) {
				glCodeList.add(coabo.getAssociatedGlcode());
			}
		}
		return glCodeList;
	}
}
