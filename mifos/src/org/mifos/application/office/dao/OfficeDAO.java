/**

 * OfficeDAO.java    version: 1.0

 

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
package org.mifos.application.office.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.master.util.valueobjects.OfficeLevelChildren;
import org.mifos.application.master.util.valueobjects.OfficeLevelMaster;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeHelper;
import org.mifos.application.office.util.helpers.OfficeSubObject;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.BranchOffice;
import org.mifos.application.office.util.valueobjects.BranchParentOffice;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeHierarchy;
import org.mifos.application.office.util.valueobjects.OfficeHirerchyList;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.application.office.util.valueobjects.OfficeLevelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.MasterDataRetrieverException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class handles the databse interaction for the office module
 * 
 * @author rajenders
 * 
 */
public class OfficeDAO extends DAO {

	// get the logger for logging
	MifosLogger officeLogger = MifosLogManager
			.getLogger(LoggerConstants.OFFICELOGGER);

	/**
	 * This function is helper function which is used to get the no of childern
	 * a given office has
	 * 
	 * @param officeId
	 *            id of the office whose children we are intersted
	 * @return no of children this office has
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public int getParentChildern(Short officeId) throws ApplicationException,
			SystemException {
		Session session = null;
		Transaction transaction = null;
		Integer noOfChilderen = null;
		Query getChildren = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the named query and set the officeId

			officeLogger.info("Executing named query "
					+ NamedQueryConstants.GETNOOFCHILDREN
					+ " with parameter as officeId=" + officeId);

			getChildren = session.getNamedQuery(
					NamedQueryConstants.GETNOOFCHILDREN).setShort(
					OfficeConstants.OFFICEID, officeId);
			noOfChilderen = (Integer) getChildren.uniqueResult();
			officeLogger.info("No of childern for office with id=" + officeId
					+ " is =" + noOfChilderen);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return (null != noOfChilderen && noOfChilderen.intValue() > 0) ? noOfChilderen
				.intValue()
				: 0;

	}

	/**
	 * This function is called to create the given office set into the context
	 */
	@Override
	public void create(Context context) throws SystemException,
			ApplicationException {

		Session session = null;
		Transaction transaction = null;
		try {

			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Office office = (Office) context.getValueObject();
			// fetch the parent form the database to set the version
			Office parentOffice = office.getParentOffice();

			officeLogger.info("Parent officeId for this office is  before get "
					+ parentOffice.getOfficeId());
			Short personnelId = context.getUserContext().getId();
			if (null != personnelId) {
				office.setCreatedBy(personnelId);
			}

			// create the office hierarchy object
			// save the office object

			officeLogger.info("officeId is" + office.getOfficeId());
			// we are creating thye office so officId should be null
			office.setOfficeId(null);
			
			
			//bug 28984  check before creating whether hierarchy still exist or not
			if ( !isLevelConfigured(office.getLevel().getLevelId()))
			{
				
				throw new OfficeException(
						OfficeConstants.KEYLEVELNOTCONFIGURED); 
			}
			
			session.save(office);

			// save the office hierachy
			OfficeHierarchy h = new OfficeHierarchy();

			h.setStartDate(new java.sql.Date(new java.util.Date().getTime()));
			h.setOffice(office);
			h.setStatus(OfficeConstants.ACTIVE);
			h.setParentOffice(office.getParentOffice());
			session.save(h);
			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (StaleObjectStateException sse) {
			transaction.rollback();
			throw new ConcurrencyException(sse);
		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function will check whether g given office has active personnel
	 * associated with it or not
	 * 
	 * @param officeId
	 *            officeId of the office for which we want to check personnel
	 * @return true or false
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public boolean isPersonnelTransferedClosed(Short officeId)
			throws ApplicationException, SystemException {

		Session session = null;
		Transaction transaction = null;
		Integer noActivePersonnel = null;
		Query getActivePersonnel = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the named query and set the officeId

			officeLogger.info("Excuting the named query "
					+ NamedQueryConstants.GETACTIVEPERSONNEL
					+ " with parameter officeId=" + officeId + " and statusId="
					+ OfficeConstants.ACTIVE);
			getActivePersonnel = session.getNamedQuery(
					NamedQueryConstants.GETACTIVEPERSONNEL).setShort(
					OfficeConstants.OFFICEID, officeId).setShort(
					OfficeConstants.STATUSID, OfficeConstants.ACTIVE);
			noActivePersonnel = (Integer) getActivePersonnel.uniqueResult();
			officeLogger.info("No of active personnel for this office is "
					+ noActivePersonnel);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return (null != noActivePersonnel && noActivePersonnel.intValue() > 0) ? true
				: false;

	}

	/**
	 * This functions checks whether given office has active children or not
	 * 
	 * @param officeId
	 *            office id whose children we are intersted in
	 * @return ture or false
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public boolean isChildPresent(Short officeId) throws ApplicationException,
			SystemException {
		Session session = null;
		Transaction transaction = null;
		Integer noOfChilderen = null;
		Query getChildren = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			// get the named query and set the officeId
			officeLogger.info("Excuting the named query "
					+ NamedQueryConstants.GETNUMBEROFACTIVECHILDERN
					+ " with parameter officeId=" + officeId);

			getChildren = session.getNamedQuery(
					NamedQueryConstants.GETNUMBEROFACTIVECHILDERN).setShort(
					OfficeConstants.OFFICEID, officeId)

			.setShort(OfficeConstants.STATUSID, OfficeConstants.ACTIVE);
			noOfChilderen = (Integer) getChildren.uniqueResult();
			officeLogger.info("No of childern for this office is "
					+ noOfChilderen);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return (null != noOfChilderen && noOfChilderen.intValue() > 0) ? true
				: false;
	}

	/**
	 * This function updates the specified office record with new values .if the
	 * parent is changed then this function will update the office hierarchy for
	 * audit reason it willl make old record as not configured and will insert
	 * new office hierarchy according to new details
	 */

	@Override
	public void update(Context context) throws ApplicationException,
			SystemException {

		Session session = null;
		Transaction tx = null;
		Query oldHierarchy = null;
		try {
			Office office = (Office) context.getValueObject();
			Short officeId = office.getOfficeId();
			// when we were coming to this page we had saved OfficeSubObject
			// which
			// is made out of the
			// this office object initially

			OfficeSubObject oso = (OfficeSubObject) ((SearchResults) context
					.getSearchResultBasedOnName(OfficeConstants.OFFICESUBOBJECT))
					.getValue();

			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			Short personnelId = ((UserContext) context.getUserContext())
					.getId();

			// if the office level is head office then we want to set the parent
			// as null
			Office parent = null;
			officeLogger.info("parent office is " + office.getParentOffice());
			if (OfficeConstants.HEADOFFICE == office.getLevel().getLevelId()
					.shortValue()) {

				office.setParentOffice(null);
			} else {

				Office parentOffice = office.getParentOffice();

				Short parentOfficeId = parentOffice.getOfficeId();
				parent = getOffice(parentOfficeId);

				if (null == parent)
					throw new OfficeException(OfficeConstants.KEYUPDATEFAILED);

				office.setParentOffice(parent);

				officeLogger.info("parent office id is " + parentOfficeId);
				// 1) see if the parent has been updated the we have to update
				// previous hierarchy record and insert new hierarchy record
				
				Short oldParent = oso.getParentId();
				Office newParent = office.getParentOffice();
				if (null != oldParent&& null != newParent)
				{
					short oldParentId = oldParent.shortValue();
					short newParentId = newParent.getOfficeId().shortValue();
					if (oldParentId != newParentId) {

						// we need to update the search id if we have updated
						// the
						// parent
						// TODO big funtionality miss not implemented yet
						List updateList = updateSearchIds(session, office.getOfficeId(),office.getSearchId(),oldParentId,newParentId);
						updateParentForCurrentOffice(updateList,office);
						if (updateList.size()>0 )
						{
							OfficeHelper.saveInContext(OfficeConstants.SEARCHIDLIST,updateList,context);
							//we need to update the search id of this office object also
							for (Iterator iter = updateList.iterator(); iter.hasNext();) {
								OfficeSearch element = (OfficeSearch) iter.next();
								if ( element.getOfficeId().shortValue()==office.getOfficeId().shortValue())
								{
									office.setSearchId(element.getSearchId());
								}
								
							}

						}
						

						//if ( true)
						//throw new OfficeException();
						// get the previous hierarchy record for this office
						// make
						// that as inactive and insert new record
						oldHierarchy = session.getNamedQuery(
								NamedQueryConstants.GETOLDOFFICEHIERARCHY)
								.setShort(OfficeConstants.OFFICEID, officeId)
								.setShort(OfficeConstants.STATUSID,
										Short.valueOf(OfficeConstants.ACTIVE));
						officeLogger.info("Executing the named query "
								+ NamedQueryConstants.GETOLDOFFICEHIERARCHY
								+ " with parameter officeId=" + officeId
								+ " and statusId =" + OfficeConstants.ACTIVE);

						OfficeHierarchy oh = (OfficeHierarchy) oldHierarchy
								.uniqueResult();
						if (null == oh) {

							throw new OfficeException(
									OfficeConstants.KEYHIERARCHYUPDATIONFAILED);

						} else {

							oh.setStatus(OfficeConstants.INACTIVE);
							oh.setEndDate(new java.sql.Date(
									new java.util.Date().getTime()));
							oh.setUpdatedDate(new java.sql.Date(
									new java.util.Date().getTime()));
							oh.setUpdatedBy(personnelId);
							session.update(oh);

						}

						// insert new record
						OfficeHierarchy h = new OfficeHierarchy();
						h.setStartDate(new java.sql.Date(new java.util.Date()
								.getTime()));
						h.setOffice(office);
						h.setParentOffice(office.getParentOffice());
						h.setStatus(OfficeConstants.ACTIVE);
						session.save(h);
					}
				}
			}
			office.setUpdatedBy(personnelId);
			office.setUpdatedDate(new java.sql.Date(new java.util.Date()
					.getTime()));
			officeLogger.info("parent office is " + office.getParentOffice());

			session.update(office);
			tx.commit();

		} catch (HibernateProcessException e) {

			tx.rollback();
			throw new ApplicationException(e);

		} catch (StaleObjectStateException e) {
			tx.rollback();
			throw new ConcurrencyException(e);
		}

		finally {
			HibernateUtil.closeSession(session);
		}

	}

	private void updateParentForCurrentOffice(List<OfficeSearch> officeList, Office office){
		for(int i=0; i<officeList.size();i++){
			OfficeSearch element = officeList.get(i);
			if(element.getOfficeId().equals(office.getOfficeId())){
				element.setParentOfficeId(office.getParentOffice().getOfficeId());
				break;
			}
		}
	}
	
	private List<OfficeSearch> updateSearchIds(Session session, Short officeId,String searchId,Short oldParent,Short newParent)
	
	throws ApplicationException{
		
		List<OfficeSearch> officeSearchIdList = null;
		List<OfficeSearch> backUp = null;
		Query queryOfficeSearchList = null;
	    List <OfficeSearch> changedList = new ArrayList <OfficeSearch>();
		//get the list of all the offices
		queryOfficeSearchList = session
		.getNamedQuery(NamedQueryConstants.GETOFFICESEARCHLIST);
		officeSearchIdList = queryOfficeSearchList.list();
		backUp = new ArrayList();
		for (Iterator iter = officeSearchIdList.iterator(); iter.hasNext();) {
			OfficeSearch element = (OfficeSearch) iter.next();
			backUp.add(new OfficeSearch(element.getOfficeId(),element.getSearchId()));
		}
		
		OfficeHelper.updateSearchIds(officeId,searchId,oldParent,newParent,officeSearchIdList);
		
		for (int i = 0; i < officeSearchIdList.size(); i++) {
			OfficeSearch newElement = officeSearchIdList.get(i);
			OfficeSearch oldElement = backUp.get(i);
			if (!newElement.getSearchId().equals(oldElement.getSearchId()))
			{
				
				session.update(newElement);
				changedList.add(newElement);
				
			}

		}
		return changedList;

	}

	/**
	 * This function will load all the office master data to the context like
	 * officecodes,office types,officeparents and office status
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadOfficeMaster(Context context, boolean active)
			throws SystemException, ApplicationException {

		// Loading the master data

		officeLogger.info("Loading the master data...");
		context.addAttribute(getOfficeCodeMaster());
		context.addAttribute(getOfficeLevelMaster());
		context.addAttribute(getCustomFieldDefnMaster());

		if (active)
			context.addAttribute(getParentMasterActive());
		else
			context.addAttribute(getParentMaster());

		context.addAttribute(getOfficeStatusMaster());

	}

	/**
	 * This function gets the office record from the database by specified
	 * officeid
	 * 
	 * @param officeId
	 *            officeId for which we want to fetch the record
	 * @return office object
	 */
	public Office getOffice(Short officeId) throws SystemException,
			ApplicationException {

		officeLogger.info("Getting the  office with the id = " + officeId);

		Session session = null;
		Office office = null;
		try {

			session = HibernateUtil.getSession();

			office = (Office) session.get(Office.class, officeId);
			if (null != office) {
				office.getLevel().getConfigured();
				if (OfficeConstants.HEADOFFICE != office.getLevel()
						.getLevelId().shortValue()) {
					// expliclitly load some lazy data
					office.getParentOffice().getOfficeId();
					office.getParentOffice().getVersionNo();
				}
				office.getOfficeCode();
				office.getLevel();
				office.getStatus();
				office.getAddress();
				office.getVersionNo();
			}

			officeLogger.info("Got the  office sucessfully with the id = "
					+ officeId);

		} catch (HibernateProcessException e) {
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return office;

	}

	/**
	 * This function get the customfields master data data
	 * 
	 * @return SearchResults object
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	private SearchResults getCustomFieldDefnMaster() throws SystemException {
		 Session session = null;
		  List queryResult = null;
		   try{
			  session = HibernateUtil.getSession();
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("entityType",OfficeConstants.OFFICE_CUSTOM_FIELD_ENTITY_TYPE);
			  queryResult = executeNamedQuery(NamedQueryConstants.MASTERDATA_CUSTOMFIELDDEFINITION,queryParameters,session);
			  if(null!=queryResult && queryResult.size()>0){
				  for(int i=0;i<queryResult.size();i++){
					 ((CustomFieldDefinition)queryResult.get(i)).getLookUpEntity().getEntityType();
				  }
			  }
				officeLogger
				.info("Calling the masterDataRetriever.retrieve() with parameters namedquery ="
						+ NamedQueryConstants.MASTERDATA_CUSTOMFIELDDEFINITION
						+ " and entityType = " + OfficeConstants.OFFICE_CUSTOM_FIELD_ENTITY_TYPE);

		   }catch(HibernateException he){
				throw new HibernateSystemException(he);
			}finally{
				HibernateUtil.closeSession(session);
			}
			SearchResults searchResults = new SearchResults();
	 		searchResults.setResultName(OfficeConstants.CUSTOM_FIELDS);
	 		searchResults.setValue(queryResult);
	 		return searchResults;
	}

	/**
	 * This function is used to retrive the master data for showing the type of
	 * offices we have configured in the system
	 * 
	 * @return SearchResults object with loaded master data
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	public SearchResults getOfficeLevelMaster() throws SystemException,
			MasterDataRetrieverException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);

		}
		masterDataRetriever.prepare(NamedQueryConstants.OFFICELEVELMASTER,
				OfficeConstants.OFFICELEVELLIST);
		masterDataRetriever.setParameter(OfficeConstants.LOCALEID,
				OfficeConstants.LOCALEENGLISH);
		officeLogger
				.info("Calling the masterDataRetriever.retrieve() with parameters namedquery ="
						+ NamedQueryConstants.OFFICELEVELMASTER
						+ " and localeId  = " + OfficeConstants.LOCALEENGLISH);

		return masterDataRetriever.retrieve();
	}

	/**
	 * This function is used to retrive the master data for showing the type of
	 * offices codes we have configured in the system
	 * 
	 * @return SearchResults object with loaded master data
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	private SearchResults getOfficeCodeMaster() throws SystemException,
			MasterDataRetrieverException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);

		}
		masterDataRetriever.prepare(NamedQueryConstants.OFFICECODEMASTER,
				OfficeConstants.OFFICECODELIST);
		masterDataRetriever.setParameter(OfficeConstants.LOCALEID,
				OfficeConstants.LOCALEENGLISH);
		officeLogger
				.info("Calling the masterDataRetriever.retrieve() with parameters namedquery ="
						+ NamedQueryConstants.OFFICECODEMASTER
						+ " and localeId  = " + OfficeConstants.LOCALEENGLISH);

		return masterDataRetriever.retrieve();
	}

	/**
	 * This function is used to retrive the master data for showing the type of
	 * offices status we have configured in the system
	 * 
	 * @return SearchResults object with loaded master data
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	public SearchResults getOfficeStatusMaster() throws SystemException,
			MasterDataRetrieverException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);

		}
		masterDataRetriever.prepare(NamedQueryConstants.OFFICESTATUSMASTER,
				OfficeConstants.OFFICESTATUSLIST);
		masterDataRetriever.setParameter(OfficeConstants.LOCALEID,
				OfficeConstants.LOCALEENGLISH);
		officeLogger
				.info("Calling the masterDataRetriever.retrieve() with parameters namedquery ="
						+ NamedQueryConstants.OFFICESTATUSMASTER
						+ " and localeId  = " + OfficeConstants.LOCALEENGLISH);

		return masterDataRetriever.retrieve();
	}

	/**
	 * This function is used to retrive the master data for showing the office
	 * parents
	 * 
	 * @return SearchResults object with loaded master data
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	private SearchResults getParentMaster() throws SystemException {

		Session session = null;
		Query queryOfficeLevel = null;
		OfficeLevelView levelView = new OfficeLevelView();
		try {
			session = HibernateUtil.getSession();
			queryOfficeLevel = session
					.getNamedQuery(NamedQueryConstants.OFFICELEVELMASTER);
			queryOfficeLevel.setShort(OfficeConstants.LOCALEID,
					OfficeConstants.LOCALEENGLISH);
			List<OfficeLevelMaster> levelList = queryOfficeLevel.list();

			for (int i = 0; i < levelList.size(); i++) {
				OfficeLevelMaster levelMaster = levelList.get(i);
				Query queryParentOffice = session
						.getNamedQuery(NamedQueryConstants.OFFICEPARENTMASTER);
				queryParentOffice.setShort(OfficeConstants.LEVELID, levelMaster
						.getLevelId());
				queryParentOffice.setShort(OfficeConstants.LOCALEID,
						OfficeConstants.LOCALEENGLISH);

				levelView.setLevelInfo(levelMaster.getLevelId(),
						queryParentOffice.list());

			}
		} catch (HibernateProcessException e) {

			throw new SystemException();
		} finally {
			HibernateUtil.closeSession(session);
		}

		return OfficeHelper.getSearchResutls(OfficeConstants.PARENTOFFICESMAP,
				levelView);

	}

	/*
	 * This function is called to load the active parent list for the given office
	 */
	private SearchResults getParentMasterActive() throws SystemException {

		Session session = null;
		Query queryOfficeLevel = null;
		OfficeLevelView levelView = new OfficeLevelView();
		try {
			session = HibernateUtil.getSession();
			queryOfficeLevel = session
					.getNamedQuery(NamedQueryConstants.OFFICELEVELMASTER);
			queryOfficeLevel.setShort(OfficeConstants.LOCALEID,
					OfficeConstants.LOCALEENGLISH);
			List<OfficeLevelMaster> levelList = queryOfficeLevel.list();

			for (int i = 0; i < levelList.size(); i++) {
				OfficeLevelMaster levelMaster = levelList.get(i);
				Query queryParentOffice = session
						.getNamedQuery(NamedQueryConstants.GETPARENTOFFICEACTIVE);
				queryParentOffice.setShort(OfficeConstants.LEVELID, levelMaster
						.getLevelId());
				queryParentOffice.setShort(OfficeConstants.LOCALEID,
						OfficeConstants.LOCALEENGLISH);
				queryParentOffice.setShort(OfficeConstants.STATUSID,
						OfficeConstants.ACTIVE);

				levelView.setLevelInfo(levelMaster.getLevelId(),
						queryParentOffice.list());

			}
		} catch (HibernateProcessException e) {

			throw new SystemException();
		} finally {
			HibernateUtil.closeSession(session);
		}

		return OfficeHelper.getSearchResutls(OfficeConstants.PARENTOFFICESMAP,
				levelView);

	}

	/**
	 * This function will returns all the level in the system
	 * 
	 * @return list of all the levels in the system
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List getOfficeLevels() throws SystemException, ApplicationException {
		Session session = null;
		Transaction transaction = null;
		List officeLevels = new ArrayList();
		Query getLevels = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get list of all the levels in the system
			getLevels = session.getNamedQuery(NamedQueryConstants.GETALLLEVEL);
			officeLevels = getLevels.list();

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return officeLevels;
	}

	/**
	 * This function will return whether we have any active office with given
	 * level id
	 * 
	 * @param levelId
	 *            levelid for which we want to find the active offices
	 * @return true or false
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean isOfficesActive(Short levelId) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		Integer offices = null;
		Query getOffices = null;

		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// check whether we have any record in the database with the given
			// levelid
			getOffices = session.getNamedQuery(
					NamedQueryConstants.GETACTIVEOFFICEWITHLEVEL).setShort(
					OfficeConstants.LEVELID, levelId);
			offices = (Integer) getOffices.uniqueResult();
			transaction.commit();
			officeLogger.info("No of office with levelId =" + levelId + " are "
					+ offices);

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return (null != offices && offices > 0) ? true : false;

	}

	/**
	 * This function updates the office Hierarchy configuration
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updatehierarchy(Context context) throws SystemException,
			ApplicationException {
		// you can safely update the configuration now

		// get the
		OfficeHirerchyList ohList = (OfficeHirerchyList) context
				.getValueObject();
		List selectedList = ohList.getLevelList();
		// if any office level is being changed
		List officeLevels = (List) context.getSearchResultBasedOnName(
				OfficeConstants.OLDHIERARCHYLIST).getValue();
		Session session = null;
		officeLogger.info("officeLevels list is " + selectedList);
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			for (int i = 0; i < selectedList.size(); i++) {

				OfficeLevel currentLevel = (OfficeLevel) selectedList.get(i);
				for (int j = 0; j < officeLevels.size(); j++) {

					OfficeLevel oldLevel = (OfficeLevel) officeLevels.get(j);

					if (currentLevel.getLevelId().shortValue() == oldLevel
							.getLevelId().shortValue()) {
						// see if user has altered the state

						if (currentLevel.getConfigured().shortValue() != oldLevel
								.getConfigured().shortValue()) {

							// we have to update these only
							oldLevel
									.setConfigured(currentLevel.getConfigured());
							officeLogger
									.info("current cofiguration id "
											+ currentLevel.getConfigured()
													.shortValue());
							// finally update it
							OfficeLevel level = (OfficeLevel) session.get(
									OfficeLevel.class, currentLevel
											.getLevelId());
							level.setConfigured(currentLevel.getConfigured());
							officeLogger.info("current officeLevel is "
									+ currentLevel.getLevelId());

							session.update(level);

						}

					}
				}

			}
			transaction.commit();

			officeLogger.info("after commit");
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function will return the master offices data till the branch level
	 * 
	 * @return list of OfficeLevelMaster objects
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<OfficeLevelMaster> getTillBranchOffice()
			throws SystemException, ApplicationException {

		Session session = null;
		Transaction transaction = null;
		List<OfficeLevelMaster> applicableLevel = new ArrayList<OfficeLevelMaster>();
		try {

			session = HibernateUtil.getSession();
			// transaction = session.beginTransaction();
			Query queryOfficeLevel = session
					.getNamedQuery(NamedQueryConstants.GETHEADOFFICE);

			List<Short> applicableLevelId = new ArrayList<Short>();

			OfficeLevel l = (OfficeLevel) queryOfficeLevel.uniqueResult();
			if (l.getParent() == null)
				applicableLevelId.add(l.getLevelId());
			while (l.getChild() != null) {

				if (l.getChild().getConfigured() == 1) {
					applicableLevelId.add(l.getChild().getLevelId());
					officeLogger.debug("level id " + l.getChild().getLevelId());
				}

				l = l.getChild();

			}

			for (int i = 0; i < applicableLevelId.size(); i++) {
				Short levelId = applicableLevelId.get(i);
				queryOfficeLevel = session
						.getNamedQuery(NamedQueryConstants.GETLEVEL);
				queryOfficeLevel.setShort(OfficeConstants.LOCALEID,
						OfficeConstants.LOCALEENGLISH);
				queryOfficeLevel.setShort(OfficeConstants.LEVELID, levelId);
				OfficeLevelMaster levelMaster = (OfficeLevelMaster) queryOfficeLevel
						.uniqueResult();

				Query queryChildren = session
						.getNamedQuery(NamedQueryConstants.GETLEVELCHILDREN);
				queryChildren.setShort(OfficeConstants.LEVELID, levelId);
				List<OfficeLevelChildren> listChildren = queryChildren.list();
				levelMaster.setOfficeLevelChildren(listChildren);

				officeLogger.debug(" applicable level id "
						+ levelMaster.getLevelId());
				officeLogger.debug("applicable level name is  "
						+ levelMaster.getLevelName());

				for (int r = 0; r < listChildren.size(); r++) {
					OfficeLevelChildren children = listChildren.get(r);

					officeLogger.debug("  child id " + children.getOfficeId());
					officeLogger.debug(" child name is "
							+ children.getOfficeName());

				}

				applicableLevel.add(levelMaster);

			}
		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {

			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return applicableLevel;
	}

	/**
	 * This function returns the list of BranchParentOffice object each loaded
	 * with the childern it has
	 * 
	 * @return BranchParentOffice object
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public List<BranchParentOffice> getBranchOffice() throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		List<BranchParentOffice> branchList = null;

		try {
			session = HibernateUtil.getSession();

			Query queryParentOffice = session
					.getNamedQuery(NamedQueryConstants.MASTERDATA_BRANCH_PARENTS);
			Query queryBranchOffice = session
					.getNamedQuery(NamedQueryConstants.MASTERDATA_BRANCH_OFFICES);
			branchList = new ArrayList<BranchParentOffice>();
			List<BranchParentOffice> branchParents = queryParentOffice.list();
			List<Short> addedParent = new ArrayList();

			BranchParentOffice parent = null;
			if (null != branchParents) {
				List<BranchOffice> branchOffices = queryBranchOffice.list();
				Iterator<BranchParentOffice> parentIterator = branchParents
						.iterator();
				while (parentIterator.hasNext()) {

					parent = parentIterator.next();

					if (!addedParent.contains(parent.getOfficeId())) {

						parent.setBranchOffice(getBranchOfficesForParent(
								branchOffices, parent.getOfficeId()));
						branchList.add(parent);
						addedParent.add(parent.getOfficeId());
					}
				}
			}

		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {

			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return branchList;

	}

	/**
	 * This function will return whether we can change the office type or not
	 * 
	 * @param levelId
	 *            levelid of the office which we are changing
	 * @param officeId
	 *            offcieId of the office we are changing
	 * @return whether we can change this office level or not
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean canChangeOfficeType(Short levelId, Short officeId,
			Short oldLevelId) throws SystemException, ApplicationException {

		// if we are moving up no problem

		if (oldLevelId.shortValue() > levelId.shortValue()) {
			return true;
		}

		Session session = null;
		try {

			session = HibernateUtil.getSession();
			Query queryOfficeLevel = session
					.getNamedQuery(NamedQueryConstants.GETHEADOFFICE);

			List<Short> applicableLevelId = new ArrayList<Short>();
			boolean isChild = false;

			OfficeLevel l = (OfficeLevel) queryOfficeLevel.uniqueResult();
			List<Short> childId = new ArrayList<Short>();

			l = l.getChild();
			while (l.getChild() != null) {
				officeLogger.debug("for level id.." + l.getLevelId());
				officeLogger.debug("officeId of the " + officeId);
				queryOfficeLevel = session
						.getNamedQuery(NamedQueryConstants.ISINVALIDVALIDOFFICEPRESENT);
				queryOfficeLevel.setShort("inValidLevelId", l.getLevelId());

				queryOfficeLevel.setShort("parentOfficeId", officeId);

				queryOfficeLevel.setShort(OfficeConstants.STATUSID,
						OfficeConstants.ACTIVE);

				Integer isValid = (Integer) queryOfficeLevel.uniqueResult();
				officeLogger.debug("count.." + isValid);
				if (isValid.intValue() > 0) {

					return false;

				}

				if (l.getChild().getConfigured() == 1) {
					if (l.getLevelId().shortValue() == levelId.shortValue()) {
						return true;
					}

				}

				l = l.getChild();

			}
		} catch (HibernateProcessException e) {
			throw new SystemException(e);

		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return true;

	}

	/**
	 * This function is helper function which will returns the childern for the
	 * current parent office
	 * 
	 * @param branchOffices
	 *            list of branchOffices
	 * 
	 * @param parentId
	 *            parentId
	 * @return
	 */
	private List<BranchOffice> getBranchOfficesForParent(
			List<BranchOffice> branchOffices, short parentId) {
		List<BranchOffice> branchOfficeList = null;
		BranchOffice bo = null;
		if (null != branchOffices) {
			branchOfficeList = new ArrayList<BranchOffice>();
			Iterator<BranchOffice> it = branchOffices.iterator();
			while (it.hasNext()) {
				bo = it.next();
				officeLogger.debug("in while  " + bo);
				if (bo.getParentOfficeId().shortValue() == parentId) {
					officeLogger.debug("child office id.." + bo.getOfficeId());
					officeLogger.debug("child office name.."
							+ bo.getOfficeName());
					branchOfficeList.add(bo);
				}
			}
		}
		officeLogger.debug("got the branch office for parent");
		return branchOfficeList;
	}

	/**
	 * This function checks for the office name uniqueness
	 * 
	 * @param context
	 *            context object
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean checkOfficeNameUniqueNess(String officeName)
			throws SystemException, ApplicationException {
		// Office office = (Office) context.getValueObject();
		Session session = null;
		Transaction transaction = null;
		Integer count = null;
		Query checkUniqueNess = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the name query
			checkUniqueNess = session.getNamedQuery(
					NamedQueryConstants.CHECKNAMEUNIQUENESS).setString(
					OfficeConstants.DISPLAYNAME, officeName);
			count = (Integer) checkUniqueNess.uniqueResult();

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return (null != count && count > 0) ? true : false;

	}

	/**
	 * This function returns the office short name uniqueness
	 * 
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean checkOfficeShortNameUniqueNess(String shortName)
			throws SystemException, ApplicationException {
		// Office office = (Office) context.getValueObject();
		Session session = null;
		Transaction transaction = null;
		Integer count = null;
		Query checkUniqueNess = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the name query
			checkUniqueNess = session.getNamedQuery(
					NamedQueryConstants.CHECKSHORTNAMEUNIQUENESS).setString(
					OfficeConstants.SHORTNAME, shortName);
			count = (Integer) checkUniqueNess.uniqueResult();
			officeLogger.info("No of offices with " + shortName
					+ " short name are " + count);

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return (null != count && count > 0) ? true : false;

	}

	/**
	 * This fuction retrive the few fieds of the office object based on the passed 
	 * officeid
	 * @param id officeid
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public OfficeSubObject getOfficeSubObject(Short id) throws SystemException,
			ApplicationException {
		Session session = null;
		OfficeSubObject oso = null;
		try {
			session = HibernateUtil.getSession();
			oso = (OfficeSubObject) session.getNamedQuery(
					NamedQueryConstants.GETOFFICESUBOBJECT).setShort(
					OfficeConstants.OFFICEID, id).uniqueResult();

		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {

			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return oso;
	}

	/**
	 * This function returns all the office type present in the system
	 * @return
	 * @throws SystemException
	 * @throws MasterDataRetrieverException
	 */
	public SearchResults getAllOfficeLevel() throws SystemException,
			MasterDataRetrieverException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);

		}
		masterDataRetriever.prepare(NamedQueryConstants.GETALLOFFICELEVELS,
				OfficeConstants.OFFICELEVELLIST);
		masterDataRetriever.setParameter(OfficeConstants.LOCALEID,
				OfficeConstants.LOCALEENGLISH);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This function is called to check that whether a perticular office type is configured in the 
	 * system or not
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean isLevelConfigured(Short LevelId) throws SystemException,
			ApplicationException {
		Session session = null;
		Transaction transaction = null;
		Integer count = null;
		Query checkUniqueNess = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the name query

					checkUniqueNess = session.getNamedQuery(
							NamedQueryConstants.ISLEVELCONFIGURED).setShort(
							OfficeConstants.LEVELID,LevelId);
					count = (Integer) checkUniqueNess.uniqueResult();
					transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return (null != count && count > 0) ? true : false;

	}

	/**
	 * This function is called to check whether a given office is active or not
	 * @param officeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean isOfficeActive(Short officeId) throws SystemException,
			ApplicationException {

		Session session = null;
		Transaction transaction = null;
		Integer count = null;
		Query officeActive = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			officeActive = session.getNamedQuery(
					NamedQueryConstants.ISOFFICEACTIVE).setShort(
					OfficeConstants.OFFICEID, officeId).setShort(
					OfficeConstants.STATUSID, OfficeConstants.ACTIVE);
			count = (Integer) officeActive.uniqueResult();

			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return (null != count && count > 0) ? true : false;

	}

	/**
	 * This function will return the master offices data till the branch level
	 * 
	 * @return list of OfficeLevelMaster objects
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<OfficeLevelMaster> getTillBranchOfficeActive(String userOfficeSearchId,Short loggedInUserOfficeLevel)
			throws SystemException, ApplicationException {
		Session session = null;
		userOfficeSearchId=userOfficeSearchId+"%";
		List<OfficeLevelMaster> applicableLevel = new ArrayList<OfficeLevelMaster>();
		try {
			session = HibernateUtil.getSession();
			Query queryOfficeLevel = session
					.getNamedQuery(NamedQueryConstants.GETHEADOFFICE);
			List<Short> applicableLevelId = new ArrayList<Short>();
			OfficeLevel l = (OfficeLevel) queryOfficeLevel.uniqueResult();
			if (l.getParent() == null)
				applicableLevelId.add(l.getLevelId());
			while (l.getChild() != null) {

				if (l.getChild().getConfigured() == 1) {
					applicableLevelId.add(l.getChild().getLevelId());
					officeLogger.info(" level id " + l.getChild().getLevelId());
				}

				l = l.getChild();

			}
			List<Short> newApplicableLevelId = new ArrayList<Short>();
			for (int i = 0; i < applicableLevelId.size(); i++) {
				if(applicableLevelId.get(i).shortValue()==loggedInUserOfficeLevel.shortValue()){
					for(;i<applicableLevelId.size();i++)
						newApplicableLevelId.add(applicableLevelId.get(i));
					break;
				}
			}
			for (int i = 0; i < newApplicableLevelId.size(); i++) {
				Short levelId = newApplicableLevelId.get(i);
				queryOfficeLevel = session
						.getNamedQuery(NamedQueryConstants.GETLEVEL);
				queryOfficeLevel.setShort(OfficeConstants.LOCALEID,
						OfficeConstants.LOCALEENGLISH);
				queryOfficeLevel.setShort(OfficeConstants.LEVELID, levelId);
				OfficeLevelMaster levelMaster = (OfficeLevelMaster) queryOfficeLevel
						.uniqueResult();

				Query queryChildren = session
						.getNamedQuery(NamedQueryConstants.GETLEVELCHILDERNACTIVE);
				queryChildren.setShort(OfficeConstants.LEVELID, levelId);
				queryChildren.setShort(OfficeConstants.STATUSID,
						OfficeConstants.ACTIVE);
				officeLogger.debug("------------ user office search id: "+ userOfficeSearchId);
				queryChildren.setString("searchId",userOfficeSearchId);

				List<OfficeLevelChildren> listChildren = queryChildren.list();
				levelMaster.setOfficeLevelChildren(listChildren);

				officeLogger.debug("applicable level id "
						+ levelMaster.getLevelId());
				officeLogger.debug("applicable level name is  "
						+ levelMaster.getLevelName());

				for (int r = 0; r < listChildren.size(); r++) {
					OfficeLevelChildren children = listChildren.get(r);
					officeLogger.debug("child id " + children.getOfficeId());
					officeLogger.debug("child name is "
							+ children.getOfficeName());
				}

				applicableLevel.add(levelMaster);

			}

			// transaction.commit();

		} catch (HibernateProcessException e) {
			// transaction.rollback();
			throw new SystemException(e);

		} catch (HibernateException he) {
			// transaction.rollback();
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return applicableLevel;
	}

	/**
	 * This function returns the list of all the office active till the branch office
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<BranchParentOffice> getBranchOfficeActive(String userOfficeSearchId)
			throws SystemException, ApplicationException {
		Session session = null;

		List<BranchParentOffice> branchList = null;
		String userOffSearchId=userOfficeSearchId+".%";
		try {
			session = HibernateUtil.getSession();

			Query queryParentOffice = session
					.getNamedQuery(NamedQueryConstants.MASTERDATA_BRANCH_PARENTS_ACTIVE);
			queryParentOffice.setShort(OfficeConstants.STATUSID,
					OfficeConstants.ACTIVE).setString("sid",userOffSearchId).setString("searchId",userOfficeSearchId);
			

			Query queryBranchOffice = session
					.getNamedQuery(NamedQueryConstants.MASTERDATA_BRANCH_OFFICES_ACTIVE);

			queryBranchOffice.setShort(OfficeConstants.STATUSID,
					OfficeConstants.ACTIVE).setString("sid",userOffSearchId).setString("searchId",userOfficeSearchId);

			branchList = new ArrayList<BranchParentOffice>();
			List<BranchParentOffice> branchParents = queryParentOffice.list();
			List<Short> addedParent = new ArrayList();

			BranchParentOffice parent = null;
			if (null != branchParents) {
				List<BranchOffice> branchOffices = queryBranchOffice.list();
				Iterator<BranchParentOffice> parentIterator = branchParents
						.iterator();
				while (parentIterator.hasNext()) {

					parent = parentIterator.next();

					if (!addedParent.contains(parent.getOfficeId())) {

						parent.setBranchOffice(getBranchOfficesForParent(
								branchOffices, parent.getOfficeId()));
						branchList.add(parent);
						addedParent.add(parent.getOfficeId());
					}
				}
			}

		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {

			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return branchList;

	}

	// #26695
	/**
	 * This function return the max offcice Id from the database
	 */
	public Short getMaxOfficeId() throws SystemException, ApplicationException {
		Session session = null;

		Short count = null;

		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETMAXID);
			count = (Short) query.uniqueResult();

		} catch (HibernateProcessException e) {

			throw new SystemException(e);

		} catch (HibernateException he) {

			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return count;

	}

}
