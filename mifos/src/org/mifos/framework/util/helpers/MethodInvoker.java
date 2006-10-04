/**

 * MethodInvoker.java    version: 1.0

 

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

package org.mifos.framework.util.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.MethodInvocationException;
import org.mifos.framework.exceptions.SystemException;

/**
 * This is a helper class used to invoke methods by reflection.It has two variants of the same method:
 * One which throws exceptions if the specified method is not found 
 * and another which swallows the exception if the method is not found 
 * but both of these methods do throw the exception thrown by the underlying 
 * method which is invoked. 
 */
public class MethodInvoker {
	
/**
 * Invokes the method specified on the object. It throws <code>MethodInvocationException</code> if the method we are trying to invoke is not available or inaccessible.
 * @param targetObject - Object on which method is to be invoked.
 * @param methodName   - Name of the method to be called.
 * @param parametersToBePassed - Array of parameters to be passed.
 * @param parameterTypes - Array of parameter types.
 * @return - Returns the object returned by the method which is invoked.
 * @throws SystemException
 * @throws ApplicationException
 */
public static Object invoke(Object targetObject,String methodName,Object[] parametersToBePassed,Class... parameterTypes) throws SystemException,ApplicationException{
		
		Method method = null;
		Object invocationResult = null;
		
		
		try {
			
			method = targetObject.getClass().getMethod(methodName, parameterTypes);
			invocationResult=	method.invoke(targetObject, parametersToBePassed);
		} catch (SecurityException se) {
			se.printStackTrace();
			throw new MethodInvocationException(se);
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
			throw new MethodInvocationException(nsme);
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			throw new MethodInvocationException(iae);
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			throw new MethodInvocationException(iae);
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
			Throwable cause = ite.getCause();
			if(cause instanceof ApplicationException){
				throw  (ApplicationException)cause;
			}else if (cause instanceof SystemException){
				throw  (SystemException)cause;
			}else{
				throw new SystemException(cause);
			}
		}
		return invocationResult;
		
	}


/**
 * Invokes the method specified on the object. 
 * Unlike invoke it does not throw any exception even if the method 
 * we are trying to invoke is not available or inaccessible.
 * @param targetObject - Object on which method is to be invoked.
 * @param methodName   - Name of the method to be called.
 * @param parametersToBePassed - Array of parameters to be passed.
 * @param parameterTypes - Array of parameter types.
 * @return - Returns the object returned by the method which is invoked.
 * @throws SystemException
 * @throws ApplicationException
 */
public static Object invokeWithNoException(Object targetObject,
	String methodName,Object[] parametersToBePassed,Class... parameterTypes)
throws SystemException,ApplicationException {
	
	Method method = null;
	Object invocationResult = null;
	
	
	try {
		method = targetObject.getClass().getMethod(methodName, parameterTypes);
		//MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("target object is of class " + targetObject.getClass());
	//	MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("parameters are " + parametersToBePassed);
	//	MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("method name is  " + methodName);
		invocationResult=	method.invoke(targetObject, parametersToBePassed);
	} catch (SecurityException se) {
		se.printStackTrace();
	} catch (NoSuchMethodException nsme) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error("No method with "+ methodName+" was found.Either the method is not present or the specified parameters dont match");
	} catch (IllegalArgumentException iae) {
		iae.printStackTrace();
	} catch (IllegalAccessException iae) {
		iae.printStackTrace();
	} catch (InvocationTargetException ite) {
		ite.printStackTrace();
		Throwable cause = ite.getCause();
		if(cause instanceof ApplicationException){
			throw  (ApplicationException)cause;
		}else if (cause instanceof SystemException){
			throw  (SystemException)cause;
		}else{
			throw new SystemException(cause);
		}
	}
	return invocationResult;
	
}

}
