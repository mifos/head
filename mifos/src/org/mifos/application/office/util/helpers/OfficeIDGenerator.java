/**

 * OfficeIDGenerator.java    version: 1.0

 

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
package org.mifos.application.office.util.helpers;

import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.framework.exceptions.ApplicationException;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * This class is used to generate the officeGlobel no for the new office for the 
 */
public class OfficeIDGenerator {
	
	/**
	 * This function generate the officeglobel no for the office
	 * @param maxId
	 * @return
	 * @throws ApplicationException
	 */
	public static String generateOfficeId(Short maxId) throws ApplicationException
	{
		String officeGlobelNo="";
        if( null == maxId)
        {
        	//TODO properkey has to be passed
        	throw new OfficeException();
        }
        else
        {
        	try
        	{
        		officeGlobelNo = String.valueOf(maxId.intValue()+1);
        		
        		if(officeGlobelNo.length()>4 )
        		{
                	//TODO properkey has to be passed
                	throw new OfficeException();
                 			
        		}
        		String temp ="";
        		for (int i = officeGlobelNo.length(); i < 4; i++) {
        			
        			temp+="0";
					
				}
        		officeGlobelNo=temp+officeGlobelNo;
        		
        	}
        	catch ( ParseException e)
        	{
            	//TODO properkey has to be passed
            	throw new OfficeException(e);
             		
        	}
        	
        }
        
        return officeGlobelNo;
	}

}
