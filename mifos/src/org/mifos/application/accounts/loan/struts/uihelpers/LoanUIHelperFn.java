/**

 * LoanUIHelperFn.java    version: xxx

 

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

package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.tags.DateHelper;

/**
 * This class has got helper functions which could be called from jsp as part of jsp2.0 specifications.
 * @author ashishsm
 *
 */
public class LoanUIHelperFn {

	/**
	 * 
	 */
	public LoanUIHelperFn() {
		super();
		
	}
	
	/**
	 * It returns a comma seperated string of sources of fund which it takes from the collection passed to it.  
	 * @param object
	 * @return
	 */
	public static String getSourcesOfFund(Object object) {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside UI helper function getSourcesOfFund");
		StringBuilder stringBuilder=new StringBuilder();
		if(object !=null) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Iterating over the sources of fund object");
			Set fundSet=(Set)object;
			for(Iterator<LoanOfferingFundEntity> iter=fundSet.iterator();iter.hasNext();) {
				Fund fund = iter.next().getFund();
				if(null != fund){
					String fundName = fund.getFundName();
					MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The fund name is " + fundName);
					stringBuilder.append(fundName);
					stringBuilder.append(iter.hasNext()?",":"");
				}
				
				
			}
		}
		return stringBuilder.toString();
	}
	
	public static String getCurrrentDate(Locale locale){
		return DateHelper.getCurrentDate(locale);
	}


}
