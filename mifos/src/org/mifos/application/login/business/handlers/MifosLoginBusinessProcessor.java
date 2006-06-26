/**

 * MifosLoginBusinessProcessor.java   version: 1.0



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
package org.mifos.application.login.business.handlers;

import java.util.Date;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.login.util.valueobjects.MifosLoginValueObject;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authentication.Authenticator;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.util.PersonRoles;
import org.mifos.framework.security.util.SecurityHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;

/**
 * The class is the Business Processor associated with Login.It extends from
 * MifosBusinessProcessor.
 * 
 * @author mohammedn
 * 
 */
public class MifosLoginBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * Default Constructor
	 */
	public MifosLoginBusinessProcessor() {
		super();
	}
	
	private MifosLogger loginLogger=MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER);

	/**
	 * The method is called when the user enters UserName and Password. The
	 * method checks with Authenticator to check the validity of the user. If
	 * the User is valid, then the UserContext returned by the Authenticator is
	 * set in the Context and the user will be allowed to enter into the application.
	 * If the User Credentials fail, he is not allowed to enter into
	 * the application
	 * 
	 * @param Context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void login(Context context) throws SystemException,
			ApplicationException {
		try {
			MifosLoginValueObject valueObject = validateValueObject(context);
			loginLogger.info("User trying to Login with Login Name " + valueObject.getUserName());
			UserContext userContext = getUserContext(valueObject);
			loginLogger.info("Logging in user with Login Name " + valueObject.getUserName());
			context.setUserContext(userContext);
		}
		catch(ApplicationException ae) {
			throw new ApplicationException(ae.getKey());
		}
		catch(SystemException se) {
			throw new SystemException(se);
		}
		catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * The method is called when the user changes his Password. The password entered by the User is
	 * checked with the password in database. If passwords are equal, the password is
	 * changed to the new password and the password change flag is set to 1 and 
	 * the LastLogin time is null, then it is set to current time.
	 * 
	 * @param Context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		loginLogger.debug("In Update of Login Business Processor");
		try {
			MifosLoginValueObject valueObject = validateValueObject(context);
			String userId = valueObject.getUserId();
			String oldPassword = valueObject.getOldPassword();
			String newpassword = valueObject.getNewPassword();
			if (null == userId || "".equals(userId)) {
				throw new ApplicationException(LoginConstants.SESSIONTIMEOUT);
			}
			PersonRoles personRoles = getPerson(userId);
			byte[] encryptedPassword = getEncryptedPassword(personRoles,
					oldPassword, newpassword);
			personRoles.setPassword(encryptedPassword);
			personRoles.setPasswordChanged(LoginConstants.PASSWORDCHANGEDFLAG);
			if (personRoles.getLastLogin() == null) {
				personRoles.setLastLogin(new Date(System.currentTimeMillis()));
			}
			SecurityHelper.updatePassWord(personRoles);
		} catch (HibernateProcessException e) {
			throw new SystemException();
		} catch(ApplicationException ae) {
			throw new ApplicationException(ae.getKey());
		} catch(SystemException se) {
			throw new SystemException(se);
		} catch(Exception e) {
			throw new ApplicationException(e);
		}	
	}

	/**
	 * This method is used to get the ValueObject from the Context and check if it is
	 * null.
	 * 
	 * @param context
	 * @return MifosLoginValueObject
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private MifosLoginValueObject validateValueObject(Context context)
			throws ApplicationException, SystemException {
		MifosLoginValueObject valueObject = (MifosLoginValueObject) context
				.getValueObject();
		checkForNull(valueObject, LoginConstants.KEYINVALIDUSER);
		return valueObject;
	}

	/**
	 * This method checks with the Authenticator for the validity of the User
	 * Name and Password entered by the User. If the User is Authenticated an UserContext is
	 * returned. If the Authentication fails, an Exception is thrown.
	 * 
	 * @param valueObject
	 * @return UserContext--If Authentication is successful
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private UserContext getUserContext(MifosLoginValueObject valueObject)
			throws ApplicationException, SystemException {
		loginLogger.info("Validating user with Login Name " + valueObject.getUserName());
		UserContext userContext = Authenticator.getInstance().validateUser(
				valueObject.getUserName(), valueObject.getPassword());
		checkForNull(valueObject, LoginConstants.KEYINVALIDUSER);
		return userContext;
	}

	/**
	 * This method is used to get the Person object associated with the UserId.
	 * 
	 * @param userId
	 * @return PersonRoles object associated with the UserId
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private PersonRoles getPerson(String userId) throws ApplicationException,
			SystemException {
		PersonRoles personRoles = null;
		try {
			personRoles = SecurityHelper.getUserWithId(Short.valueOf(userId));
			checkForNull(personRoles, LoginConstants.KEYINVALIDUSER);
		} catch (NumberFormatException e) {
			throw new ApplicationException(LoginConstants.SESSIONTIMEOUT);
		} catch (HibernateProcessException e) {
			throw new SystemException();
		} catch(ApplicationException ae) {
			throw new ApplicationException(ae.getKey());
		} catch(SystemException se) {
			throw new SystemException(se);
		} catch(Exception e) {
			throw new ApplicationException(e);
		}
		return personRoles;
	}

	/**
	 * This method is used to get the Password of the User. It checks the
	 * OldPassword entered by the User with the  Password of the User.
	 * If they are equal, it encrypts the New Password entered by the User.
	 * 
	 * @param person
	 * @param oldPassword
	 * @param newPassword
	 * @return Encrypted New Password.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private byte[] getEncryptedPassword(PersonRoles person, String oldPassword,
			String newPassword) throws ApplicationException, SystemException {
		byte[] password = person.getPassword();
		checkForNull(password, LoginConstants.KEYINVALIDUSER);
		
		byte[] encryptedPassword = null;
		try {
			
			if(EncryptionService.getInstance().verifyPassword(oldPassword,password))
			{
				encryptedPassword = EncryptionService.getInstance().createEncryptedPassword(newPassword);
				checkForNull(encryptedPassword, LoginConstants.KEYINVALIDUSER);
			} else {
				throw new ApplicationException(LoginConstants.INVALIDOLDPASSWORD);
			}
			
		} catch (EncryptionException e) {
			throw new SystemException(e);
		} catch(ApplicationException ae) {
			throw ae;
		}
		catch(SystemException se) {
			throw se;
		}
		catch(Exception e) {
			throw new ApplicationException(e);
		}
		
		return encryptedPassword;
	}

	/**
	 * This method checks, if the given object is null. If Object is null, it
	 * throws an ApplicationException and Logs the message.
	 * 
	 * @param obj to be checked for null
	 * @param exception to be thrown
	 * @param message to be logged
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private void checkForNull(Object obj, String exception)
			throws ApplicationException, SystemException {
		if (null == obj) {
			throw new ApplicationException(exception);
		}
	}
}
