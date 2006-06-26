/**

 * RolesandPermissionDAO.java    version: 1.0

 

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
package org.mifos.application.rolesandpermission.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.rolesandpermission.exceptions.RoleAndPermissionException;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionHelper;
import org.mifos.application.rolesandpermission.util.valueobjects.Activity;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivitiesKey;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;

/**
 * RolesandPermissionDAO is used for data retrival for the roleandpermission
 * module it has functions for populating ui from master data
 * 
 * @author rajenders
 * 
 */
public class RolesandPermissionDAO extends DAO {
	
	
	MifosLogger rolesAndPermissionLogger = MifosLogManager
	.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER);


	/**
	 * This function is used to get list of all the roles in the system For
	 * displaying the list in UI
	 * 
	 * @return it returns the list of all the roles in the system
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Role> getRoles() throws SystemException, ApplicationException {
		List<Role> rolesList = null;
		Session session = null;
		Transaction transaction = null;
		Query quaryRoleList = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			// get the named query
			
			rolesAndPermissionLogger.info("Executing the named query ="+NamedQueryConstants.GETROLES);
			quaryRoleList = session.getNamedQuery(NamedQueryConstants.GETROLES);
			rolesList = quaryRoleList.list();
			transaction.commit();
		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		}

		finally {
			HibernateUtil.closeSession(session);
		}
		if (null != rolesList && rolesList.size() > 0) {
			return rolesList;
		} else {
			throw new ApplicationException(
					RolesAndPermissionConstants.KEYROLENOTEXIST);
		}

	}

	/**
	 * This function gets the list of all the activities in the system
	 * 
	 * @return returns the list of the all the activities in the system
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Activity> getActivities() throws SystemException,
			ApplicationException {
		List<Activity> activitiesList = new ArrayList<Activity>();
		Session session = null;
		Transaction transaction = null;
		Query quaryActivitiesList = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			rolesAndPermissionLogger.info("Executing the named query ="+NamedQueryConstants.GETACTIVITIES);
			
			quaryActivitiesList = session
					.getNamedQuery(NamedQueryConstants.GETACTIVITIES);
			activitiesList = quaryActivitiesList.list();
			for(Activity activity : activitiesList){
				activity.getActivityNameLookupValues().getLookUpValueLocaleSet();
				activity.getDescriptionLookupValues().getLookUpValueLocaleSet();
			}
			transaction.commit();
		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return activitiesList;

	}

	/**
	 * Thsi function gets the perticular role based on the specified id set into
	 * the role object
	 * 
	 * @param role
	 *            role object with id set
	 * @return role object with all the values set
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public Role getRole(Short roleId) throws ApplicationException,
			SystemException {

		Role role_ = null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();

			rolesAndPermissionLogger.info("Getting the role with id" + roleId);
			role_ = (Role) session.get(Role.class, roleId);
			if (null != role_) {
				for (Iterator iter = role_.getActivities().iterator(); iter
						.hasNext();) {
					RoleActivity element = (RoleActivity) iter.next();
					((RoleActivitiesKey) element.getCompositeKey())
							.getActivityId();
				}
			}
			else
			{
				throw new RoleAndPermissionException(RolesAndPermissionConstants.KEYROLEDELETEDBYOTHERUSER);
			}
			transaction.commit();
		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return role_;

	}

	/**
	 * Thsi function check whether a role with the given name exist or not
	 * 
	 * @param RoleName
	 *            name of the role
	 * @return whether a role exist or not
	 * @throws ApplicationException
	 * @throws SystemException
	 */

	public boolean roleExists(String RoleName) throws ApplicationException,
			SystemException {

		List rolesList = null;
		Session session = null;
		Transaction transaction = null;
		Query queryGetRole = null;
		try {
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			rolesAndPermissionLogger.info("Executing the named query ="+NamedQueryConstants.GETROLE);
			
			queryGetRole = session.getNamedQuery(NamedQueryConstants.GETROLE)
					.setString(RolesAndPermissionConstants.ROLENAME, RoleName);
			rolesList = queryGetRole.list();
			transaction.commit();

		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		if (null != rolesList && rolesList.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This function create a new role with the passed role object
	 * 
	 * @param role
	 *            role object which we want to create
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void create(Context context) throws ApplicationException,
			SystemException {
		Session session = null;
		Transaction transaction = null;
		try {
			Role role = (Role) context.getValueObject();
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			
			rolesAndPermissionLogger.info("Creating the role with name ="+role.getName());

			// Description for the role is required field so save it same as
			// role name
			role.setDescription(role.getName());

			Set roleActivitySet = role.getActivities();
			session.save(role);
			java.util.Iterator iter = roleActivitySet.iterator();

			// saving the each activity individually
			while (iter.hasNext()) {
				RoleActivity roleActivity = (RoleActivity) iter.next();
				RoleActivitiesKey activityKey = roleActivity.getCompositeKey();
				activityKey.setRoleId(role.getId());
				session.save(roleActivity);
			}
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
	 * This function deletes a specified role fronm the database with the given
	 * role object with id set
	 * 
	 * @param context
	 *            Context object with id set which we want to delete
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void delete(Context context) throws ApplicationException,
			SystemException {
		Session session = null;
		Transaction transaction = null;
		try {
			Role role = (Role) context.getValueObject();
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			rolesAndPermissionLogger.info("Deleting  the role with name ="+role.getName() + " and id ="+role.getId());
			// get the actual role object before deleting it
			role = (Role) session.get(Role.class, role.getId());
			session.delete(role);
			transaction.commit();
		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (StaleObjectStateException sse) {
			throw new ConcurrencyException(sse);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function updates the given role
	 * 
	 * @param Context
	 *            context object with new values set
	 * @throws HibernateProcessException
	 */
	public void update(Context context) throws ApplicationException,
			SystemException {
		/*
		 * The main logic we are follwing is that if role is updated then some
		 * activities from the role might have been deleted and some might have
		 * been added so we are deleted those activities which are unselected by
		 * user and we are adding which are newly selected by the user
		 */

		Session session = null;
		Transaction transaction = null;
		try {
			Role role = (Role) context.getValueObject();

			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Role oldRole = new Role();
			oldRole.setId(role.getId());
			oldRole = getRole(oldRole.getId());
			
			rolesAndPermissionLogger.info("Updating  the role with name ="+role.getName() + " and id ="+role.getId());
			
			Set oldActivitySet = oldRole.getActivities();
			// iterate and make a list of id
			Set currentActivitySet = role.getActivities();

			// Coverting sets to arrylist
			ArrayList oldActivityIdList = RolesAndPermissionHelper
					.convertSetToArrayList(oldActivitySet);
			ArrayList newActivityIdList = RolesAndPermissionHelper
					.convertSetToArrayList(currentActivitySet);

			// deleting activities which needs to be deleted

			for (Iterator iter = currentActivitySet.iterator(); iter.hasNext();) {
				RoleActivity roleActivity = (RoleActivity) iter.next();
				RoleActivitiesKey activityKey = roleActivity.getCompositeKey();
				activityKey.setRoleId(role.getId());
				if (!oldActivityIdList.contains(activityKey.getActivityId())) {
					session.save(roleActivity);
				}

			}
			// adding the activities which are being added by user to this role
			for (Iterator iter = oldActivitySet.iterator(); iter.hasNext();) {
				RoleActivity roleActivity = (RoleActivity) iter.next();
				RoleActivitiesKey activityKey = roleActivity.getCompositeKey();
				activityKey.setRoleId(role.getId());
				if (!newActivityIdList.contains(activityKey.getActivityId())) {
					session.delete(roleActivity);
				}

			}
			// check if name is being also updated if so we need to update the
			// name also
			if (!role.getName().trim().equalsIgnoreCase(
					oldRole.getName().trim())) {

				role.setActivities(null);
				role.setVersionNo(oldRole.getVersionNo());
				session.update(role);
			}
			transaction.commit();
		} catch (HibernateProcessException e) {

			transaction.rollback();
			throw new SystemException(e);
		} catch (StaleObjectStateException sse) {
			throw new ConcurrencyException(sse);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This function checks whether given role is assigned to any user or not if role is assigned to a user then
	 * we can not delete that role 
	 * @param roleId
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public boolean isRoleAssignedToPersonnel(Short roleId)
			throws ApplicationException, SystemException {
		Session session = null;
		Query isRoleAssigned = null;
		Integer noOfRoles = null;
		try {

			session = HibernateUtil.getSession();
			rolesAndPermissionLogger.info("Executing the named query ="+NamedQueryConstants.GETROLEWITHID + 
					" with parameter roleId = "+roleId );
			isRoleAssigned = session.getNamedQuery(
					NamedQueryConstants.GETROLEWITHID).setShort(RolesAndPermissionConstants.ROLEID,
					roleId);
			noOfRoles = (Integer) isRoleAssigned.uniqueResult();

		} catch (HibernateProcessException e) {

			throw new SystemException(e);
		} catch (StaleObjectStateException sse) {
			throw new ConcurrencyException(sse);
		} catch (HibernateException he) {
			throw new ApplicationException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return (null != noOfRoles && noOfRoles > 0) ? true : false;

	}
}