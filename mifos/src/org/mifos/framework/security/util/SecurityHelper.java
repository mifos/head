/**

 * SecurityHelper.java    version: 1.0

 

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

package org.mifos.framework.security.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.resources.SecurityConstants;

/**
 * This class encupsulate all the funcionality requied by security module to
 * perform its duties i.e getting the list of activityroles etc
 */

public class SecurityHelper {

	/**
	 * This function is used to retrive the all the activities in the system and
	 * the set of roles which includes these activities
	 * 
	 * @return list of ActivityRoles objects
	 * @throws HibernateProcessException
	 */
	public static List<ActivityRoles> getActivities() throws SystemException,
			ApplicationException {
		List<ActivityRoles> activityRolesList = null;
		Session session = null;
		Transaction transaction = null;
		Query quaryActivityRoles = null;
		try {
			session = HibernateUtil.openSession();
			transaction = session.beginTransaction();
			// get the named query
			quaryActivityRoles = session
					.getNamedQuery(NamedQueryConstants.GETACTIVITYROLES);
			activityRolesList = quaryActivityRoles.list();
			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException e) {
			transaction.rollback();
			throw new SecurityException(SecurityConstants.GENERALERROR, e);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return activityRolesList;
	}

	/**
	 * Thsi function returns the PersonRoles object which contains the person
	 * information and the set of all the roles related to that user
	 * 
	 * @param uid
	 *            user id
	 * @return PersonRoles
	 * @throws HibernateProcessException
	 */
	public static Set getUserRoles(short uid) throws SystemException,
			ApplicationException {

		Set roles = null;
		Session session = null;
		Transaction transaction = null;

		Query personRoles = null;

		try {
			session = HibernateUtil.openSession();
			transaction = session.beginTransaction();
			personRoles = session
					.getNamedQuery(NamedQueryConstants.GETPERSONROLES);
			personRoles.setShort("ID", uid);
			List<PersonRoles> lst = personRoles.list();
			transaction.commit();
			if (null != lst && lst.size() > 0) {
				PersonRoles pr = lst.get(0);
				roles = pr.getRoles();
			}
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			transaction.rollback();
			throw new SecurityException(SecurityConstants.GENERALERROR, he);
		}

		finally {
			HibernateUtil.closeSession(session);
		}

		return roles;

	}

	/**
	 * This function is used to get the list of the offices under the given
	 * personnel office under any user at any time
	 * 
	 * @param officeid
	 *            office id of the person
	 * @return List list of the offices under him
	 * @throws HibernateProcessException
	 */
	public static List<OfficeSearch> getPersonnelOffices(Short officeid)
			throws SystemException, ApplicationException {

		HierarchyManager hm = HierarchyManager.getInstance();
		String pattern = hm.getSearchId(officeid) + "%";
		List<OfficeSearch> lst = null;

		Session session = null;
		Transaction transaction = null;
		Query officeSearch = null;
		try {
			session = HibernateUtil.openSession();
			transaction = session.beginTransaction();
			officeSearch = session
					.getNamedQuery(NamedQueryConstants.GETOFFICESEARCH);
			officeSearch.setString(SecurityConstants.PATTERN, pattern);
			lst = officeSearch.list();
			transaction.commit();

		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			transaction.rollback();
			throw new SecurityException(SecurityConstants.GENERALERROR, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return lst;
	}

	/**
	 * This function is used to initialise the the hirerchy manager which is
	 * paert of the security module which keeps the cache of officeid to office
	 * search id so that it can find office under given person without going to
	 * database every time
	 * 
	 * @return List of OfficeSearch objects which contains office is and
	 *         associated searchid
	 * @throws HibernateProcessException
	 */
	public static List<OfficeSearch> getOffices() throws SystemException,
			ApplicationException {

		List<OfficeSearch> lst = null;
		Session session = null;
		Transaction transaction = null;
		Query queryOfficeSearchList = null;

		try {
			session = HibernateUtil.openSession();
			transaction = session.beginTransaction();
			queryOfficeSearchList = session
					.getNamedQuery(NamedQueryConstants.GETOFFICESEARCHLIST);
			lst = queryOfficeSearchList.list();
			transaction.commit();
		} catch (HibernateProcessException e) {
			transaction.rollback();
			throw new SystemException(e);
		} catch (HibernateException he) {
			transaction.rollback();
			throw new SecurityException(SecurityConstants.GENERALERROR, he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return lst;
	}

	/**
	 * This function is used to find the leaf activities in the system as those
	 * are the actual activities user can perform in the system rest are used
	 * for grouping activities only
	 * 
	 * @return List of leafs activity id's
	 * @throws HibernateProcessException
	 */
	public static List<Short> getLeafActivities() throws SystemException,
			ApplicationException {
		RolesPermissionsPersistence rolesPermissionsPersistence = 
			new RolesPermissionsPersistence();

		List<ActivityEntity> activityList = 
			rolesPermissionsPersistence.getActivities();
		List<Short> leafs = new ArrayList<Short>();
		buildLeafItems(activityList, leafs);
		return leafs;
	}

	/**
	 * Internal helper function used to find the leaf activities this function
	 * is once each for the top level activity which has parent as 0
	 * 
	 * @param l
	 *            list of activities in the system
	 * @param leafs
	 *            list of leafs id's
	 */
	private static void buildLeafItems(List<ActivityEntity> l, List<Short> leafs) {
		List<ActivityEntity> li = getChildren(l, Short.valueOf("0"));

		for (int i = 0; i < li.size(); i++) {
			makeLeafItems(l, li.get(i).getId(), leafs);
		}
	}

	/**
	 * This is internal helper function used to find the childern of the given
	 * activity it takes the list of all the activity in the system and find out
	 * which are childern of passed activity
	 * 
	 * @param activities
	 *            List of Activity objects in the system
	 * @param id
	 *            id of the activity whose childern we are trying to find out
	 *            right now
	 * @return
	 */
	private static List<ActivityEntity> getChildren(List<ActivityEntity> activities,
			Short id) {
		List<ActivityEntity> l = new ArrayList<ActivityEntity>();

		/*
		 * for (int i = 0; i < activities.size(); i++) { if
		 * (activities.get(i).getParent().shortValue() == id.shortValue()) {
		 * l.add(activities.get(i)); } }
		 */
		// if id=0 then we are looking for top level activities
		for (int i = 0; i < activities.size(); i++) {
			ActivityEntity parent = activities.get(i).getParent();
			if (id.shortValue() == 0) {

				if (null == parent) {
					l.add(activities.get(i));
				}

			} else {

				if (null != parent) {
					if (parent.getId().shortValue() == id.shortValue()) {
						l.add(activities.get(i));
					}
				}

			}
		}

		return l;
	}

	/**
	 * This function is called recursively for each top level activities in the
	 * system till we reach the leafs this is the function where we find the
	 * leafs
	 * 
	 * @param l
	 *            List of Activity objects in the system
	 * @param id
	 *            id of current activity
	 * @param leafs
	 *            list of all the leafs activity in the system this ia a out
	 *            parameter
	 */
	private static void makeLeafItems(List<ActivityEntity> l, Short id,
			List<Short> leafs) {
		List<ActivityEntity> lst = getChildren(l, id);
		for (int i = 0; i < lst.size(); i++) {
			Short id2 = lst.get(i).getId();
			// check whether it is leaf
			List<ActivityEntity> li = getChildren(l, id2);
			if (li.size() == 0) {
				leafs.add(id2);
			} else {
				makeLeafItems(l, id2, leafs);
			}
		}
	}

}
