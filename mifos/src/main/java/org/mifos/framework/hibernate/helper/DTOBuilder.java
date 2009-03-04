/**

 * DTOBuilder.java    version: xxx



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
package org.mifos.framework.hibernate.helper;


import java.lang.reflect.Method;

import org.hibernate.type.Type;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.util.DateTimeService;

/**
 * This is returned on a search operation. Search would typically 
 * result in a set of search result objects , these search result 
 * objects would be obtained through hibernate scroll for pagination 
 * in the front end , the associate hibernate session would be held 
 * in this object , a call to close from the front end on this interface 
 * would result in the hibernate session object getting closed.
 */

public class DTOBuilder {

	String dtoPath = "";

	Type[] returnTypes = null;

	String[] aliasNames = null;


	/**
	 * Set the query inputs to build the DTO
	 * @param queryInputs
	 */
	public void setInputs(QueryInputs queryInputs) {
		dtoPath = queryInputs.getPath();
		returnTypes = queryInputs.getTypes();
		aliasNames = queryInputs.getAliasNames();


	}

	/**
	 * Build the DTO object based on the query output and query results
	 * @param dtoData
	 * @return Object
	 */
	public Object buildDTO(Object[] dtoData) throws HibernateSearchException {
		int i = -1;
		String alias = "";
		Method methodToCall = null;
		Class[] parameterTypes = null;
		Object setValues[] = new Object[1];
		Object dtoObject = null;
		try {
			dtoObject = Class.forName(dtoPath).newInstance();

			while (i++ < dtoData.length - 1) {
				alias = "set" + capitalize(aliasNames[i]);
				parameterTypes = getParameterTypes(returnTypes[i].getName());
				methodToCall = dtoObject.getClass().getMethod(alias,
						parameterTypes);
				setValues[0] = getValue(dtoData[i], returnTypes[i].getName());
				if (null != setValues[0]) {
					methodToCall.invoke(dtoObject, setValues[0]);
				}
			}
		}
		catch (Exception e) {
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(
					"error.." + e.getMessage());
			throw new HibernateSearchException(HibernateConstants.BUILDDTO, e);
		}
		finally {
			alias = null;
			methodToCall = null;
			parameterTypes = null;
			setValues = null;
		}
		return dtoObject;

	}

	/**
	 * Capitalize the string passed , should be moved to string util later
	 */
	String capitalize(String s) {
		if (s != null && s.length() != 0) {
			String firstLetter = s.substring(0, 1);
			firstLetter = firstLetter.toUpperCase();
			StringBuffer sb = new StringBuffer(firstLetter);
			sb.append(s.substring(1, s.length()));
			return sb.toString();
		}
		return s;
	}


	/**
	 * Returns the parameter type of the passed data type
	 */
	Class[] getParameterTypes(String dataType) throws Exception {
		Class[] params = new Class[1];
		// compare the datatypes and cast the value accordingly before returning
		// handeled for hibernate - integer , string , long , double , date , short
		try {
			if (dataType.equals("integer"))
				params[0] = Integer.TYPE;
			else if (dataType.equals("string"))
				params[0] = new String("").getClass();
			else if (dataType.equals("long"))
				params[0] = Long.TYPE;
			else if (dataType.equals("date"))
				params[0] = new java.util.GregorianCalendar().getClass()
						.getSuperclass();
			else if (dataType.equals("double"))
				params[0] = Double.TYPE;
			else if (dataType.equals("short"))
				params[0] = Short.TYPE;
			else if (dataType.equals("CHAR"))
				params[0] = Character.TYPE;
			else if (dataType.equals("TIMESTAMP"))
				params[0] = new java.sql.Timestamp(new DateTimeService().getCurrentDateTime().getMillis())
						.getClass();
			else if (dataType.equals("TIME"))
				params[0] = new java.sql.Time(new DateTimeService().getCurrentDateTime().getMillis())
						.getClass();
			else return null;
		}
		catch (Exception e) {
			throw e;

		}

		return params;


	}

	/**
	 * Returns the calendar corrosponding the value passed
	 */
	Object getValue(Object value, String dataType) {
		if (dataType.equals("date")) {
			java.util.Date dateVal = (java.util.Date) value;
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.setTime(dateVal);
			return cal;
		}
		return value;
	}

	public void cleanUp() {
		dtoPath = null;
		returnTypes = null;
		aliasNames = null;
	}

}
