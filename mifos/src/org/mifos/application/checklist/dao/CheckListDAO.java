/**

 * CheckListDAO  version: 1.0



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

package org.mifos.application.checklist.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.valueobjects.AccountCheckList;
import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.checklist.util.helpers.CheckListHelper;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.checklist.util.valueobjects.CheckList;
import org.mifos.application.checklist.util.valueobjects.CheckListDetail;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.checklist.util.valueobjects.CheckListSearchMaster;
import org.mifos.application.checklist.util.valueobjects.CustomerCheckList;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;

/**
 * CheckListDAO contains code for getting master data and setter methods for
 * CheckList Module
 * 
 * @author imtiyazmb 
 * 
 */

public class CheckListDAO extends DAO {

	public CheckListDAO() {
		super();
	}

	/**
	 * returns category type this method is called on load which returns product
	 * or client type
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void loadMasterdata(Context context,Short localeId) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		List<CheckListMaster> customerList = null;
		List<CheckListMaster> prdList = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();			
			Query customerQuery = session.getNamedQuery(CheckListConstants.CUSTOMER_LOADMASTERDATA);
			customerQuery.setShort("localeId",localeId);
  		  	customerList= customerQuery.list();
			transaction.commit();
			for (Iterator iter = customerList.iterator(); iter.hasNext();) {
				CheckListMaster element = (CheckListMaster) iter.next();
				element.setCheckListStatus((short) 0);

			}			
			Query productQuery = session.getNamedQuery(CheckListConstants.PRODUCT_LOADMASTERDATA);
			productQuery.setShort("localeId",localeId);
			prdList= productQuery.list();			
			transaction.commit();
			for (Iterator iter = prdList.iterator(); iter.hasNext();) {
				CheckListMaster element = (CheckListMaster) iter.next();
				element.setCheckListStatus((short) 1);

			}

			customerList.addAll(prdList);
			CheckListHelper.saveInContext(CheckListConstants.CATAGORY,
					customerList, context);

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * this function loads all the master data required when the user
	 * clicks on view all checklists
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getCheckListMasterdata(Context context,Short localeId) throws SystemException,
			ApplicationException {

		Session session = null;
		Transaction transaction = null;
		List<CheckListSearchMaster> checkListMaster = null;
		List<CheckListSearchMaster> checkListMasterCustomer = null;
		List<CheckListMaster> prdList = null;
		List<CheckListMaster> customerList = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query customer_checkListMaster = session.getNamedQuery(CheckListConstants.CUSTOMER_CHECKLISTMASTER_GETCHECKLISTMASTERDATA);
			customer_checkListMaster.setShort("localeId",localeId);
			checkListMaster= customer_checkListMaster.list();			
			for (CheckListSearchMaster checkListSearchMaster : checkListMaster) {
				checkListSearchMaster.setRecordType(1);
			}			
			Query product_checkListMaster = session.getNamedQuery(CheckListConstants.PRODUCT_CHECKLISTMASTER_GETCHECKLISTMASTERDATA);
			product_checkListMaster.setShort("localeId",localeId);
			prdList= product_checkListMaster.list();			
			transaction.commit();
			for (Iterator iter = prdList.iterator(); iter.hasNext();) {
				CheckListMaster element = (CheckListMaster) iter.next();
				element.setCheckListStatus((short) 1);

			}
			
			Query customer_checkListDetails = session.getNamedQuery(CheckListConstants.CUSTOMER_CHECKLISTDETAILS_GETCHECKLISTMASTERDATA);
			customer_checkListDetails.setShort("localeId",localeId);
			customerList= customer_checkListDetails.list();
			for (Iterator iter = customerList.iterator(); iter.hasNext();) {
				CheckListMaster element = (CheckListMaster) iter.next();
				element.setCheckListStatus((short) 0);

			}
			
			Query checkListDetails = session.getNamedQuery(CheckListConstants.CHECKLIST_GETCHELISTMASTERDATA);
			checkListDetails.setShort("localeId",localeId);
			checkListMasterCustomer= checkListDetails.list();
			for (CheckListSearchMaster checkListSearchMaster : checkListMasterCustomer) {

				checkListSearchMaster.setRecordType(0);

			}
			CheckListHelper.saveInContext("PrdSearchMaster", checkListMaster,
					context);
			CheckListHelper.saveInContext("PrdTypeMaster", prdList, context);
			CheckListHelper.saveInContext("CustomerLevelMaster", customerList,
					context);
			CheckListHelper.saveInContext("CustomerSearchMaster",
					checkListMasterCustomer, context);

			transaction.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *This method is called from businessprocessor to load the status of parent
	 * category type ie client or product	  
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getStatus(Context context,Short localeId) throws SystemException,ApplicationException {
		Short level=null;
		Short typeId=null;		
		List<StatusMaster> statusList = null;
		Session session = null;
		Transaction transaction = null;
		Query query = null;
		CheckListHelper checkListHelper = new CheckListHelper();	 
		
		if(context.getSearchResultBasedOnName("Level").getValue().toString().trim()!=null){
			level = Short.valueOf(context.getSearchResultBasedOnName("Level").getValue().toString());
		}
		if(context.getSearchResultBasedOnName("Type").getValue().toString().trim()!=null){
			typeId = Short.valueOf(context.getSearchResultBasedOnName("Type").getValue().toString());
		}
		
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			if (typeId.shortValue() == 0 ) {
				query = session.getNamedQuery(NamedQueryConstants.MASTERDATA_STATUS);				
			} 
			else if (typeId.shortValue() == 1) {
				query = session.getNamedQuery(CheckListConstants.PRODUCTSTATUSLIST);							
			}			
			query.setShort("localeId",localeId);
			query.setShort("levelId",level);
			statusList= query.list();			
			CheckListHelper.saveInContext("StateMaster", checkListHelper.pendingApprovalRequired(typeId,level,statusList,context), context);
			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * this method returns the record of a particular checklist 
	 * depending upon type ie customer checklist or product checklist
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void get(Context context) throws SystemException,
			ApplicationException {
		
		getCheckListMasterdata(context,Short.valueOf(CheckListConstants.LOCALEID));
		Session session = null;
		Transaction transaction = null;
		CheckList uiCheckList = (CheckList) context.getValueObject();
		CustomerCheckList customerCheckList = null;
		AccountCheckList accountCheckList = null;
		List<CheckListMaster> prdList = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			String type =  context.getSearchResultBasedOnName("Type").getValue().toString();			
			Short parentCheckListId=uiCheckList.getChecklistId();			
			CheckList cklst =(CheckList) session.get(CheckList.class, parentCheckListId);
			if (null != type && type.equals("0")) {

				if (null != parentCheckListId) {

					//String str_parentCheckListId = parentCheckListId.toString();
					//customerCheckList = (CustomerCheckList) session.get(CustomerCheckList.class, Short.valueOf(str_parentCheckListId));

					//CheckList cklst = customerCheckList.getCheckList();
					
					
					customerCheckList = cklst.getCustomerChecklist();	
					if (null != cklst) {

						cklst.getChecklistId();
						cklst.getChecklistName();
						cklst.getChecklistStatus();
						Set descriptionSet = cklst.getChecklistDetailSet();
						if (null != descriptionSet) {
							for (Iterator iter = descriptionSet.iterator(); iter
									.hasNext();) {
								CheckListDetail element = (CheckListDetail) iter
										.next();
								if (null != element) {
									element.getDetailText();
								}

							}
						}
					}
				}
				CheckListHelper.saveInContext("CheckListParent",
						customerCheckList, context);
				CheckListHelper.saveInContext("Type", (Object) 0, context);				
				Query query = session.getNamedQuery(CheckListConstants.PRODUCTLIST_GET);
				query.setShort("localeId",Short.valueOf(CheckListConstants.LOCALEID));				
				prdList= query.list();
				getStatus(context,Short.valueOf(CheckListConstants.LOCALEID));
				transaction.commit();
				CheckListHelper.saveInContext("Level", prdList, context);

			} else if (null != type && type.equals("1")) {
				if (null != parentCheckListId) {

					accountCheckList = cklst.getAccountCheckList();

					if (null != cklst) {
						cklst.getChecklistId();
						cklst.getChecklistName();
						cklst.getChecklistStatus();

						Set descriptionSet = cklst.getChecklistDetailSet();
						if (null != descriptionSet) {
							for (Iterator iter = descriptionSet.iterator(); iter
									.hasNext();) {
								CheckListDetail element = (CheckListDetail) iter
										.next();
								if (null != element) {
									element.getDetailText();
								}

							}
						}
					}

				}
				CheckListHelper.saveInContext("CheckListParent",
						accountCheckList, context);
				CheckListHelper.saveInContext("Type", Short.valueOf("1"),
						context);
				Query query = session.getNamedQuery(CheckListConstants.PRDLIST_GET);
				query.setShort("localeId",Short.valueOf(CheckListConstants.LOCALEID));				
				prdList= query.list();				
				getStatus(context,Short.valueOf(CheckListConstants.LOCALEID));
				CheckListHelper.saveInContext("Level", prdList, context);
			}
			Query query=session.getNamedQuery(CheckListConstants.CREATEDBY);			
			query.setShort("PERSONNEL_ID",cklst.getCreatedBy());			
			CheckListHelper.saveInContext("CreatedBy",(String)query.uniqueResult(),context);			
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * overrides super method
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void update(Context context) throws ApplicationException,
			SystemException {

		CheckList chkList = (CheckList) context.getValueObject();
		

		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			Short level = (Short) context.getSearchResultBasedOnName("Level")
					.getValue();
			Short type = (Short) context.getSearchResultBasedOnName("Type")
					.getValue();

			Object obj = context.getSearchResultBasedOnName("CheckListParent")
					.getValue();

			if (obj instanceof AccountCheckList) {
				AccountCheckList accountCheckList = (AccountCheckList) obj;
				// if we are here the original object retrived was
				// AccountCheckList compare with this now
				if (type.shortValue() != 1) // it means user has chaged the type
				// to other we need to delete this
				// record
				{
					CheckList checkList=(CheckList)session.get(CheckList.class,accountCheckList.getChecklistId());	
					if (type.shortValue() == 0) {
						
						session.delete(checkList.getAccountCheckList());					
						checkList.setAccountCheckList(null);
						checkList.setCustomerChecklist(chkList.getCustomerChecklist());					
						
						Set<CheckListDetail> checkListDetailSetOld =checkList.getChecklistDetailSet();
						for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {
							session.delete(checkListDetailOld);
						}											
						checkListDetailSetOld.clear();				
						
						Set<CheckListDetail> checkListDetailSet = chkList.getChecklistDetailSet();
						for (CheckListDetail checkListDetail : checkListDetailSet) {
							checkListDetail.setCheckList(chkList);
							checkListDetail.setAnswerType(Short.valueOf("1"));
							checkList.addCheckListDetail(checkListDetail);
						}
						checkList.setUpdatedBy(context.getUserContext().getId());		
						checkList.setUpdatedDate( new Date(System.currentTimeMillis()));					
						session.update(checkList);
					}
				} else {
//					 if he has changed the level with in account
					if (level.shortValue() != accountCheckList
							.getAccountTypeId().shortValue()
							&& chkList.getAccountCheckList()
									.getAccountStatusId().shortValue() != accountCheckList.getAccountStatusId().shortValue()) {
						// we need to update the record itself
						// accountCheckList.setAccountTypeId(level);
						AccountCheckList accCheckList = chkList
								.getAccountCheckList();
						// this is context accchecklist ie present
						// set the original id
						accCheckList.setChecklistId(accountCheckList.getChecklistId());
						
						SupportedLocales supportedLocales = new SupportedLocales();
						supportedLocales.setLocaleId(Short.valueOf("1"));
						chkList.setSupportedLocales(supportedLocales);

						chkList.setCustomerChecklist(null);

						// put the checklist details also
						chkList.setChecklistId(accountCheckList.getCheckList()
								.getChecklistId());
						Set<CheckListDetail> checkListDetailSet = chkList.getChecklistDetailSet();

						for (CheckListDetail checkListDetail : checkListDetailSet) {
							checkListDetail.setCheckList(chkList);
							checkListDetail.setAnswerType(Short.valueOf("1"));
						}

						Set<CheckListDetail> checkListDetailSetOld = accountCheckList.getCheckList().getChecklistDetailSet();

						for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {

							session.delete(checkListDetailOld);
						}
						accCheckList.setCheckList(chkList);
						chkList.setAccountCheckList(accCheckList);
						chkList.setCreatedBy(accountCheckList.getCheckList().getCreatedBy());
						chkList.setCreatedDate(accountCheckList.getCheckList().getCreatedDate());
						chkList.setUpdatedBy(context.getUserContext().getId());		
						chkList.setUpdatedDate( new Date(System.currentTimeMillis()));
						session.update(chkList);

					} else if (level.shortValue() == accountCheckList
							.getAccountTypeId().shortValue()
							&& chkList.getAccountCheckList()
									.getAccountStatusId().shortValue() != accountCheckList
									.getAccountStatusId().shortValue()) {

						chkList.setCustomerChecklist(null);
						chkList.setChecklistId(accountCheckList.getCheckList()
								.getChecklistId());
						SupportedLocales supportedLocales = new SupportedLocales();
						supportedLocales.setLocaleId(Short.valueOf("1"));
						chkList.setSupportedLocales(supportedLocales);
						AccountCheckList accCheckList = chkList
								.getAccountCheckList();
						accCheckList.setChecklistId(accountCheckList.getChecklistId());						
						Set<CheckListDetail> checkListDetailSet = chkList
								.getChecklistDetailSet();

						for (CheckListDetail checkListDetail : checkListDetailSet) {
							checkListDetail.setCheckList(chkList);
							checkListDetail.setAnswerType(Short.valueOf("1"));
						}

						Set<CheckListDetail> checkListDetailSetOld = accountCheckList
								.getCheckList().getChecklistDetailSet();

						for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {

							session.delete(checkListDetailOld);
						}
						chkList.setCreatedBy(accountCheckList.getCheckList().getCreatedBy());
						chkList.setCreatedDate(accountCheckList.getCheckList().getCreatedDate());
						chkList.setUpdatedBy(context.getUserContext().getId());		
						chkList.setUpdatedDate( new Date(System.currentTimeMillis()));
						accCheckList.setCheckList(chkList);
						chkList.setAccountCheckList(accCheckList);
						session.update(chkList);
					}

					else {
						chkList.setCustomerChecklist(null);
						chkList.setAccountCheckList(null);
						chkList.setChecklistId(accountCheckList.getCheckList()
								.getChecklistId());
						SupportedLocales supportedLocales = new SupportedLocales();
						supportedLocales.setLocaleId(Short.valueOf("1"));
						chkList.setSupportedLocales(supportedLocales);

						Set<CheckListDetail> checkListDetailSet = chkList
								.getChecklistDetailSet();

						for (CheckListDetail checkListDetail : checkListDetailSet) {
							checkListDetail.setCheckList(chkList);
							checkListDetail.setAnswerType(Short.valueOf("1"));
						}

						Set<CheckListDetail> checkListDetailSetOld = accountCheckList
								.getCheckList().getChecklistDetailSet();

						for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {

							session.delete(checkListDetailOld);
						}
						chkList.setCreatedBy(accountCheckList.getCheckList().getCreatedBy());
						chkList.setCreatedDate(accountCheckList.getCheckList().getCreatedDate());
						chkList.setUpdatedBy(context.getUserContext().getId());		
						chkList.setUpdatedDate( new Date(System.currentTimeMillis()));
						session.update(chkList);

					}

				}

			}

			else if (obj instanceof CustomerCheckList) {
				CustomerCheckList customerCheckList = (CustomerCheckList) obj;

				if (type.shortValue() != 0) // it means user has chaged the type
				// to other we need to delete this
				// record
				{					
					if (type.shortValue() == 1) {		
							
							CheckList checkList=(CheckList)session.get(CheckList.class,customerCheckList.getChecklistId());							
							session.delete(checkList.getCustomerChecklist());							
							checkList.setCustomerChecklist(null);							
							checkList.setAccountCheckList(chkList.getAccountCheckList());
							checkList.getAccountCheckList().setCheckList(checkList);
							
							Set<CheckListDetail> checkListDetailSetOld =checkList.getChecklistDetailSet();
							for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {
								session.delete(checkListDetailOld);
							}		
							checkListDetailSetOld.clear();				
							
							Set<CheckListDetail> checkListDetailSet = chkList.getChecklistDetailSet();
							for (CheckListDetail checkListDetail : checkListDetailSet) {
								checkListDetail.setCheckList(chkList);
								checkListDetail.setAnswerType(Short.valueOf("1"));
								checkList.addCheckListDetail(checkListDetail);
							}
							checkList.setUpdatedBy(context.getUserContext().getId());		
							checkList.setUpdatedDate( new Date(System.currentTimeMillis()));							
							session.update(checkList);							
					}
				} else {

					// if he has changed the level with in account
					if (level.shortValue() != customerCheckList.getLevelId()
							.shortValue()) {
						if (chkList.getCustomerChecklist().getCustomerState()
								.getStatusId() != customerCheckList
								.getCustomerState().getStatusId()) {
							// if level in customertable changed and status
							// changed
							chkList.setAccountCheckList(null);
							chkList.setChecklistId(customerCheckList
									.getCheckList().getChecklistId());
							SupportedLocales supportedLocales = new SupportedLocales();
							supportedLocales.setLocaleId(Short.valueOf("1"));
							chkList.setSupportedLocales(supportedLocales);

							Set<CheckListDetail> checkListDetailSet = chkList
									.getChecklistDetailSet();

							for (CheckListDetail checkListDetail : checkListDetailSet) {
								checkListDetail.setCheckList(chkList);
								checkListDetail.setAnswerType(Short
										.valueOf("1"));
							}
							Set<CheckListDetail> checkListDetailSetOld = customerCheckList
									.getCheckList().getChecklistDetailSet();

							for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {

								session.delete(checkListDetailOld);
							}
							CustomerCheckList cc = chkList
									.getCustomerChecklist();
							cc.setChecklistId(customerCheckList.getChecklistId());
							chkList.setCustomerChecklist(cc);
							chkList.setCreatedBy(customerCheckList.getCheckList().getCreatedBy());
							chkList.setCreatedDate(customerCheckList.getCheckList().getCreatedDate());
							chkList.setUpdatedBy(context.getUserContext().getId());		
							chkList.setUpdatedDate( new Date(System.currentTimeMillis()));
							session.update(chkList);

						} else {
							// if level changed but status is not changed
						}

					}

					else {

						chkList.setAccountCheckList(null);
						chkList.setChecklistId(customerCheckList.getCheckList()
								.getChecklistId());

						SupportedLocales supportedLocales = new SupportedLocales();
						supportedLocales.setLocaleId(Short.valueOf("1"));
						chkList.setSupportedLocales(supportedLocales);

						CustomerCheckList cc = chkList.getCustomerChecklist();
						cc.setChecklistId(customerCheckList.getChecklistId());
						chkList.setCustomerChecklist(cc);

						Set<CheckListDetail> checkListDetailSet = chkList
								.getChecklistDetailSet();

						for (CheckListDetail checkListDetail : checkListDetailSet) {
							checkListDetail.setCheckList(chkList);
							checkListDetail.setAnswerType(Short.valueOf("1"));
						}
						Set<CheckListDetail> checkListDetailSetOld = customerCheckList
								.getCheckList().getChecklistDetailSet();

						for (CheckListDetail checkListDetailOld : checkListDetailSetOld) {

							session.delete(checkListDetailOld);
						}
						chkList.setCreatedBy(customerCheckList.getCheckList().getCreatedBy());
						chkList.setCreatedDate(customerCheckList.getCheckList().getCreatedDate());
						chkList.setUpdatedBy(context.getUserContext().getId());		
						chkList.setUpdatedDate( new Date(System.currentTimeMillis()));
						session.update(chkList);
					}

				}
			}

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * validation for State of Corresponding category
	 * or client type
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void validateState(Context context) throws SystemException,
			ApplicationException {
		Short level = (Short) context.getSearchResultBasedOnName("Level")
				.getValue();
		Short type = Short.valueOf(context.getSearchResultBasedOnName("Type")
				.getValue().toString());
		CheckList checkList = (CheckList) context.getValueObject();
		Session session = null;
		Integer count = null;
		if (type.shortValue() == 0) {
			
			Short statusId = checkList.getCustomerChecklist()
					.getCustomerState().getStatusId();
			String fromPage = context.getSearchResultBasedOnName("fromPage")
					.getValue().toString();
			Short previousStatusId = null;
			if (context.getSearchResultBasedOnName("previousStatusId")
					.getValue() != null) {
				previousStatusId = Short.valueOf(context
						.getSearchResultBasedOnName("previousStatusId")
						.getValue().toString());
			}
			
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside validateState method of DAO previousStatusId is "+previousStatusId);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside validateState method of DAO statusId is "+statusId);
			if (fromPage != null)
				if (fromPage.equalsIgnoreCase("manage")) {
					if (previousStatusId != null && statusId != null) {
						if (previousStatusId.shortValue() == statusId
								.shortValue()) {
						} else {
							try {
								session = HibernateUtil.getSession();
								Query query = session.getNamedQuery(CheckListConstants.CUSTOMER_VALIDATESTATE);								
								query.setShort("lvlId", level);
								query.setShort("stsId", statusId);
								count= (Integer)query.uniqueResult();						
							} catch (Exception e) {
								e.printStackTrace();
							}
							finally {
								HibernateUtil.closeSession(session);
							}
							if (count > 0) {
								throw new CheckListException(
										CheckListConstants.CATEGORYEXCEPTION);
							}
						}
					} else {
						try {
							session = HibernateUtil.getSession();
							Query query = session.getNamedQuery(CheckListConstants.CUSTOMER_VALIDATESTATE);								
							query.setShort("lvlId", level);
							query.setShort("stsId", statusId);
							count= (Integer)query.uniqueResult();	
						} catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							HibernateUtil.closeSession(session);
						}
						if (count > 0) {
							throw new CheckListException(
									CheckListConstants.CATEGORYEXCEPTION);
						}
					}

				} else {
					try {
						session = HibernateUtil.getSession();
						Query query = session.getNamedQuery(CheckListConstants.CUSTOMER_VALIDATESTATE);								
						query.setShort("lvlId", level);
						query.setShort("stsId", statusId);
						count= (Integer)query.uniqueResult();
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						HibernateUtil.closeSession(session);
					}
					if (count > 0) {
						throw new CheckListException(
								CheckListConstants.CATEGORYEXCEPTION);
					}
				}
		} else if (type.shortValue() == 1) {
		
			Short acStatusId = checkList.getAccountCheckList()
					.getAccountStatusId();
			String fromPage = context.getSearchResultBasedOnName("fromPage")
					.getValue().toString();
			Short previousStatusId = null;
			if (context.getSearchResultBasedOnName("previousStatusId")
					.getValue() != null) {
				previousStatusId = Short.valueOf(context
						.getSearchResultBasedOnName("previousStatusId")
						.getValue().toString());
			}
			if (fromPage != null)
				if (fromPage.equalsIgnoreCase("manage")) {
					if (previousStatusId != null && acStatusId != null) {
						if (previousStatusId.shortValue() == acStatusId
								.shortValue()) {
						} else {
							try {
								session = HibernateUtil.getSession();								
								Query query = session.getNamedQuery(CheckListConstants.PRODUCT_VALIDATESTATE);								
								query.setShort("lvlId", level);
								query.setShort("stsId", acStatusId);
								count= (Integer)query.uniqueResult();								
							} catch (Exception e) {
								e.printStackTrace();
							}
							finally {
								HibernateUtil.closeSession(session);
							}
							if (count > 0) {
								throw new CheckListException(
										CheckListConstants.CATEGORYEXCEPTION);
							}
						}
					} else {
						try {
							session = HibernateUtil.getSession();
							Query query = session.getNamedQuery(CheckListConstants.PRODUCT_VALIDATESTATE);								
							query.setShort("lvlId", level);
							query.setShort("stsId", acStatusId);
							count= (Integer)query.uniqueResult();
						} catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							HibernateUtil.closeSession(session);
						}
						if (count > 0) {
							throw new CheckListException(
									CheckListConstants.CATEGORYEXCEPTION);
						}
					}

				} else {
					try {
						session = HibernateUtil.getSession();
						Query query = session.getNamedQuery(CheckListConstants.PRODUCT_VALIDATESTATE);								
						query.setShort("lvlId", level);
						query.setShort("stsId", acStatusId);
						count= (Integer)query.uniqueResult();
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						HibernateUtil.closeSession(session);
					}
					if (count > 0) {
						throw new ApplicationException(
								CheckListConstants.CATEGORYEXCEPTION);
					}
				}
		}
	}	
}
