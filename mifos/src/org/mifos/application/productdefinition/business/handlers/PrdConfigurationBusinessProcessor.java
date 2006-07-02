/**

 * PrdConfigurationBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.productdefinition.business.handlers;

import java.util.Iterator;
import java.util.List;

import org.mifos.application.productdefinition.dao.PrdConfigurationDAO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class contains the business logic related to configuration  of the 
 * products.
 * 
 * @author mohammedn
 * 
 */
public class PrdConfigurationBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * Default Constructor
	 */
	public PrdConfigurationBusinessProcessor() {
	}

	/**
	 * This retrieves the product configuration information
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void getProductTypes(Context context) throws SystemException,
			ApplicationException {
		try {
			List<ProductType> producTypeList = ((PrdConfigurationDAO) getDAO(context
					.getPath())).search(ProductDefinitionConstants.LOANID,
					ProductDefinitionConstants.SAVINGSID);
			context.addAttribute(getSearchResults(ProductDefinitionConstants.PRODUCTTYPELIST,producTypeList));
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ApplicationException(exception);
		}
	}

	/**
	 * This updates the configuration values.
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		try {
			ProductType productType=validateValueObject(context);
			List<ProductType> productTypeList= productType.getProductTypeList();
			for(Iterator<ProductType> iter=productTypeList.iterator();iter.hasNext();) {
				ProductType prdType=iter.next();
				((PrdConfigurationDAO) getDAO(context
						.getPath())).update(prdType);
			}
		} catch(ApplicationException ae) {
			throw ae;
		} catch(SystemException se) {
			throw se;
		} catch(Exception exception) {
			throw new ApplicationException(exception);
		}
	}
	
	/**
	 * This method is used to obtain SearchResults Object by passing the resultName and value
	 * @param resultName
	 * @param value
	 * @return
	 */
	private SearchResults getSearchResults(String resultName,Object value) {
		SearchResults searchResults=new SearchResults();
		searchResults.setResultName(resultName);
		searchResults.setValue(value);
		return searchResults;
	}

	/**
	 * This method is used to get the ValueObject from the Context and checks
	 * for null.
	 * 
	 * @param context
	 * @return MifosLoginValueObject
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private ProductType validateValueObject(Context context)
			throws ApplicationException, SystemException {
		ProductType valueObject = (ProductType) context
				.getValueObject();
		checkForNull(valueObject, ProductDefinitionConstants.PRDINVALID);
		return valueObject;
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
