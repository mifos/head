/**

 * Authenticator.java    version: 1.0

 

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

package org.mifos.framework.security.authentication;

import java.util.Locale;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.util.valueobjects.Country;
import org.mifos.application.master.util.valueobjects.Language;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.PersonRoles;
import org.mifos.framework.security.util.SecurityHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;

/**
 * Authenticator class performs the authentication for the user
 * 
 * @author rajenders
 */
public class Authenticator {
	private static final Authenticator authenticator = new Authenticator();

	private Authenticator() {
	}

	public static Authenticator getInstance() {
		return authenticator;
	}

	/**
	 * This method validate the user based on his name and password
	 * 
	 * @param name
	 *            User name
	 * @param password
	 *            user password
	 * @return UserContext UserContext object
	 * @throws InvalidUserException
	 *             if user fails to succesfully login to system
	 */
	public UserContext validateUser(String name, String password)
			throws InvalidUserException, ApplicationException, SystemException {
		UserContext userContext = null;
		PersonRoles personRoles = null;
		try {
			// 1) Check if user exist in database
			personRoles = SecurityHelper.getUser(name);
			// 2) Check if user is inactive
			if (Constants.ACTIVE != personRoles.getPersonnelStatus())

				throw new InvalidUserException(LoginConstants.KEYUSERINACTIVE);

			// 3 Check whether user is locked
			if (Constants.LOCKED == personRoles.getLocked())
				throw new InvalidUserException(LoginConstants.KEYUSERLOCKED);

			// to go get user encrypted oassword from the database
			// match with supplied password

			if (EncryptionService.getInstance().verifyPassword(password,
					personRoles.getPassword())) {
				;
			} else {

				SecurityHelper.updateNoOfTries(personRoles,
						(short) (personRoles.getNoOfTries() + 1));
				throw new InvalidUserException(LoginConstants.KEYINVALIDUSER);
			}

			userContext = new UserContext();

			// load user perferences and roles here from personRoles object
			userContext.setId(personRoles.getId());
			userContext.setName(personRoles.getDisplayName());
			userContext.setLevelId(personRoles.getLevelId());
			userContext.setRoles(personRoles.getRoles());
			userContext.setLastLogin(personRoles.getLastLogin());
			userContext.setPasswordChanged(personRoles.getPasswordChanged());
			if (Constants.PASSWORDCHANGED == personRoles.getPasswordChanged()) {
				SecurityHelper.UpdateLastLogin(personRoles);
			}
			Personnel personnel = SecurityHelper.getPersonnel(personRoles.getId());
			SupportedLocales supportedLocales = personnel.getPreferredLocale();
			if (null != supportedLocales) {
				userContext.setLocaleId(supportedLocales.getLocaleId());
				Language lang = supportedLocales.getLanguage();
				Country country = supportedLocales.getCountry();
				if (null != lang && null != country) {
					userContext.setPereferedLocale(new Locale(lang
							.getLanguageShortName(), country
							.getCountryShortName()));
				} else {
					throw new SecurityException(SecurityConstants.GENERALERROR);
				}
			}

			// put officeRelated information
			Office office = personnel.getOffice();
			if (null != office) {
				userContext.setBranchId(office.getOfficeId());
				userContext.setBranchGlobalNum(office.getGlobalOfficeNum());
				userContext.setOfficeLevelId(office.getLevel().getLevelId());
			} else {
				throw new SecurityException(SecurityConstants.GENERALERROR);
			}
			userContext.setMfiLocaleId(Configuration.getInstance().getSystemConfig().getMFILocaleId());
			userContext.setMfiLocale(Configuration.getInstance().getSystemConfig().getMFILocale());
			SecurityHelper.updateNoOfTries(personRoles, (short) 0);

		} catch (EncryptionException e) {
			try {
				SecurityHelper.updateNoOfTries(personRoles,
						(short) (personRoles.getNoOfTries() + 1));
			} catch (HibernateSystemException hexp) {
				throw new SystemException(e);
			}

			throw new InvalidUserException(LoginConstants.KEYINVALIDUSER);
		} catch (HibernateSystemException e) {

			throw new SystemException(e);

		}

		return userContext;
	}

}
