/**

 * OfficeBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.office.business.handlers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.exceptions.DuplicateOfficeException;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeHelper;
import org.mifos.application.office.util.helpers.OfficeIDGenerator;
import org.mifos.application.office.util.helpers.OfficeSubObject;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.application.office.util.valueobjects.OfficeLevelView;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.EventManger;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class holds all the business logic related to the office module .All
 * validations are done in the functions starts with initial
 * 
 * @author rajenders
 * 
 */
public class OfficeBusinessProcessor extends MifosBusinessProcessor {

	// get the logger for logging
	MifosLogger officeLogger = MifosLogManager
			.getLogger(LoggerConstants.OFFICELOGGER);

	/**
	 * This function is called before the create fuction and it performs the
	 * validation for the uniqueness of the officeName and officeShortName
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context) throws SystemException,
			ApplicationException {

		try {
			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());
			Office office = (Office) context.getValueObject();
			UserContext uc = context.getUserContext();
			Short userId = uc.getId();

			// check whether name is unique or not
			if (officedao.checkOfficeNameUniqueNess(office.getOfficeName())) {

				officeLogger
						.error(office.getOfficeName()
								+ " is not unique office name while office is created by user with userid ="
								+ userId);
				throw new DuplicateOfficeException(OfficeConstants.KEYNAMEEXIST);
			}

			// check whether officeShort name is unique or not
			if (officedao.checkOfficeShortNameUniqueNess(office.getShortName())) {
				officeLogger
						.error(office.getShortName()
								+ " is not unique office short name while office is created by user with userid ="
								+ userId);

				throw new DuplicateOfficeException(
						OfficeConstants.KEYSHORTNAMEEXIST);
			}

			officeLogger
					.info("Sucessfully verified the uniqueness of office name and short name ");
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new ApplicationException(OfficeConstants.KEYCREATEFAILED);
		}
	}

	/**
	 * This function is called to create new office record
	 */
	@Override
	public void create(Context context) throws SystemException,
			ApplicationException {

		try {
			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

			Office office = (Office) context.getValueObject();
			Office parent = office.getParentOffice();
			UserContext uc = context.getUserContext();
			Short userId = uc.getId();

			officeLogger.info("Parent office id is  =" + parent.getOfficeId()
					+ " of the created office and created by user with id="
					+ userId);

			// generate and set the searchid
			String searchId = OfficeHelper.generateSerachId(context);
			officeLogger.info("Generated searchId  is =" + searchId
					+ " and operation is performed by user with id =" + userId);

			office.setSearchId(searchId);
			// bug 26695
			String officeGlobelNo = OfficeIDGenerator
					.generateOfficeId(officedao.getMaxOfficeId());
			officeLogger.info("Generated  office globel no is ="
					+ officeGlobelNo
					+ " and operation is performed by user with id =" + userId);

			office.setGlobalOfficeNum(officeGlobelNo);

			office.setCreatedDate(new java.sql.Date(new java.util.Date()
					.getTime()));

			office.setMaxChildCount(Integer.valueOf(OfficeConstants.ZERO));
			office
					.setOperationMode(Short
							.valueOf(OfficeConstants.REMOTESERVER));
			context.setValueObject(office);
			super.create(context);
			// if we are here it means office created sucessfully
			// we need to update hierarchy manager cache
			OfficeSearch os = new OfficeSearch(office.getOfficeId(), office.getSearchId(),office.getParentOffice().getOfficeId());
			List<OfficeSearch> osList = new ArrayList<OfficeSearch>();
			osList.add(os);
			EventManger.postEvent(Constants.CREATE, osList,
					SecurityConstants.OFFICECHANGEEVENT);

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYCREATEFAILED);
		}
	}

	/**
	 * This function load the specified office information from the databse
	 */
	@Override
	public void get(Context context) throws SystemException,
			ApplicationException {

		try {
			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());
			Office office = (Office) context.getValueObject();
			UserContext uc = context.getUserContext();
			Short userId = uc.getId();

			// get the office selected by the user

			officeLogger.info("Fetching the office with the id ="
					+ office.getOfficeId()
					+ " and operation is performed by user with id =" + userId);
			office = officedao.getOffice(office.getOfficeId());

			// set the newley got value object in context
			context.setValueObject(office);

			officeLogger.info("Loading the master data...");

			// load all the master data to the context
			officedao.loadOfficeMaster(context, false);

			// get the current office level
			Short officeLevel = office.getLevel().getLevelId();

			// get the parent map which was loaded during the loadOfficeMaster
			// call
			OfficeLevelView olv = (OfficeLevelView) context
					.getSearchResultBasedOnName(
							OfficeConstants.PARENTOFFICESMAP).getValue();

			officeLogger
					.info("Loading the parent offices for office level with id="
							+ officeLevel
							+ " and operation is performed by user with id ="
							+ userId);
			// now base on the parentid papulate the parent list
			List parentList = (List) olv.getParentOffices(officeLevel);
			// save this list in context
			OfficeHelper.saveInContext(OfficeConstants.PARENTS, parentList,
					context);

			// Create office subobject with fewer fields
			// if parent office is head office than the parent office will be
			// null
			OfficeSubObject oso = null;
			if (OfficeConstants.HEADOFFICE == office.getOfficeId().shortValue()) {
				oso = new OfficeSubObject(office.getOfficeName(), office
						.getShortName(), office.getStatus().getStatusId(),
						null, office.getLevel().getLevelId());
			} else {
				oso = new OfficeSubObject(office.getOfficeName(), office
						.getShortName(), office.getStatus().getStatusId(),
						office.getParentOffice().getOfficeId(), office
								.getLevel().getLevelId());

			}

			// save this also in the context later this can be used while
			// updating this record
			OfficeHelper.saveInContext(OfficeConstants.OFFICESUBOBJECT, oso,
					context);

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYGETFAILED);
		}

	}

	/**
	 * This function is called to load the master data to the context when we
	 * click the create new office on the admin page
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());
			UserContext uc = context.getUserContext();
			Short userId = uc.getId();

			officeLogger.info("Loading the master data...");

			// load the master data to context
			officedao.loadOfficeMaster(context, true);
			// remove the head office from the list
			OfficeHelper.UpdateOfficeLevelList(context);

			// set the office and the parent office based on the selected values
			Office office = (Office) context.getValueObject();
			OfficeLevel ol = (OfficeLevel) office.getLevel();
			if (null != ol && null != ol.getLevelId()) {

				OfficeLevelView olv = (OfficeLevelView) context
						.getSearchResultBasedOnName(
								OfficeConstants.PARENTOFFICESMAP).getValue();
				officeLogger
						.info("Loading the parent offices for office level with id="
								+ ol.getLevelId()
								+ " and operation is performed by user with id ="
								+ userId);

				// now base on the parentid papulate the parent list
				List parentList = (List) olv.getParentOffices(ol.getLevelId());

				// save this list in context
				OfficeHelper.saveInContext(OfficeConstants.PARENTS, parentList,
						context);

			}

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYLOADFAILED);
		}
	}

	/**
	 * manage initial is called when we want to see the details of any
	 * perticular office
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void manageInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

			Office office = (Office) context.getValueObject();
			UserContext uc = context.getUserContext();
			Short userId = uc.getId();

			officeLogger.info("Loading the master data...");
			// load the master data to context
			officedao.loadOfficeMaster(context, false);

			short level = office.getLevel().getLevelId().shortValue();
			officeLogger.info("Loading the office type list for office type="
					+ level + " and operation is performed by user with id ="
					+ userId);

			if (level == OfficeConstants.HEADOFFICE) {
				OfficeHelper.UpdateOfficeLevelList(context,
						OfficeConstants.HEADOFFICE);

			} else if (level == OfficeConstants.BRANCHOFFICE) {
				OfficeHelper.UpdateOfficeLevelList(context,
						OfficeConstants.BRANCHOFFICE);

			} else {
				OfficeHelper.UpdateOfficeLevelList(context);
			}
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYGETFAILED);
		}

	}

	/**
	 * The function is called for the validation of the updated values ,This
	 * function do the validation for the uniqueness of officeName and shortName
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void updateInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());
			Office office = (Office) context.getValueObject();
			// get the saved small officeSubobject
			OfficeSubObject oso = (OfficeSubObject) ((SearchResults) context
					.getSearchResultBasedOnName(OfficeConstants.OFFICESUBOBJECT))
					.getValue();

			String oldName = oso.getOfficeName();
			String oldShortName = oso.getShortName();
			String newName = office.getOfficeName();
			String newShortName = office.getShortName();
			Short officeId = office.getOfficeId();
			// 1) see if officeName or office Short name has changed if so we
			// have
			// to see for uniqueness of these
			if (!oldName.equalsIgnoreCase(newName)) {

				if (officedao.checkOfficeNameUniqueNess(newName)) {

					officeLogger.error("Updated office name " + newName
							+ "already exist in database ");

					throw new DuplicateOfficeException(
							OfficeConstants.KEYNAMEEXIST);
				}

				// other wise set new name

				officeLogger.info("Updating the office name form " + oldName
						+ " --->" + newName);
				oso.setOfficeName(newName);

			}
			if (!oldShortName.equalsIgnoreCase(newShortName)) {
				if (officedao.checkOfficeShortNameUniqueNess(newShortName)) {

					officeLogger.error("Updated office short name "
							+ newShortName + "already exist in database");

					throw new DuplicateOfficeException(
							OfficeConstants.KEYSHORTNAMEEXIST);
				}
			}

			short oldStatus = oso.getStatus().shortValue();
			short newStatus = office.getStatus().getStatusId().shortValue();

			officeLogger.info("office old status id is =" + oldStatus
					+ "office new status is " + newStatus);
			// fisrt check is we are making office inactive

			// 2) check if status has changed
			if (oldStatus != newStatus) {
				// see if we can chage it or not
				// 2a) check if we are making office inactive
				if (newStatus == OfficeConstants.INACTIVE) {
					if (officedao.isChildPresent(officeId)) {
						throw new OfficeException(
								OfficeConstants.KEYHASACTIVECHILDREN);
					}

					if (officedao.isPersonnelTransferedClosed(officeId)) {
						throw new OfficeException(
								OfficeConstants.KEYHASACTIVEPERSONNEL);

					}
				} else if (newStatus == OfficeConstants.ACTIVE) {

					// if we are making a office active we need to check that

					// 1) that level of this office should be configured

					OfficeLevel ol = office.getLevel();

					if (null != ol) {

						if (!officedao.isLevelConfigured(ol.getLevelId())) {
							throw new OfficeException(
									OfficeConstants.KEYOFFICELEVELNOTCONFIGURED);

						}
					}

					// 2) parent of this office has to be active
					if (null != office.getParentOffice()) {

						Office parentOffice = office.getParentOffice();
						Short parentOfficeId = parentOffice.getOfficeId();

						if (null != parentOfficeId) {

							// get the office for checking the the parent

							OfficeSubObject parentSubObject = officedao
									.getOfficeSubObject(parentOfficeId);

							if (parentSubObject.getStatus().shortValue() == OfficeConstants.INACTIVE) {
								throw new OfficeException(
										OfficeConstants.KEYPARENTNOTACTIVE);
							}
						}
					}

				}

			}

			// 3) check if change in office type is valid

			Short oldOfficeType = oso.getOfficeType();
			Short newOfficeType = office.getLevel().getLevelId();
			officeLogger.info("Office old type is :=" + oldOfficeType
					+ "office new type is " + newOfficeType);

			if (oldOfficeType.shortValue() != newOfficeType.shortValue()) {
				// check are we moving down
				if (!officedao.canChangeOfficeType(newOfficeType, officeId,
						oldOfficeType)) {
					throw new OfficeException(
							OfficeConstants.KEYHASACTIVECHILDREN);

				}

			}

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYUPDATEFAILED);
		}

	}

	public void loadall(Context context) throws SystemException,
			ApplicationException {

		try {

			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

			// this will load the list of all the level into the context
			context.addAttribute(officedao.getAllOfficeLevel());
			List tillOfficeLevel = officedao.getTillBranchOffice();
			OfficeHelper.saveInContext(
					OfficeConstants.OFFICESTILLBRANCHOFFICESLIST,
					tillOfficeLevel, context);
			List branchOffices = officedao.getBranchOffice();
			OfficeHelper.saveInContext(
					OfficeConstants.OFFICESBRANCHOFFICESLIST, branchOffices,
					context);
			context.addAttribute(officedao.getOfficeStatusMaster());
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYGETFAILED);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	public void update(Context context) throws SystemException,
			ApplicationException {

		try {

			super.update(context);

			SearchResults sr = context
					.getSearchResultBasedOnName(OfficeConstants.SEARCHIDLIST);
			if (null != sr) {
				List<OfficeSearch> osList = (List) sr.getValue();
				EventManger.postEvent(Constants.UPDATE, osList,
						SecurityConstants.OFFICECHANGEEVENT);
			}

		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(OfficeConstants.KEYUPDATEFAILED);
		}
	}

}
