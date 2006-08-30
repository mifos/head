/**

 * RolesandPermissionBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.rolesandpermission.business.handlers;

import java.util.List;
import java.util.Set;

import org.mifos.application.rolesandpermission.dao.RolesandPermissionDAO;
import org.mifos.application.rolesandpermission.exceptions.RoleAlreadyExist;
import org.mifos.application.rolesandpermission.exceptions.RoleAndPermissionException;
import org.mifos.application.rolesandpermission.util.helpers.RoleSubObject;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionHelper;
import org.mifos.application.rolesandpermission.util.valueobjects.Activity;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.EventManger;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class encapsulate all the business logic related to the managning roles
 * and permission module i.e. functionality related to creation updation of
 * roles and permission
 */
public class RolesandPermissionBusinessProcessor extends MifosBusinessProcessor {

	MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER);

	/**
	 * This function get called when user select to delete a perticular role and
	 * press submit button it takes a confirmation screen
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#preview(org.mifos.framework.util.valueobjects.Context)
	 */
	public void preview(Context context) throws SystemException,
			ApplicationException {
		try {
			Role role = (Role) context.getValueObject();

			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());

			logger.info("Getting the role with id = " + role.getId());
			role = dao.getRole(role.getId());
			// getting role searchresult and setting inot the context
			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.ROLE, role, context);
			super.preview(context);
		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {

			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLEDELETIONFAILED);

		}

	}

	/**
	 * This function is called when user wants to create a new role this
	 * function has the logic to disply the role templete to the user
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#load(org.mifos.framework.util.valueobjects.Context)
	 */
	public void load(Context context) throws SystemException,
			ApplicationException {

		try {
			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());

			List<Activity> l = dao.getActivities();
			logger.info("Size of activities list is =" + l.size());

			StringBuilder sb = RolesAndPermissionHelper.getTempleteBuffer(l,context.getUserContext().getLocaleId());
			// getting the templete and setting it to the context
			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.BUFF, sb.toString(), context);

			/*
			 * We are storing the list of activities also in the context so that
			 * we need not go to db to get them every time
			 */

			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.ACTIVITYLIST, l, context);

		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLETEMPLETELOADFAILED);
		}

	}

	/**
	 * This function get called when user wnat to see/edit a perticular role
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */

	public void get(Context context) throws SystemException,
			ApplicationException {

		try {
			Role role = (Role) context.getValueObject();
			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());
			List l = dao.getActivities();
			logger.info("Size of activities list is =" + l.size());

			role = dao.getRole(role.getId());

			// put the roleSubobject in context for using it later
			RoleSubObject rso = new RoleSubObject(role.getId(), role.getName(),
					role.getVersionNo());
			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.ROLESUBOBJECT, rso, context);

			// getting the templete for the perticular role and setting it to
			// the context
			// later used for showing in ui
			logger.debug("building the role templete ");
			StringBuilder sb = RolesAndPermissionHelper.getTempleteBuffer(l,
					role.getActivities(),context.getUserContext().getLocaleId());

			// getting the templete and setting it to the context

			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.BUFF, sb.toString(), context);

			/*
			 * We are storing the list of activities also in the context so that
			 * we need not go to db to get them every time
			 */

			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.ACTIVITYLIST, l, context);
		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLETEMPLETELOADFAILED, e);
		}

	}

	/**
	 * This function do all the validation before creating a role
	 * 
	 * @param context
	 *            context object
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void createInitial(Context context) throws SystemException,
			ApplicationException {
		
		Role role=null;
		List l =null;

		try {
			 role = (Role) context.getValueObject();
			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());

			 l = dao.getActivities();
			logger.info("Veryfying the entered role information with name ="
					+ role.getName());
			// Bug 26819 added check for activities
			Set activities = role.getActivities();
			if (null != activities) {
				logger.info("No of activities selected bu yser is ="
						+ activities.size());

				if (!(activities.size() > RolesAndPermissionConstants.ZERO)) {

					throw new RoleAndPermissionException(
							RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
				}
			} else {

				throw new RoleAndPermissionException(
						RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);

			}

			// checking for the duplication
			if (dao.roleExists(role.getName())) {
				// bug 26790 -- Added code to keep the activities selected

				StringBuilder sb = RolesAndPermissionHelper.getTempleteBuffer(
						l, role.getActivities(),context.getUserContext().getLocaleId());

				RolesAndPermissionHelper.saveInContext(
						RolesAndPermissionConstants.BUFF, sb.toString(),
						context);

				throw new RoleAlreadyExist(
						RolesAndPermissionConstants.KEYROLEALREADYEXIST);
			}
			if (!(role.getActivities().size() > RolesAndPermissionConstants.ZERO)) {
				throw new RoleAndPermissionException(
						RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
			}
		}catch( RoleAndPermissionException re)
		{
			
			// bug 26790 -- Added code to keep the activities selected

			StringBuilder sb = RolesAndPermissionHelper.getTempleteBuffer(
					l, role.getActivities(),context.getUserContext().getLocaleId());

			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.BUFF, sb.toString(),
					context);
			
			throw re;
			
		}
		catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLECREATIONFAILED);
		}

	}

	/**
	 * This fuction is used to create a perticular role ,User needs to select
	 * the activities he wants to associates with the role
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#create(org.mifos.framework.util.valueobjects.Context)
	 */

	public void create(Context context) throws SystemException,
			ApplicationException {

		try {
			Role role = (Role) context.getValueObject();
			logger.info("Creating the role with the name = " + role.getName());
			super.create(context);
			// sending the event to security
			EventManger.postEvent(Constants.CREATE, role,
					Constants.ROLECHANGEEVENT);
		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLECREATIONFAILED);
		}

	}

	/**
	 * This function is used to do the validation before updating a role
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context) throws SystemException,
			ApplicationException {
		
		Role role=null;
		List l=null;

		try {
			 role = (Role) context.getValueObject();
			// Bug 26819 added check for activities
			Set activities = role.getActivities();
			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());

			 l = dao.getActivities();

			logger
					.info("Veryfying the information enter by user to update the role ");

			if (null != activities) {

				if (!(activities.size() > RolesAndPermissionConstants.ZERO)) {

					throw new RoleAndPermissionException(
							RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIESFORUPDATE);
				}
			} else {
				throw new RoleAndPermissionException(
						RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIESFORUPDATE);

			}

			logger.debug("Role new  name is " + role.getName());
			RoleSubObject oldValue = (RoleSubObject) ((SearchResults) context
					.getSearchResultBasedOnName(RolesAndPermissionConstants.ROLESUBOBJECT))
					.getValue();
			logger.debug("Role old name is " + oldValue.getRoleName());
			if (!role.getName().trim().equalsIgnoreCase(
					oldValue.getRoleName().trim())) {

				// checking for the duplication
				if (dao.roleExists(role.getName())) {
					// bug 26790 -- Added code to keep the activities selected
					// rebuild the templete

					StringBuilder sb = RolesAndPermissionHelper
							.getTempleteBuffer(l, role.getActivities(),context.getUserContext().getLocaleId());

					RolesAndPermissionHelper.saveInContext(
							RolesAndPermissionConstants.BUFF, sb.toString(),
							context);

					throw new RoleAlreadyExist(
							RolesAndPermissionConstants.KEYROLEALREADYEXIST);
				}

			}
		}
		catch( RoleAndPermissionException re)
		{
			
			// bug 26790 -- Added code to keep the activities selected

			StringBuilder sb = RolesAndPermissionHelper.getTempleteBuffer(
					l, role.getActivities(),context.getUserContext().getLocaleId());

			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.BUFF, sb.toString(),
					context);
			
			throw re;
			
		}
		catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLECREATIONFAILED);
		}

	}

	/**
	 * Update method is called when the user updates the perticular role in the
	 * system
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		try {
			Role role = (Role) context.getValueObject();
			// keep back up of this object so that we can update cache
			Role backupRole = RolesAndPermissionHelper.makeCopy(role);
			logger.info("Updating the role name to =" + role.getName()
					+ " and activies size is =" + role.getActivities().size());
			super.update(context);
			/*
			 * we have cached all the roles in the AuthrizationManager component
			 * of the security we need to send the notification to that also so
			 * that AuthrizationManager can update it's cache
			 */

			logger.info("Updating the role cache in authorization manager ...");
			EventManger.postEvent(Constants.UPDATE, backupRole,
					Constants.ROLECHANGEEVENT);

		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLEUPDATIONFAILED);
		}

	}

	/**
	 * This function shows the initial screen with all the roles in the system
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#manage(org.mifos.framework.util.valueobjects.Context)
	 */
	public void manage(Context context) throws SystemException,
			ApplicationException {

		try {
			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());
			List<Role> lst = dao.getRoles();

			if (null != lst) {
				logger.info("Loading the role list with size = " + lst.size());

			}
			// save the list in context
			RolesAndPermissionHelper.saveInContext(
					RolesAndPermissionConstants.ROLES, lst, context);
		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLELOADINGFAILED);
		}

	}

	public void deleteInitial(Context context) throws SystemException,
			ApplicationException {

		try {

			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());
			Role role = (Role) context.getValueObject();

			// check whether the role is assigned to any personnel
			// if so then we can not delete the role

			if (null != role.getId()) {
				if (dao.isRoleAssignedToPersonnel(role.getId())) {
					throw new RoleAndPermissionException(
							RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL);
				}

			} else {
				throw new RoleAndPermissionException(
						RolesAndPermissionConstants.KEYROLEDELETIONFAILED);
			}
		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLEDELETIONFAILED);
		}

	}

	/**
	 * Function to delete a perticular role from the system
	 * 
	 * @param Context
	 *            object
	 * @throws SystemException
	 *             ,ApplicationException
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#delete(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	public void delete(Context context) throws SystemException,
			ApplicationException {

		try {

			RolesandPermissionDAO dao = (RolesandPermissionDAO) getDAO(context
					.getPath());
			Role role = (Role) context.getValueObject();

			// get the object for updating role cache
			Role backupRole = dao.getRole(role.getId());

			super.delete(context);
			/*
			 * we have cached all the roles in the AuthrizationManager component
			 * of the security we need to send the notification to that also so
			 * that AuthrizationManager can update it's cache
			 */

			EventManger.postEvent(Constants.DELETE, backupRole,
					Constants.ROLECHANGEEVENT);

		} catch (ApplicationException ae) {
			throw ae;
		}

		catch (SystemException se) {
			throw se;

		} catch (Exception e) {
			throw new RoleAndPermissionException(
					RolesAndPermissionConstants.KEYROLEDELETIONFAILED);
		}

	}
}
