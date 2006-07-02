/**

* ClientHelper.java   version: 1.0



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

package org.mifos.application.customer.client.util.helpers;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.mifos.application.customer.center.exception.DuplicateCustomerException;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.dao.ClientCreationDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.DuplicateCustomerGovtIdException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.DAOFactory;

/**
 * @author sumeethaec
 *
 */
public class ClientHelper {
	public static int calculateAge(String userDateOfBirth , Locale locale){

		int day=0;
		int month=0;
		int year=0;
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String userfmt = ((SimpleDateFormat) df).toPattern();
		String separator = DateHelper.getSeparator(userfmt);
		if(userDateOfBirth!=null && !userDateOfBirth.equals("")){
			//added by mohammedn
			String dmy[]=null;
			String format=DateHelper.convertToDateTagFormat(userfmt);
				dmy=DateHelper.getDayMonthYear(userDateOfBirth,format,separator);
				day=Integer.parseInt(dmy[0].trim());
				month=Integer.parseInt(dmy[1].trim());
				year=Integer.parseInt(dmy[2].trim());
		}



		// Create a calendar object with the date of birth
		GregorianCalendar dateOfBirth = new GregorianCalendar(year, month, day);

	    // Todays date
	    Calendar today = Calendar.getInstance();

	    // Get age based on year
	    int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
	    //// System.out.println("-------Age:" + age);
	    int monthDiff = (today.get(Calendar.MONTH)+1) - dateOfBirth.get(Calendar.MONTH);
	    int dayDiff = today.get(Calendar.DAY_OF_MONTH) - dateOfBirth.get(Calendar.DAY_OF_MONTH);
	    //// System.out.println("-------MonthDiff:" + monthDiff);
	    //// System.out.println("-------DayDiff:" + dayDiff);
	    // If this year's birthday has not happened yet, subtract one from age
	    if ( monthDiff < 0) {
	        age--;
	    }
	    else if ( monthDiff ==0 ){
	    	if(dayDiff < 0) {
	        age--;
	    	}
	    }

	    /*if (today.before(dateOfBirth)) {
	        age--;
	    }*/
	    //// System.out.println("-------Age:" + age);
	    return age;

	}
/**
	 * Calls the corresponding method on the DAO and checks for duplicate clients.
	 * @param name
	 * @param dob
	 * @param governmentId
	 * @return
	 */
	public void checkForDuplicacy(String name, Date dob, String governmentId,Integer customerId)throws SystemException,ApplicationException{
		try{
			ClientCreationDAO clientDAO = (ClientCreationDAO)getDAO(org.mifos.application.customer.util.helpers.PathConstants.CLIENT_CREATION);

			if(!ValidateMethods.isNullOrBlank(governmentId)){
				if(clientDAO.checkForDuplicacyOnGovtId(governmentId) == true){
					Object[] values = new Object[1];
					values[0] = governmentId;
					throw new DuplicateCustomerGovtIdException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION,values);
				}
			}
			else{
				if(clientDAO.checkForDuplicacyOnName(name,dob,governmentId,customerId) == true){
					Object[] values = new Object[1];
					values[0] = name;
					throw new DuplicateCustomerException(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,values);
				}
			}
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}

	}
	/**
	 * Returns the DAO corresponding to the path passed to it.
	 * The path should uniquely identify the dependency element in Dependency.xml
	 * DAO is returned using {@link DAOFactory}.
	 * @param path
	 * @return
	 * @throws ResourceNotCreatedException
	 */
	protected DAO getDAO(String path) throws ResourceNotCreatedException{

		return (DAO)DAOFactory.getInstance().get(path);
	}
	public boolean checkGroupStatus(short status , short parentStatus) {
		boolean isNotValid = false ;
		if(status == 2){
			if(parentStatus == 7){
				isNotValid = true;
			}
		}
		else if(status == 3){
			if(parentStatus == 7 || parentStatus == 8){
				isNotValid = true;
			}
		}
		return isNotValid;
	}
}
